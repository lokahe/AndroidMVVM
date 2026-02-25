package com.lokahe.androidmvvm.data.models.supabase

import com.google.gson.annotations.SerializedName

data class Post(
    @SerializedName("id")
    val id: String,
    @SerializedName("author_id")
    val authorId: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("content")
    val content: String,
    @SerializedName("image_urls")
    val imageUrls: String,
    @SerializedName("video_urls")
    val videoUrls: String,
    @SerializedName("hashtag")
    val hashtag: String,
    @SerializedName("reply_post_id")
    val replyPostId: String,
    // join
    /**
     * "profiles":{"name": "テスト花子", "avatar": "https://lh3.googleusercontent.com/a/ACg8ocLAEyzlSvVi2dA9gUOtZpfd73CYnkzzytDcMt1fHQCJ482S9A=s96-c"},"likes":[{"count": 0}]},
     */
    @SerializedName("profiles")
    val profiles: Profile,
    @SerializedName("likes")
    val likes: List<Like>
)

data class Like(
    @SerializedName("count")
    val count: Int
)

data class PostRequest(
    @SerializedName("author_id")
    val authorId: String,
    @SerializedName("content")
    val content: String,
    @SerializedName("image_urls")
    val imageUrls: String,
    @SerializedName("video_urls")
    val videoUrls: String,
    @SerializedName("hashtag")
    val hashtag: String,
    @SerializedName("reply_post_id")
    val replyPostId: String?
)