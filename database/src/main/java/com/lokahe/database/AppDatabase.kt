package com.lokahe.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.lokahe.database.dao.PersonDao
import com.lokahe.database.model.Person

// Add your entities to the array, e.g. entities = [User::class]
@Database(entities = [Person::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    // abstract fun userDao(): UserDao
    abstract fun personDao(): PersonDao
}