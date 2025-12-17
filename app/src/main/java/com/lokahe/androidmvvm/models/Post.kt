package com.lokahe.androidmvvm.models

data class Post(
    val id: Long,
    val author: String,
    val content: String,
    val published: Boolean,
    val date: Long,
    val likedByMe: Boolean,
    val likes: Int = 0,
    val shares: Int = 0,
    val views: Int = 0,
    val favor: Boolean
)