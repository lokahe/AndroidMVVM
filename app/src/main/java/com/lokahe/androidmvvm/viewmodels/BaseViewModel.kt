package com.lokahe.androidmvvm.viewmodels

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lokahe.androidmvvm.AppDialog
import com.lokahe.androidmvvm.R
import com.lokahe.androidmvvm.curSecond
import com.lokahe.androidmvvm.data.local.UserManager
import com.lokahe.androidmvvm.data.models.UserPreferences
import com.lokahe.androidmvvm.data.models.supabase.AuthResponse
import com.lokahe.androidmvvm.data.models.supabase.User
import com.lokahe.androidmvvm.data.repository.HttpRepository
import com.lokahe.androidmvvm.data.repository.PreferencesRepository
import com.lokahe.androidmvvm.str
import com.lokahe.androidmvvm.toast
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.runBlocking

private val _activeDialogs = mutableStateListOf<AppDialog>()

open class BaseViewModel(
    private val repository: PreferencesRepository,
    private val httpRepository: HttpRepository,
    private val userManager: UserManager
) : ViewModel() {
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

    // token
    protected suspend fun getUser(): User? {
        checkTokenExpires()
        return userManager.userFlow.firstOrNull()
    }

    protected suspend fun getAccessToken(): String? {
        checkTokenExpires()
        return userManager.accessTokenFlow.firstOrNull()
    }

    private suspend fun checkTokenExpires() {
        userManager.accessTokenExpiresAtFlow.firstOrNull()?.let {
            if (it < curSecond()) {
                refreshToken()
            }
        }
    }

    protected suspend fun refreshToken() {
        val refreshToken = userManager.refreshTokenFlow.firstOrNull()
        if (!refreshToken.isNullOrEmpty()) {
            httpRepository.refreshToken(refreshToken)
                .onSuccess { save(it) }
                .onFailure { toast(it.message); userManager.clearUser() }
                .onException { toast(it.message ?: R.string.unkown_error.str()) }
        }
    }

    protected suspend fun save(auth: AuthResponse) {
        save(auth.accessToken, auth.expiresAt, auth.refreshToken, auth.user)
    }

    protected suspend fun save(
        token: String?,
        expiresAt: Long?,
        refreshToken: String?,
        user: User? = null
    ) {
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

    // State for the currently visible dialog
    fun showDialog(dialog: AppDialog) {
        _activeDialogs.add(dialog)
    }

    fun dismissDialog(vararg dialogs: AppDialog = emptyArray()) {
        if (dialogs.isEmpty()) _activeDialogs.clear()
        else _activeDialogs.removeAll(dialogs.toSet())
    }

    protected fun onProcessing() {
        showDialog(AppDialog.Loading)
    }

    protected fun unProcessing() {
        dismissDialog(AppDialog.Loading)
    }
}