package com.lokahe.androidmvvm.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.lokahe.androidmvvm.models.Person

@Dao
interface PersonDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(person: Person)

    @Query("SELECT * FROM persons")
    suspend fun getAllPersons(): List<Person>

    @Query("SELECT * FROM persons WHERE id = :id")
    suspend fun getPersonById(id: Int): Person?

    @Query("DELETE FROM persons WHERE id = :id")
    suspend fun deletePersonById(id: Int)
}