package com.lokahe.androidmvvm.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName

@Entity(tableName = "persons")
data class Person(
    @PrimaryKey(autoGenerate = true)
    @SerialName("id")
    val id: Int? = null,
    @SerialName("name")
    val name:String,
    @SerialName("description")
    val description: String,
    @SerialName("age")
    val age: Int,
    @SerialName("gender")
    val gender: String,
    @SerialName("image")
    val image: String
)