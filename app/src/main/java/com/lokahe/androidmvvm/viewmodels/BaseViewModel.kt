package com.lokahe.androidmvvm.viewmodels

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lokahe.androidmvvm.AppDialog
import com.lokahe.androidmvvm.R
import com.lokahe.androidmvvm.curSecond
import com.lokahe.androidmvvm.data.local.UserManager
import com.lokahe.androidmvvm.data.models.UserPreferences
import com.lokahe.androidmvvm.data.repository.PreferencesRepository
import com.lokahe.domain.model.AuthResult
import com.lokahe.domain.model.Following
import com.lokahe.domain.model.User
import com.lokahe.domain.repository.AuthRepository
import com.lokahe.domain.repository.UserRepository
import com.lokahe.http.ApiResult
import com.lokahe.androidmvvm.str
import com.lokahe.androidmvvm.toast
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.runBlocking

private val _activeDialogs = mutableStateListOf<AppDialog>()

open class BaseViewModel(
    protected val repository: PreferencesRepository,
    protected val authRepository: AuthRepository,
    protected val userRepository: UserRepository,
    protected val userManager: UserManager
) : ViewModel() {
    // Expose the user as State for Compose
    val currentUser by lazy {
        userManager.userFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    }

    // Check if logged in based on if user data exists
    val isSignedIn by lazy {
        currentUser.map { it != null }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
    }

    // Expose preferences as StateFlow for UI observation
    val userPreferences: StateFlow<UserPreferences> = repository.observeUserPreferences()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = runBlocking {
                repository.observeUserPreferences().first()
            }
        )

    val colorSeed = userManager.colorSeedFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val activeDialogs: List<AppDialog> get() = _activeDialogs

    // api
    private val _apiCallState = MutableStateFlow<ApiResult<Any>?>(null)
    val loadingState =
        _apiCallState.map { it is ApiResult.Loading }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
            .onEach { if (it) onProcessing() else unProcessing() }

    suspend fun <T> Flow<ApiResult<T>>.cole(showLoading: Boolean = true): ApiResult<T> {
        lateinit var finalResult: ApiResult<T>
        this.collect { result ->
            @Suppress("UNCHECKED_CAST")
            if (showLoading) _apiCallState.value = result as ApiResult<Any>?
            if (result !is ApiResult.Loading) finalResult = result
        }
        return finalResult
    }

    // token, user
    protected suspend fun getUser(): User? {
        checkTokenExpires()
        return userManager.userFlow.firstOrNull()
    }

    protected suspend fun getAccessToken(): String? {
        checkTokenExpires()
        return userManager.currentSession()?.accessToken
    }

    private suspend fun checkTokenExpires() {
        userManager.currentSession()?.expiresAt?.let {
            if (it < curSecond()) {
                refreshToken()
            }
        }
    }

    protected suspend fun refreshToken(clear: Boolean = true) {
        val refreshToken = userManager.currentSession()?.refreshToken
        if (!refreshToken.isNullOrEmpty()) {
            authRepository.refreshToken(refreshToken).cole()
                .onSuccess { save(it) }
                .onFailure { toast(it.message); if (clear) userManager.clearUser() }
                .onException { toast(it.message ?: R.string.unkown_error.str()) }
        }
    }

    protected suspend fun save(auth: AuthResult) {
        save(
            auth.session.accessToken,
            auth.session.expiresAt,
            auth.session.refreshToken,
            auth.user
        )
    }

    protected suspend fun save(
        token: String?,
        expiresAt: Long?,
        refreshToken: String?,
        user: User? = null
    ) {
        userManager.saveToken(token, expiresAt, refreshToken)
        user?.let { userManager.saveUser(it.fetchProfile(token)) } ?: token?.let { tk ->
            authRepository.verifyToken(tk).cole()
                .onSuccess { userManager.saveUser(it.fetchProfile(tk)) }
                .onFailure { toast(it.message) }
                .onException { toast(it.message ?: R.string.unkown_error.str()) }
        }
        dismissDialog(AppDialog.SignIn)
    }

    suspend fun updateProfileLocal(
        follower: Following? = null,
    ) = userManager.updateProfileLocal(follower)

    private suspend fun User.fetchProfile(token: String?): User =
        this.apply {
            token?.let { tk ->
                userRepository.fetchProfileById(tk, id).cole().onSuccess { profile = it }
            }
        }

    // State for the currently visible dialog
    fun showDialog(dialog: AppDialog) {
        _activeDialogs.add(dialog)
    }

    fun dismissDialog(vararg dialogs: AppDialog = emptyArray()) {
        if (dialogs.isEmpty()) _activeDialogs.clear()
        else _activeDialogs.removeAll(dialogs.toSet())
    }

    private fun onProcessing() {
        showDialog(AppDialog.Loading)
    }

    private fun unProcessing() {
        dismissDialog(AppDialog.Loading)
    }
}