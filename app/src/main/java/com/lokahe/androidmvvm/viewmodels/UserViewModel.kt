package com.lokahe.androidmvvm.viewmodels

import androidx.lifecycle.viewModelScope
import com.lokahe.androidmvvm.data.local.UserManager
import com.lokahe.androidmvvm.data.models.supabase.Follower
import com.lokahe.androidmvvm.data.models.supabase.Followers
import com.lokahe.androidmvvm.data.models.supabase.User
import com.lokahe.androidmvvm.data.repository.HttpRepository
import com.lokahe.androidmvvm.data.repository.PreferencesRepository
import com.lokahe.androidmvvm.emptyNull
import com.lokahe.androidmvvm.nuEmpty
import com.lokahe.androidmvvm.toast
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class UserViewModel @Inject constructor(
    private val prefRepository: PreferencesRepository,
    private val httpRepository: HttpRepository,
    private val userManager: UserManager
) : BaseViewModel(prefRepository, httpRepository, userManager) {

    private val _user = MutableStateFlow<User?>(null)
    val user = _user.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun fetchUser(id: String) {
        viewModelScope.launch {
            httpRepository.fetchProfileById(getAccessToken() ?: "", id).cole().onSuccess {
                _user.value = User(id, it.email, it.phone.nuEmpty(), null, it)
            }.onFailure { toast(message = it.message) }.onException { toast(message = it.message) }
        }
    }

    fun refreshMe() {
        viewModelScope.launch { refreshToken(false) }
    }

    fun follow(followerId: String, targetId: String) {
        viewModelScope.launch {
            getAccessToken()?.emptyNull()?.let { token ->
                httpRepository.follow(token, followerId, targetId).cole().onSuccess {
                    updateProfileLocal(Follower(targetId))
                    updateUser(delFollowCount = 1)
                }.onFailure { toast(message = it.message) }
                    .onException { toast(message = it.message) }
            }
        }
    }

    fun unFollow(followerId: String, targetId: String) {
        viewModelScope.launch {
            getAccessToken()?.emptyNull()?.let { token ->
                httpRepository.unFollow(token, followerId, targetId).cole().onSuccess {
                    updateProfileLocal(Follower(targetId))
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