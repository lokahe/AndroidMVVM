package com.lokahe.androidmvvm.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.lokahe.androidmvvm.data.models.Post

@Dao
interface PostDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(post: Post)

    @Query("SELECT * FROM posts WHERE (:authorId IS NULL OR authorId = :authorId)  ORDER BY createdAt DESC LIMIT :pageSize OFFSET :offset")
    suspend fun getAllPosts(
        pageSize: Int,
        offset: Int,
        authorId: String? = null
    ): List<Post>
}