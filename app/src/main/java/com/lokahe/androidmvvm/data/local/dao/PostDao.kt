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

    @Query("SELECT * FROM posts WHERE (:ownerId IS NULL OR ownerId = :ownerId)  ORDER BY created DESC LIMIT :pageSize OFFSET :offset")
    suspend fun getAllPosts(
        pageSize: Int,
        offset: Int,
        ownerId: String? = null
    ): List<Post>
}