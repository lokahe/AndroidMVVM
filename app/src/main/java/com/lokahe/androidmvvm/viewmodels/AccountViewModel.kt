package com.lokahe.androidmvvm.viewmodels

import androidx.lifecycle.viewModelScope
import com.lokahe.androidmvvm.R
import com.lokahe.androidmvvm.data.local.UserManager
import com.lokahe.androidmvvm.str
import com.lokahe.androidmvvm.toast
import com.lokahe.domain.model.IdentityInfo
import com.lokahe.domain.model.SessionInfo
import com.lokahe.domain.repository.UserRepository
import com.lokahe.store.repository.PreferencesRepository
import com.lokahe.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class AccountViewModel @Inject constructor(
    repository: PreferencesRepository,
    authRepository: AuthRepository,
    userRepository: UserRepository,
    userManager: UserManager
) : BaseViewModel(repository, authRepository, userRepository, userManager) {

    val identities = MutableStateFlow<List<IdentityInfo>>(emptyList())
    val sessions = MutableStateFlow<List<SessionInfo>>(emptyList())

    fun refresh() {
        viewModelScope.launch {
            val token = getAccessToken() ?: return@launch
            userRepository.fetchIdentities(token).cole()
                .onSuccess { identities.value = it }
                .onFailure { toast(it.message) }
                .onException { toast(it.message ?: R.string.unkown_error.str()) }
            userRepository.fetchSessions(token).cole()
                .onSuccess { sessions.value = it }
                .onFailure { toast(it.message) }
                .onException { toast(it.message ?: R.string.unkown_error.str()) }
            updateProfileLocal()
        }
    }

    fun kick(id: String) {
        viewModelScope.launch {
            val token = getAccessToken() ?: return@launch
            userRepository.kickSession(token, id).cole()
                .onSuccess {
                    toast(R.string.success.str())
                    userRepository.fetchSessions(token).cole()
                        .onSuccess { sessions.value = it }
                }
                .onFailure { toast(it.message) }
                .onException { toast(it.message ?: R.string.unkown_error.str()) }
        }
    }
}
