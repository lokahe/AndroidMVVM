package com.lokahe.database.repository

import com.lokahe.database.dao.PersonDao
import com.lokahe.database.dao.PostDao
import com.lokahe.database.model.Person
import com.lokahe.database.model.Post
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

    suspend fun getAllPosts(
        pageSize: Int,
        offset: Int,
        authorId: String? = null
    ): List<Post> {
        return postDao.getAllPosts(
            pageSize = pageSize,
            offset = offset,
            authorId = authorId
        )
    }

    suspend fun getPersonById(id: Int): Person? {
        return personDao.getPersonById(id)
    }
}