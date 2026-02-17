package com.lokahe.androidmvvm.data.repository

import android.util.Log
import com.lokahe.androidmvvm.R
import com.lokahe.androidmvvm.data.models.auth.GoogleAuth
import com.lokahe.androidmvvm.data.models.auth.GoogleAuthResponse
import com.lokahe.androidmvvm.data.models.supabase.AuthResponse
import com.lokahe.androidmvvm.data.models.supabase.OtpRequest
import com.lokahe.androidmvvm.data.models.supabase.RefreshTokenRequest
import com.lokahe.androidmvvm.data.models.supabase.User
import com.lokahe.androidmvvm.data.models.supabase.VerifyRequest
import com.lokahe.androidmvvm.data.models.x.Oauth
import com.lokahe.androidmvvm.data.remote.ApiService
import com.lokahe.androidmvvm.s
import jakarta.inject.Inject

class HttpRepository @Inject constructor(
    private val apiService: ApiService
) {

//    suspend fun login(email: String, password: String): Result<String> {
//        return try {
//            val request = LoginRequest(email, md5(password))
//            val response = apiService.login(request)
//            if (response.isSuccessful && response.body()?.userToken?.isNotEmpty() == true) {
//                userManager.saveUser(response.body()!!)
//                Result.success(response.body()?.message ?: s(R.string.registration_successful))
//            } else {
//                Result.failure(
//                    Exception(
//                        response.body()?.message ?: s(R.string.registration_failed)
//                    )
//                )
//            }
//        } catch (e: Exception) {
//            Result.failure(e)
//        }
//    }
//
//    fun loginWithTwitter(
//        stayLoggedIn: Boolean = true
//    ) {
//
//        val providerCode = "twitter" // from Backendless Console → Users → Login Providers
//        Log.d("loginWithTwitter", "loginWithTwitter")
//        Backendless.UserService.loginWithOAuth1(
//            providerCode,
//            Api.TWITTER_ACCESS_TOKEN,        // OAuth 1.0a access token from Twitter
//            Api.TWITTER_ACCESS_SECRET,    // OAuth 1.0a token secret from Twitter
//            /* fieldsMappings = */ null, // or map provider fields to Users columns
//            object : AsyncCallback<BackendlessUser> {
//                override fun handleResponse(user: BackendlessUser) {
//                    Log.d("loginWithTwitter", "user: ${user.toString()}")
//                    // ✅ Logged in – user contains Backendless session (user-token header used automatically)
//                    CoroutineScope(Dispatchers.IO).launch {
//                        userManager.saveUser(
//                            LoginResponse(
//                                userToken = user.getProperty("user-token")?.toString(),
//                                lastLogin = user.getProperty("lastLogin")?.toLong(),
//                                userStatus = user.getProperty("userStatus")?.toString(),
//                                created = user.getProperty("created")?.toLong(),
//                                accountType = user.getProperty("accountType")?.toString(),
//                                socialAccount = user.getProperty("socialAccount")?.toString(),
//                                ownerId = user.getProperty("ownerId")?.toString(),
//                                oAuthIdentities = user.getProperty("oAuthIdentities") as? List<String>,
//                                name = user.getProperty("name").toString(),
//                                className = user.getProperty("___class")?.toString(),
//                                blUserLocale = user.getProperty("blUserLocale")?.toString(),
//                                updated = user.getProperty("updated")?.toLong(),
//                                email = user.getProperty("email").toString(),
//                                objectId = user.getProperty("objectId").toString(),
//                                avatar = user.getProperty("avatar")?.toString(),
//                                phone = user.getProperty("phone")?.toString(),
//                                address = user.getProperty("address")?.toString(),
//                                birthDate = user.getProperty("birthDate")?.toString(),
//                                description = user.getProperty("description")?.toString(),
//                                gender = user.getProperty("gender")?.toString(),
//                                message = user.getProperty("message")?.toString(),
//                                code = user.getProperty("code")?.toString()
//                            )
//                        )
//                    }
//                }
//
//                override fun handleFault(fault: BackendlessFault) {
//                    Log.d("loginWithTwitter", "fault: ${fault.message}")
//                    // ❌ Handle error
//                }
//            },
//            stayLoggedIn
//        )
//    }

    suspend fun xOauth(): Result<Oauth> {
        return try {
            val response = apiService.xOauth()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(
                    Exception(
                        response.body()?.message ?: s(R.string.send_failed)
                    )
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun gAuth(body: GoogleAuth): Result<GoogleAuthResponse> {
        return try {
            val response = apiService.googleAuth(body = body)
            if (response.isSuccessful && response.body() != null) {
                Log.d("GoogleAuth", "gAuth: ${response.body()}")
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.toString()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun varifyToken(accessToken: String): Result<User> {
        return try {
            val response = apiService.varifyToken(token = "Bearer $accessToken")
            if (response.isSuccessful && response.body() != null) {
                Log.d("GoogleAuth", "varifyToken: ${response.body()}")
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.toString()))
            }
        } catch (e: Exception) {
            Log.d("GoogleAuth", "varifyToken: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun refreshToken(refreshToken: String): Result<Any> {
        return try {
            val response = apiService.refreshToken(body = RefreshTokenRequest(refreshToken))
            if (response.isSuccessful) {
                Log.d("GoogleAuth", "refreshToken: ${response.body()}")
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.toString()))
            }
        } catch (e: Exception) {
            Log.d("GoogleAuth", "refreshToken: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun signOut(accessToken: String): Result<Any> {
        return try {
            val response = apiService.signOut(token = "Bearer $accessToken")
            if (response.isSuccessful) {
                Log.d("GoogleAuth", "signOut: ${response.body()}")
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.toString()))
            }
        } catch (e: Exception) {
            Log.d("GoogleAuth", "signOut: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun sign(email: String): Result<String> {
        return try {
            val response = apiService.sign(
                body = OtpRequest(email)
            )
            if (response.isSuccessful) {
                Log.d("GoogleAuth", "sign: ${response.body()}")
                Result.success("Verify email sent, please confirm your email.") // TODO: R.string
            } else {
                Log.d("GoogleAuth", "sign: ${response.body()}")
                Result.failure(Exception(response.toString()))
            }
        } catch (e: Exception) {
            Log.d("GoogleAuth", "sign: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun verifyEmail(email: String, token: String): Result<AuthResponse> {
        return try {
            val response = apiService.verifyEmail(
                body = VerifyRequest(
                    type = "email",
                    email = email,
                    token = token
                )
            )
            if (response.isSuccessful) {
                Log.d("GoogleAuth", "verifyEmail: ${response.body()}")
                Result.success(response.body()!!)
            } else {
                Log.d("GoogleAuth", "verifyEmail: ${response.body()}")
                Result.failure(Exception(response.toString()))
            }
        } catch (e: Exception) {
            Log.d("GoogleAuth", "verifyEmail: ${e.message}")
            Result.failure(e)
        }
    }
}
