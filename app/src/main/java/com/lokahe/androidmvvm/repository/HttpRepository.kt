package com.lokahe.androidmvvm.repository

import android.util.Log
import com.lokahe.androidmvvm.R
import com.lokahe.androidmvvm.models.network.LoginRequest
import com.lokahe.androidmvvm.models.network.RegisterRequest
import com.lokahe.androidmvvm.network.ApiService
import com.lokahe.androidmvvm.network.UserManager
import com.lokahe.androidmvvm.s
import com.lokahe.androidmvvm.utils.Utils.Companion.md5
import jakarta.inject.Inject

class HttpRepository @Inject constructor(
    private val apiService: ApiService,
    private val userManager: UserManager
) {
    suspend fun register(email: String, password: String, name: String): Result<String> {
        return try {
            val request = RegisterRequest(email, md5(password), name)
            val response = apiService.register(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()?.message ?: s(R.string.registration_successful))
            } else {
                Result.failure(
                    Exception(
                        response.body()?.message ?: R.string.registration_failed.toString()
                    )
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun isUserRegistered(email: String): Boolean {
        // 1. Format the clause: email='john@doe.com'
        val whereClause = "email='$email'"

        // 2. Make the call
        val response = apiService.isRegistered(whereClause)

        if (response.isSuccessful) {
            val usersList = response.body()
            // 3. If list is not null and not empty, the user exists
            return !usersList.isNullOrEmpty()
        }
        return false // Network error or API failure
    }

    suspend fun login(email: String, password: String): Result<String> {
        return try {
            val request = LoginRequest(email, md5(password))
            val response = apiService.login(request)
            if (response.isSuccessful && response.body()?.userToken?.isNotEmpty() == true) {
                userManager.saveUser(response.body()!!)
                Result.success(response.body()?.message ?: s(R.string.registration_successful))
            } else {
                Result.failure(
                    Exception(
                        response.body()?.message ?: R.string.registration_failed.toString()
                    )
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun verifyToken(token: String): Boolean {
        val response = apiService.verifyToken(token)
        if (response.isSuccessful) {
            return response.body() ?: false
        }
        return false // Network error or API failure
    }

    suspend fun logout(token: String): Result<String> {
        return try {
            val response = apiService.logout(token)
            if (!response.isSuccessful) {
                Log.e("Logout", "Error: ${response.body()?.message}")
                Result.failure(
                    Exception(
                        response.body()?.message ?: ""
                    )
                )
            } else {
                Result.success(response.body()?.message ?: "")
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateProperty(
        objectId: String,
        token: String,
        request: Any
    ): Result<String> {
        return try {
            val response = apiService.updateProperty(
                objectId = objectId,
                token = token,
                request = request
            )
            if (response.isSuccessful && response.body() != null) {
                userManager.saveUser(response.body()!!)
                Result.success(response.body()?.message ?: "")
            } else {
                Log.e("updateProperty", "Error: ${response.body()?.message}")
                Result.failure(
                    Exception(
                        response.body()?.message ?: ""
                    )
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
