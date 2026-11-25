package com.lokahe.androidmvvm.viewmodels

import android.webkit.CookieManager
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.lokahe.androidmvvm.R
import com.lokahe.androidmvvm.data.local.UserManager
import com.lokahe.androidmvvm.emptyNull
import com.lokahe.androidmvvm.str
import com.lokahe.androidmvvm.toast
import com.lokahe.androidmvvm.utils.Utils
import com.lokahe.database.model.Person
import com.lokahe.database.repository.DataBaseRepository
import com.lokahe.domain.model.AuthEntryOutcome
import com.lokahe.domain.model.IdentityInfo
import com.lokahe.domain.repository.AuthRepository
import com.lokahe.domain.repository.UserRepository
import com.lokahe.log.L
import com.lokahe.store.repository.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

@HiltViewModel
class MainViewModel @Inject constructor(
    repository: PreferencesRepository,
    private val dbRepository: DataBaseRepository,
    authRepository: AuthRepository,
    userRepository: UserRepository,
    userManager: UserManager
) : BaseViewModel(repository, authRepository, userRepository, userManager) {

    init {
        // 2. Trigger the check immediately when ViewModel is created (App Start)
        autoSignIn()
    }

    // Unified email-entry in-flight state (verification email sent, waiting
    // for the code). The entry password is reused for account creation or
    // silently set on login.
    private var pendingEntryEmail: String? = null
    private var pendingEntryPassword: String? = null

    /** True while the flow is waiting for the emailed code (drives the UI step). */
    private val _entryCodeSent = MutableStateFlow(false)
    val entryCodeSent: StateFlow<Boolean> = _entryCodeSent

    private fun autoSignIn() {
        viewModelScope.launch {
            // Get the saved token
            // Note: We use .firstOrNull() to get the current value from the Flow once
            getAccessToken()?.emptyNull()?.let { token ->
                // Verify with server
                authRepository.verifyToken(token).cole()
                    .onFailure {
                        if (it.code == 401 || it.code == 403) {
                            refreshToken()
                        } else {
                            toast(it.message)
                            userManager.clearUser()
                        }
                    }
                    .onException { toast(it.message ?: R.string.unkown_error.str()) }
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            val refreshToken = userManager.currentSession()?.refreshToken
            getAccessToken()?.emptyNull()?.let { token ->
                authRepository.signOut(token, refreshToken ?: "").cole()
            }
            userManager.clearUser()
        }
    }

    fun signIn(login: String, password: String) {
        viewModelScope.launch {
            authRepository.emailEntry(login.trim(), password).cole()
                .onSuccess { outcome ->
                    L.d("emailEntry") { "outcome: $outcome" }
                    when (outcome) {
                        is AuthEntryOutcome.LoggedIn -> {
                            pendingEntryEmail = null
                            pendingEntryPassword = null
                            _entryCodeSent.value = false
                            save(outcome.auth)
                        }
                        AuthEntryOutcome.CodeSent -> {
                            pendingEntryEmail = login.trim()
                            pendingEntryPassword = password
                            _entryCodeSent.value = true
                            toast(R.string.code_sent_to_generic.str())
                        }
                    }
                }
                .onFailure { toast(it.message) }
                .onException { toast(it.message ?: R.string.unkown_error.str()) }
        }
    }

    /**
     * Confirm the emailed code: logs in (registered) or creates the account
     * (unregistered; the entry password is reused to create it).
     */
    fun confirmEntryCode(code: String) {
        val email = pendingEntryEmail ?: return
        viewModelScope.launch {
            authRepository.emailEntryConfirm(email, code, pendingEntryPassword?.takeIf { it.isNotEmpty() })
                .cole()
                .onSuccess {
                    pendingEntryEmail = null
                    pendingEntryPassword = null
                    _entryCodeSent.value = false
                    save(it)
                }
                .onFailure { toast(it.message) }
                .onException { toast(it.message ?: R.string.unkown_error.str()) }
        }
    }

    /** Back from the code step to the email+password box. */
    fun resetEntryCodeStep() {
        _entryCodeSent.value = false
    }

    fun requestPasswordReset(email: String) {
        viewModelScope.launch {
            authRepository.requestPasswordReset(email).cole()
                .onSuccess {
                    // Anti-enumeration: the backend always answers 202 even for
                    // unknown emails, so this hint is conditional on format only.
                    toast(R.string.password_reset_sent.str())
                }
                .onFailure { toast(it.message) }
                .onException { toast(it.message ?: R.string.unkown_error.str()) }
        }
    }

    // ---- GitHub OAuth (in-app WebView, intercepts the gateway callback JSON) ----

    /** Launch point handed to the WebView screen. */
    fun githubAuthorizeUrl(): String = authRepository.buildAuthorizeUrl("github")

    /** Called by the WebView screen with the raw JSON captured at the callback URL. */
    fun onGitHubCallback(body: String) {
        L.d("onGitHubCallback") { "body: $body" }
        val auth = authRepository.parseOAuthResponse(body)
        if (auth == null) {
            toast(R.string.unkown_error.str())
            return
        }
        viewModelScope.launch { save(auth) }
    }

    /** Clear GitHub cookies so the next sign-in can pick a different account. */
    fun clearGitHubCookies() {
        CookieManager.getInstance().removeAllCookies(null)
        CookieManager.getInstance().flush()
    }

    fun updateAvatar(url: String) {
        viewModelScope.launch {
            val token = getAccessToken() ?: return@launch
            userRepository.updateAvatar(token, url).cole()
                .onSuccess { profile ->
                    // Persist through the same path as login so the color seed
                    // is recalculated from the new avatar.
                    userManager.userFlow.firstOrNull()?.let { current ->
                        userManager.saveUser(current.copy(profile = profile))
                    }
                }
                .onFailure { toast(it.message) }
                .onException { toast(it.message ?: R.string.unkown_error.str()) }
        }
    }

    // ---- Avatar picker: avatar candidates from the connected platforms ----

    private val _identityAvatars = MutableStateFlow<List<IdentityInfo>>(emptyList())
    val identityAvatars: StateFlow<List<IdentityInfo>> = _identityAvatars

    /** Load platform avatars for the picker dialog; called when it opens. */
    fun loadIdentityAvatars() {
        viewModelScope.launch {
            val token = getAccessToken() ?: return@launch
            userRepository.fetchIdentities(token).cole()
                .onSuccess { list -> _identityAvatars.value = list.filter { it.avatarUrl != null } }
                .onFailure { toast(it.message) }
                .onException { toast(it.message ?: R.string.unkown_error.str()) }
        }
    }

    fun updateUserProfile(
        phone: String,
        address: String,
        birthDate: String,
        description: String,
        gender: String
    ) {
        viewModelScope.launch {
            val userId = getUser()?.id ?: ""
            val token = getAccessToken()
            if (userId.isEmpty() || token.isNullOrEmpty()) return@launch
        }
    }

    // State for the list of persons (Database)
    private val _persons = mutableStateOf<List<Person>>(emptyList())
    val persons: State<List<Person>> = _persons

    init {
        fetchPersons()
    }

    private fun fetchPersons() {
        viewModelScope.launch {
            _persons.value = dbRepository.getAllPersons()
        }
    }

    fun addPerson(person: Person) {
        viewModelScope.launch {
            dbRepository.insertPerson(person)
            fetchPersons()
        }
    }

    fun addRandomPerson() {
        addPerson(Utils.randomPerson())
    }
}
