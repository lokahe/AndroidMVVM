package com.lokahe.database.repository

import com.lokahe.database.dao.PersonDao
import com.lokahe.database.model.Person
import javax.inject.Inject

class DataBaseRepository @Inject constructor(
    private val personDao: PersonDao
) {
    suspend fun insertPerson(person: Person) {
        personDao.insert(person)
    }

    suspend fun getAllPersons(): List<Person> {
        return personDao.getAllPersons()
    }

    suspend fun getPersonById(id: Int): Person? {
        return personDao.getPersonById(id)
    }
}