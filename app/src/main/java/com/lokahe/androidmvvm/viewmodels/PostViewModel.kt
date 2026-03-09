package com.lokahe.androidmvvm.viewmodels

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.lokahe.androidmvvm.R
import com.lokahe.androidmvvm.data.local.UserManager
import com.lokahe.androidmvvm.data.models.supabase.Like
import com.lokahe.androidmvvm.data.models.supabase.LikeStatus
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
import com.lokahe.androidmvvm.utils.Utils
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
//    private val _detailPost = MutableStateFlow<Post?>(null)
//    val detailPost = _detailPost.asStateFlow()
    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    private val _userPosts = MutableStateFlow<List<Post>>(emptyList())
    private val _replyPosts = MutableStateFlow<List<Post>>(emptyList())
    val posts = _posts.asStateFlow()
    val userPosts = _userPosts.asStateFlow()
    val replyPosts = _replyPosts.asStateFlow()

//    fun setDetailPost(post: Post) {
//        _detailPost.value = post
//        fetchReplyPosts(PAGE_SIZE, 0, post.id)
//    }

    fun fetchPosts(pageSize: Int = Api.PAGE_SIZE, offset: Int, authorId: String? = null) {
        viewModelScope.launch {
            getAccessToken()?.emptyNull()?.let { token ->
                httpRepository.fetchPosts(
                    token,
                    authorId,
                    currentUser.value?.id,
                    Api.EMPTY_UUID,
                    pageSize,
                    offset
                ).cole()
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

    fun toggleLike(postId: String, liked: Boolean, onSuccess: () -> Unit = {}) {
        if (liked) dislike(postId, onSuccess) else like(postId, onSuccess)
    }

    fun like(postId: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            unNullPair(getUser(), getAccessToken())?.let { (user, token) ->
                httpRepository.like(token, postId, user.id).cole().onSuccess {
                    updatePosts(postId, delLikeCount = 1)
                    onSuccess()
                }.onFailure { toast(it.message) }
                    .onException { toast(it.message ?: R.string.unkown_error.str()) }
            }
        }
    }

    fun dislike(postId: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            unNullPair(getUser(), getAccessToken())?.let { (user, token) ->
                httpRepository.dislike(token, postId, user.id).cole().onSuccess {
                    updatePosts(postId, delLikeCount = -1)
                    onSuccess()
                }.onFailure { toast(it.message) }
                    .onException { toast(it.message ?: R.string.unkown_error.str()) }
            }
        }
    }

    private fun updatePosts(postId: String, delLikeCount: Int = 0) {
        val transform: (Post) -> Post = {
            if (it.id == postId) it.copy(
                likes = listOf(Like((it.likes[0].count + delLikeCount).coerceAtLeast(0))),
                liked = if (delLikeCount == -1) emptyList()
                else if (delLikeCount == 1 && currentUser.value != null)
                    it.liked.plus(LikeStatus(currentUser.value!!.id))
                else it.liked
            ) else it
        }
        _posts.value = _posts.value.map { transform(it) }
        _userPosts.value = _userPosts.value.map { transform(it) }
        _replyPosts.value = _replyPosts.value.map { transform(it) }
//        _detailPost.value = _detailPost.value?.let { transform(it) }
    }

    fun fetchReplyPosts(pageSize: Int = Api.PAGE_SIZE, offset: Int, postId: String) {
        viewModelScope.launch {
            getAccessToken()?.emptyNull()?.let { token ->
                httpRepository.fetchPosts(
                    token,
                    userId = currentUser.value?.id,
                    limit = pageSize,
                    offset = offset,
                    replyId = postId
                ).cole().onSuccess {
                    _replyPosts.value = it
                }.onFailure { toast(it.message) }
                    .onException { toast(it.message ?: R.string.unkown_error.str()) }
            }
        }
    }

    fun uploadImage(imageUri: Uri) {
        viewModelScope.launch {
            getAccessToken()?.emptyNull()?.let { token ->
                httpRepository.uploadImage(
                    token = token,
                    path = "/${Utils.md5(token)}/${System.currentTimeMillis()}.jpg",
                    imageUri = imageUri,
                    onProgress = {
                        android.util.Log.d("uploadImage", "uploadImage: $it")
                    }).cole().onSuccess {
                    toast(R.string.upload_success.str())
                }.onFailure { toast(it.message) }
                    .onException { toast(it.message ?: R.string.unkown_error.str()) }
            }
        }
    }
}