package com.lokahe.androidmvvm.viewmodels

import androidx.lifecycle.viewModelScope
import com.lokahe.androidmvvm.R
import com.lokahe.androidmvvm.data.local.UserManager
import com.lokahe.androidmvvm.data.models.supabase.Post
import com.lokahe.androidmvvm.data.models.supabase.PostRequest
import com.lokahe.androidmvvm.data.remote.Api
import com.lokahe.androidmvvm.data.repository.DataBaseRepository
import com.lokahe.androidmvvm.data.repository.HttpRepository
import com.lokahe.androidmvvm.data.repository.PreferencesRepository
import com.lokahe.androidmvvm.emptyNull
import com.lokahe.androidmvvm.str
import com.lokahe.androidmvvm.toast
import com.lokahe.androidmvvm.unNullPair
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class PostViewModel @Inject constructor(
    private val prefRepository: PreferencesRepository,
    private val dbRepository: DataBaseRepository,
    private val httpRepository: HttpRepository,
    private val userManager: UserManager,
) : BaseViewModel(prefRepository, httpRepository, userManager) {

    // State for the list of posts
    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    private val _userPosts = MutableStateFlow<List<Post>>(emptyList())
    val posts = _posts.asStateFlow()
    val userPosts = _userPosts.asStateFlow()

    fun fetchPosts(pageSize: Int, offset: Int, authorId: String? = null) {
        viewModelScope.launch {
            getAccessToken()?.emptyNull()?.let { token ->
                httpRepository.fetchPosts(token, authorId, Api.EMPTY_UUID, pageSize, offset).cole()
                    .onSuccess { posts ->
                        if (authorId.isNullOrEmpty())
                            _posts.update { currentList -> if (offset == 0) posts else currentList + posts }
                        else
                            _userPosts.update { currentList -> if (offset == 0) posts else currentList + posts }
                    }.onFailure { toast(it.message) }
                    .onException { toast(it.message ?: R.string.unkown_error.str()) }
            }
        }
    }

    fun deletePost(ids: List<String>) {
        viewModelScope.launch {
            getAccessToken()?.emptyNull()?.let { token ->
                httpRepository.deletePosts(token, ids).cole().onSuccess {
                    _userPosts.update { currentList -> currentList.filter { !ids.contains(it.id) } }
                    toast(R.string.delete_success.str())
                }.onFailure { toast(it.message) }
                    .onException { toast(it.message ?: R.string.unkown_error.str()) }
            }
        }
    }

    fun sendPost(
        content: String,
        imageUrls: String = "",
        videoUrls: String = "",
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            unNullPair(getUser(), getAccessToken())?.let { (user, token) ->
                httpRepository.insertPost(
                    token, PostRequest(user.id, content, imageUrls, videoUrls, "", Api.EMPTY_UUID)
                ).cole().onSuccess { onSuccess() } // TODO: dbRepository.insertPost(it.first())
                    .onFailure { toast(it.message) }
                    .onException { toast(it.message ?: R.string.unkown_error.str()) }
            }
        }
    }
}