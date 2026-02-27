package com.lokahe.androidmvvm.viewmodels

import androidx.lifecycle.viewModelScope
import com.lokahe.androidmvvm.R
import com.lokahe.androidmvvm.data.local.UserManager
import com.lokahe.androidmvvm.data.models.supabase.Post
import com.lokahe.androidmvvm.data.repository.DataBaseRepository
import com.lokahe.androidmvvm.data.repository.HttpRepository
import com.lokahe.androidmvvm.data.repository.PreferencesRepository
import com.lokahe.androidmvvm.emptyNull
import com.lokahe.androidmvvm.str
import com.lokahe.androidmvvm.toast
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class PostDetailViewModel @Inject constructor(
    private val prefRepository: PreferencesRepository,
    private val dbRepository: DataBaseRepository,
    private val httpRepository: HttpRepository,
    private val userManager: UserManager,
) : BaseViewModel(prefRepository, httpRepository, userManager) {

    private val _post = MutableStateFlow<Post?>(null)
    private val _replyPosts = MutableStateFlow<List<Post>>(emptyList())
    val post = _post.asStateFlow()
    val replyPosts = _replyPosts.asStateFlow()

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