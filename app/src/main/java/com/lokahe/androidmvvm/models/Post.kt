package com.lokahe.androidmvvm.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName

@Entity(tableName = "posts")
data class Post(
    @PrimaryKey(autoGenerate = true)
    @SerialName("id")
    val id: Long,
    @SerialName("author")
    val author: String,
    @SerialName("content")
    val content: String,
    @SerialName("published")
    val published: Boolean,
    @SerialName("date")
    val date: Long,
    @SerialName("liked_by_me")
    val likedByMe: Boolean,
    @SerialName("likes")
    val likes: Int = 0,
    @SerialName("shares")
    val shares: Int = 0,
    @SerialName("views")
    val views: Int = 0,
    @SerialName("favor")
    val favor: Boolean
)