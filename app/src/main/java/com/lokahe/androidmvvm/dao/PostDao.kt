package com.lokahe.androidmvvm.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.lokahe.androidmvvm.models.Post

@Dao
interface PostDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(post: Post)

    @Query("SELECT * FROM posts")
    suspend fun getAllPosts(): List<Post>

    @Query("SELECT * FROM posts WHERE id = :id")
    suspend fun getPostById(id: Long): Post?

    @Query("DELETE FROM posts WHERE id = :id")
    suspend fun deletePostById(id: Long)
}