package com.lokahe.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "posts")
data class Post(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val authorId: String,
    val createdAt: String,
    val content: String,
    val imageUrls: String,
    val videoUrls: String,
    val hashtag: String,
    val replyPostId: String
)