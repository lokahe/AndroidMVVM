package com.lokahe.androidmvvm.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.lokahe.androidmvvm.data.local.dao.PersonDao
import com.lokahe.androidmvvm.data.local.dao.PostDao
import com.lokahe.androidmvvm.data.models.Person
import com.lokahe.androidmvvm.data.models.Post

// Add your entities to the array, e.g. entities = [User::class]
@Database(entities = [Person::class, Post::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    // abstract fun userDao(): UserDao
    abstract fun personDao(): PersonDao
    abstract fun postDao(): PostDao
}