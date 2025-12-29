package com.lokahe.androidmvvm.utils

import com.lokahe.androidmvvm.MyApplication.Companion.application
import com.lokahe.androidmvvm.R
import com.lokahe.androidmvvm.models.Person
import com.lokahe.androidmvvm.s

class Utils {
    companion object {
        val names0 by lazy {
            application.applicationContext.applicationContext.resources.getStringArray(R.array.female_names)
                .toList()
        }

        val names1 by lazy {
            application.applicationContext.applicationContext.resources.getStringArray(R.array.male_names)
                .toList()
        }

        fun randomPerson(gender: String? = null): Person {
            val gd = gender ?: listOf(s(R.string.female), s(R.string.male)).random()
            return Person(
                name = if (gd == s(R.string.female)) names0.random() else names1.random(),
                description = s(R.string.desc_random_person),
                age = (18..65).random(),
                gender = gd,
                image = "https://fastly.picsum.photos/id/336/200/200.jpg?hmac=VZ7MzNM30jINYNf5Oj_8zqPLTDAyKDk6eXWTGnNb4bU"
                //"https://picsum.photos/200"
            )
        }

        fun md5(str: String): String {
            val digest = java.security.MessageDigest.getInstance("MD5")
            val bytes = digest.digest(str.toByteArray())
            return bytes.joinToString("") { "%02x".format(it) }
        }
    }
}