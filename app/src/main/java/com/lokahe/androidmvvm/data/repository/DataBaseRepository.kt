package com.lokahe.androidmvvm.data.repository

import com.lokahe.androidmvvm.data.local.dao.PersonDao
import com.lokahe.androidmvvm.data.local.dao.PostDao
import com.lokahe.androidmvvm.data.models.Person
import com.lokahe.androidmvvm.data.models.Post
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