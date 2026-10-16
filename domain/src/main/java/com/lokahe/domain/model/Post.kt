package com.lokahe.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Post(
    val id: String,
    val authorId: String?,
    val createdAt: String,
    val content: String?,
    val imageUrls: String?,
    val videoUrls: String?,
    val hashtag: String?,
    val replyPostId: String?,
    // join
    val profiles: Profile,
    val likes: List<Like>,
    val liked: List<LikeStatus>
)

@Serializable
data class Like(val count: Int)

@Serializable
data class LikeStatus(val userId: String)

data class PostRequest(
    val authorId: String,
    val content: String,
    val imageUrls: String,
    val videoUrls: String,
    val hashtag: String,
    val replyPostId: String?
)