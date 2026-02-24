package com.lokahe.androidmvvm.viewmodels

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import androidx.lifecycle.viewModelScope
import com.lokahe.androidmvvm.AppDialog
import com.lokahe.androidmvvm.MyApplication
import com.lokahe.androidmvvm.R
import com.lokahe.androidmvvm.curSecond
import com.lokahe.androidmvvm.data.auth.GoogleAuther
import com.lokahe.androidmvvm.data.local.UserManager
import com.lokahe.androidmvvm.data.models.Person
import com.lokahe.androidmvvm.data.models.auth.GoogleAuth
import com.lokahe.androidmvvm.data.models.supabase.AuthResponse
import com.lokahe.androidmvvm.data.models.supabase.User
import com.lokahe.androidmvvm.data.remote.Api
import com.lokahe.androidmvvm.data.remote.Api.PAGE_SIZE
import com.lokahe.androidmvvm.data.repository.DataBaseRepository
import com.lokahe.androidmvvm.data.repository.HttpRepository
import com.lokahe.androidmvvm.data.repository.PreferencesRepository
import com.lokahe.androidmvvm.emptyNull
import com.lokahe.androidmvvm.str
import com.lokahe.androidmvvm.toast
import com.lokahe.androidmvvm.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class MainViewModel @Inject constructor(
    private val prefRepository: PreferencesRepository,
    private val dbRepository: DataBaseRepository,
    private val httpRepository: HttpRepository,
    private val userManager: UserManager,
    private val googleAuther: GoogleAuther
) : BaseViewModel(prefRepository, userManager) {

    init {
        // 2. Trigger the check immediately when ViewModel is created (App Start)
        autoSignIn()
    }

    // Expose the user as State for Compose
    val currentUser by lazy {
        userManager.userFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    }

    // Check if logged in based on if user data exists
    val isSignedIn by lazy {
        currentUser.map { it != null }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
    }

    private val _processing = mutableStateOf(false)
    val processing: State<Boolean> = _processing
    private fun onProcessing() {
        _processing.value = true
        showDialog(AppDialog.Loading)
    }

    private fun unProcessing() {
        _processing.value = false
        dismissDialog(AppDialog.Loading)
    }

    private fun save(auth: AuthResponse) {
        save(auth.accessToken, auth.expiresAt, auth.refreshToken, auth.user)
    }

    private fun save(
        token: String?,
        expiresAt: Long?,
        refreshToken: String?,
        user: User? = null
    ) = viewModelScope.launch {
        userManager.saveToken(token, expiresAt, refreshToken)
        user?.let { userManager.saveUser(it.fetchProfile(token)) } ?: token?.let { tk ->
            httpRepository.varifyToken(tk)
                .onSuccess { userManager.saveUser(it.fetchProfile(token)) }
                .onFailure { toast(it.message) }
                .onException { toast(it.message ?: R.string.unkown_error.str()) }
        }
        dismissDialog(AppDialog.SignIn)
    }

    private suspend fun User.fetchProfile(token: String?): User {
        token?.let { tk -> httpRepository.fetchProfileById(tk, id).onSuccess { profile = it } }
        return this
    }

    private fun checkTokenExpires() {
        viewModelScope.launch {
            userManager.accessTokenExpiresAtFlow.firstOrNull()?.let {
                if (it < curSecond()) {
                    refreshToken()
                }
            }
        }
    }

    private fun autoSignIn() {
        viewModelScope.launch {
            // Get the saved token
            // Note: We use .firstOrNull() to get the current value from the Flow once
            val token = userManager.accessTokenFlow.firstOrNull()
            if (token.isNullOrEmpty()) return@launch
            onProcessing()
            checkTokenExpires()
            // Verify with server
            httpRepository.varifyToken(token)
                .onFailure {
                    if (it.code == 403) {
                        refreshToken()
                    } else {
                        toast(it.message)
                        userManager.clearUser()
                    }
                }
                .onException { toast(it.message ?: R.string.unkown_error.str()) }
            unProcessing()
        }
    }

    private fun refreshToken() {
        viewModelScope.launch {
            val refreshToken = userManager.refreshTokenFlow.firstOrNull()
            if (!refreshToken.isNullOrEmpty()) {
                onProcessing()
                httpRepository.refreshToken(refreshToken)
                    .onSuccess { save(it) }
                    .onFailure {
                        toast(it.message)
                        userManager.clearUser()
                    }
                    .onException {
                        toast(it.message ?: R.string.unkown_error.str())
                    }
                unProcessing()
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            userManager.accessTokenFlow.firstOrNull()?.ifEmpty { null }
                ?.let { httpRepository.signOut(it) }
            userManager.clearUser()
        }
    }

    fun signWithTwitter() {
        // This tells Android: "Open the real browser with this URL"
        MyApplication.application.startActivity(
            Intent(
                Intent.ACTION_VIEW,
                "${Api.SPB_AUTH_URL}?provider=x&redirect_to=${Api.REDIRECT}".toUri()
            ).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK })
    }

    fun signWithGithub() {
        viewModelScope.launch {
            val codeVerifier =
                Utils.generateCodeVerifier().apply { userManager.saveCodeVerifier(this) }
            android.util.Log.d("signWithGithub", "codeVerifier: $codeVerifier")
            MyApplication.application.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    ("${Api.SPB_AUTH_URL}?provider=github&redirect_to=${Api.REDIRECT}&code_" +
                            "challenge=${Utils.generateCodeChallenge(codeVerifier)}&code_challenge_method=S256").toUri()
                ).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK })
        }
    }

    fun signWithGoogle(context: Context) {
        viewModelScope.launch {
            onProcessing()
            googleAuther.gOauth(context) { idToken ->
                httpRepository.gAuth(body = GoogleAuth(idToken = idToken, nonce = null))
                    .onSuccess { save(it) }
                    .onFailure { toast(it.message) }
                    .onException { toast(it.message ?: R.string.unkown_error.str()) }
            }
            unProcessing()
        }
    }


    fun sign(email: String) {
        viewModelScope.launch {
            onProcessing()
            httpRepository.sign(email).onSuccess {
                toast("Verify email sent, please confirm your email.") // TODO: R.string
                _verifyEmail.value = email
            }.onFailure { toast(it.message) }
                .onException { toast(it.message ?: R.string.unkown_error.str()) }
            unProcessing()
        }
    }

    fun verifyEmail(email: String, token: String) {
        viewModelScope.launch {
            onProcessing()
            httpRepository.verifyEmail(email, token).onSuccess { it -> save(it) }
                .onFailure { toast(it.message) }
                .onException { toast(it.message ?: R.string.unkown_error.str()) }
            unProcessing()
        }
    }

    fun updateAvatar(url: String) {
        viewModelScope.launch {
//            val objectId = userManager.userFlow.firstOrNull()?.objectId ?: ""
//            val token = userManager.userTokenFlow.firstOrNull()
//            if (objectId.isEmpty() || token.isNullOrEmpty()) return@launch
//            val result = httpRepository.updateProperty(
//                objectId = objectId,
//                token = token,
//                request = UpdateAvatarRequest(url)
//            )
//            result.onSuccess { message ->
//                toast(message)
//            }.onFailure { error ->
//                toast(error.message ?: R.string.unkown_error.toString())
//            }
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
            val userId = userManager.userFlow.firstOrNull()?.id ?: ""
            val token = userManager.accessTokenFlow.firstOrNull()
            if (userId.isEmpty() || token.isNullOrEmpty()) return@launch

            val updateMap = mapOf(
                "phone" to phone,
                "address" to address,
                "birthDate" to birthDate,
                "description" to description,
                "gender" to gender
            )

//            val result = httpRepository.updateProperty(
//                objectId = objectId,
//                token = token,
//                request = updateMap
//            )
//            result.onSuccess { message ->
//                toast(message)
//                userManager.saveUser(
//                    currentUser.value!!.copy(
//                        phone = phone,
//                        address = address,
//                        birthDate = birthDate,
//                        description = description,
//                        gender = gender
//                    )
//                )
//            }.onFailure { error ->
//                toast(error.message ?: R.string.unkown_error.toString())
//            }
        }
    }

    // State for the check is signed up
    private val _verifyEmail = mutableStateOf("")
    val verifyEmail: State<String> = _verifyEmail
    fun resetVerifyEmail() {
        _verifyEmail.value = ""
    }

    // state for home tab index state
    private var _homeTabIndex = mutableIntStateOf(0)
    val homeTabIndex: State<Int> = _homeTabIndex
    fun setHomeTabIndex(index: Int) {
        _homeTabIndex.intValue = index
    }

    // State for the list of users
    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users = _users.asStateFlow()

    init {
        fetchUsers(PAGE_SIZE, 0)
    }

    fun fetchUsers(pageSize: Int, offset: Int) {
        viewModelScope.launch {
//            val result = httpRepository.getUsers(pageSize, offset)
//            result.onSuccess { users ->
//                _users.update { currentList ->
//                    if (offset == 0) users
//                    else currentList + users
//                }
//            }
//            result.onFailure { error ->
//                toast(error.message ?: R.string.unkown_error.toString())
//            }
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

    fun handleMagicLink(uri: Uri) {
        // 1. Get the part after the '#'
        uri.fragment?.let { fragment ->
            // 2. Convert "key=value&key2=value2" into a Map
            val params = fragment.split("&").associate {
                val (key, value) = it.split("=")
                key to value
            }
            val type = params["type"]
            if (type == "magiclink") {
                val accessToken = params["access_token"]
                val expiresAt = params["expires_at"]?.toLong()
                val refreshToken = params["refresh_token"]
                accessToken?.emptyNull()?.let {
                    viewModelScope.launch { save(it, expiresAt, refreshToken) }
                } ?: params["error_description"]?.emptyNull()?.let { toast(it) }
            }
        }
        uri.getQueryParameter("code")?.emptyNull()?.let { code ->
            viewModelScope.launch {
                userManager.codeVerifierFlow.firstOrNull()?.emptyNull()
                    ?.let { codeVerifier ->
                        android.util.Log.d("handleMagicLink", "codeVerifier: $codeVerifier")
                        httpRepository.codeExchange(code, codeVerifier).onSuccess {
                            save(it)
                        }.onFailure { toast(it.message) }.onException {
                            toast(it.message ?: R.string.unkown_error.str())
                        }
                    }
            }
        }
    }
}
