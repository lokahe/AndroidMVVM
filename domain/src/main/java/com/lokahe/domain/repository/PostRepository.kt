package com.lokahe.domain.repository

import android.net.Uri
import com.lokahe.domain.Const
import com.lokahe.domain.model.Post
import com.lokahe.domain.model.PostRequest
import com.lokahe.http.ApiResult
import kotlinx.coroutines.flow.Flow

/** Post/timeline contract — implementation-agnostic. */
interface PostRepository {
    fun insertPost(token: String, post: PostRequest): Flow<ApiResult<List<Post>>>

    fun fetchPosts(
        token: String,
        authorId: String? = null,
        userId: String? = null,
        replyId: String = Const.EMPTY_UUID,
        limit: Int = Const.PAGE_SIZE,
        offset: Int = 0
    ): Flow<ApiResult<List<Post>>>

    fun deletePosts(token: String, ids: List<String>): Flow<ApiResult<Any>>

    fun like(token: String, postId: String, userId: String): Flow<ApiResult<Unit>>

    fun dislike(token: String, postId: String, userId: String): Flow<ApiResult<Unit>>

    fun uploadImage(
        token: String,
        path: String = "",
        imageUri: Uri,
        onProgress: (percent: Int) -> Unit
    ): Flow<ApiResult<Any>>
}