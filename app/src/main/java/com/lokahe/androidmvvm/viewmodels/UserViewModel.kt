package com.lokahe.androidmvvm.viewmodels

import androidx.lifecycle.viewModelScope
import com.lokahe.androidmvvm.data.local.UserManager
import com.lokahe.androidmvvm.data.repository.PreferencesRepository
import com.lokahe.domain.model.Followers
import com.lokahe.domain.model.Following
import com.lokahe.domain.model.User
import com.lokahe.domain.model.toUser
import com.lokahe.domain.repository.AuthRepository
import com.lokahe.domain.repository.UserRepository
import com.lokahe.androidmvvm.emptyNull
import com.lokahe.androidmvvm.toast
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class UserViewModel @Inject constructor(
    repository: PreferencesRepository,
    authRepository: AuthRepository,
    userRepository: UserRepository,
    userManager: UserManager
) : BaseViewModel(repository, authRepository, userRepository, userManager) {

    private val _user = MutableStateFlow<User?>(null)
    val user = _user.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    fun fetchUser(id: String) {
        viewModelScope.launch {
            userRepository.fetchProfileById(getAccessToken() ?: "", id).cole().onSuccess {
                _user.value = it.toUser()
            }.onFailure { toast(message = it.message) }.onException { toast(message = it.message) }
        }
    }

    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users = _users.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    fun fetchUsers(ids: List<String>) {
        viewModelScope.launch {
            userRepository.fetchProfilesByIds(getAccessToken() ?: "", ids).cole().onSuccess {
                _users.value = it.map { profile -> profile.toUser() }
            }.onFailure { toast(message = it.message) }.onException { toast(message = it.message) }
        }
    }

    fun refreshMe() {
        viewModelScope.launch { refreshToken(false) }
    }

    fun follow(followerId: String, targetId: String) {
        viewModelScope.launch {
            getAccessToken()?.emptyNull()?.let { token ->
                userRepository.follow(token, followerId, targetId).cole().onSuccess {
                    updateProfileLocal(Following(targetId))
                    updateUser(delFollowCount = 1)
                }.onFailure { toast(message = it.message) }
                    .onException { toast(message = it.message) }
            }
        }
    }

    fun unFollow(followerId: String, targetId: String) {
        viewModelScope.launch {
            getAccessToken()?.emptyNull()?.let { token ->
                userRepository.unFollow(token, followerId, targetId).cole().onSuccess {
                    updateProfileLocal(Following(targetId))
                    updateUser(delFollowCount = -1)
                }.onFailure { toast(message = it.message) }
                    .onException { toast(message = it.message) }
            }
        }
    }

    private fun updateUser(delFollowCount: Int = 0) {
        _user.value = _user.value?.copy(
            profile = _user.value?.profile?.copy(
                followers = listOf(
                    Followers(
                        ((_user.value?.profile?.followers?.get(0)?.count
                            ?: 0) + delFollowCount).coerceAtLeast(0)
                    )
                )
            )
        )
    }
}