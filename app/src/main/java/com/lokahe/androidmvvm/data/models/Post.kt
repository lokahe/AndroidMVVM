package com.lokahe.androidmvvm.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "posts")
data class Post(
    @PrimaryKey(autoGenerate = false)
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
    val replyPostId: String
)