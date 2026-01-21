package com.lokahe.androidmvvm.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "posts")
data class Post(
    @PrimaryKey(autoGenerate = false)
    @SerializedName("objectId")
    val objectId: String,
    @SerializedName("ownerId")
    val ownerId: String,
    @SerializedName("author")
    val author: String,
    @SerializedName("avatar")
    val avatar: String,
    @SerializedName("created")
    val created: Long,
    @SerializedName("updated")
    val updated: Long,
    @SerializedName("content")
    val content: String,
    @SerializedName("images")
    val images: String,
    @SerializedName("parentId")
    val parentId: String,

    // base
    @SerializedName("message")
    val message: String?,

    @SerializedName("code")
    val code: String?
)