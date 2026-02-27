package com.lokahe.androidmvvm.viewmodels

import androidx.lifecycle.viewModelScope
import com.lokahe.androidmvvm.R
import com.lokahe.androidmvvm.data.local.UserManager
import com.lokahe.androidmvvm.data.models.supabase.Like
import com.lokahe.androidmvvm.data.models.supabase.Liked
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
    private val _replyPosts = MutableStateFlow<List<Post>>(emptyList())
    val posts = _posts.asStateFlow()
    val userPosts = _userPosts.asStateFlow()
    val replyPosts = _replyPosts.asStateFlow()

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
        reply2PostId: String = Api.EMPTY_UUID,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            unNullPair(getUser(), getAccessToken())?.let { (user, token) ->
                httpRepository.insertPost(
                    token, PostRequest(user.id, content, imageUrls, videoUrls, "", reply2PostId)
                ).cole().onSuccess { onSuccess() } // TODO: dbRepository.insertPost(it.first())
                    .onFailure { toast(it.message) }
                    .onException { toast(it.message ?: R.string.unkown_error.str()) }
            }
        }
    }

    fun toggleLike(postId: String, liked: Boolean) {
        if (liked) dislike(postId) else like(postId)
    }

    fun like(postId: String) {
        viewModelScope.launch {
            unNullPair(getUser(), getAccessToken())?.let { (user, token) ->
                httpRepository.like(token, postId, user.id).cole().onSuccess {
                    updateProfileLocal(liked = Liked(postId))
                    updatePosts(postId, delLikeCount = 1)
                }.onFailure { toast(it.message) }
                    .onException { toast(it.message ?: R.string.unkown_error.str()) }
            }
        }
    }

    fun dislike(postId: String) {
        viewModelScope.launch {
            unNullPair(getUser(), getAccessToken())?.let { (user, token) ->
                httpRepository.dislike(token, postId, user.id).cole().onSuccess {
                    updateProfileLocal(liked = Liked(postId))
                    updatePosts(postId, delLikeCount = -1)
                }.onFailure { toast(it.message) }
                    .onException { toast(it.message ?: R.string.unkown_error.str()) }
            }
        }
    }

    private fun updatePosts(postId: String, delLikeCount: Int = 0) {
        _posts.value = _posts.value.map {
            if (it.id == postId) it.copy(
                likes = listOf(Like((it.likes[0].count + delLikeCount).coerceAtLeast(0)))
            ) else it
        }
        _userPosts.value = _userPosts.value.map {
            if (it.id == postId) it.copy(
                likes = listOf(Like((it.likes[0].count + delLikeCount).coerceAtLeast(0)))
            ) else it
        }
    }

    fun fetchReplyPosts(pageSize: Int, offset: Int, postId: String) {
        viewModelScope.launch {
            getAccessToken()?.emptyNull()?.let { token ->
                httpRepository.fetchPosts(token, replyId = postId).cole().onSuccess {
                    _replyPosts.value = it
                }.onFailure { toast(it.message) }
                    .onException { toast(it.message ?: R.string.unkown_error.str()) }
            }
        }
    }
}