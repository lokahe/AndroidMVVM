package com.lokahe.androidmvvm.repository

import com.lokahe.androidmvvm.dao.PersonDao
import com.lokahe.androidmvvm.dao.PostDao
import com.lokahe.androidmvvm.models.Person
import com.lokahe.androidmvvm.models.Post
import javax.inject.Inject

class DataBaseRepository @Inject constructor(
    private val personDao: PersonDao,
    private val postDao: PostDao
) {
    suspend fun insertPerson(person: Person) {
        personDao.insert(person)
    }

    suspend fun insertPost(post: Post) {
        postDao.insert(post)
    }

    suspend fun getAllPersons(): List<Person> {
        return personDao.getAllPersons()
    }

    suspend fun getAllPosts(): List<Post> {
        return postDao.getAllPosts()
    }

    suspend fun getPersonById(id: Int): Person? {
        return personDao.getPersonById(id)
    }
}