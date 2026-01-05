package com.lokahe.androidmvvm.network

import com.lokahe.androidmvvm.models.network.BaseResponse
import com.lokahe.androidmvvm.models.network.LoginRequest
import com.lokahe.androidmvvm.models.network.LoginResponse
import com.lokahe.androidmvvm.models.network.Post
import com.lokahe.androidmvvm.models.network.RegisterRequest
import com.lokahe.androidmvvm.models.network.RegisterResponse
import com.lokahe.androidmvvm.models.network.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @POST(Api.REGISTER)
    suspend fun register(@Body registerRequest: RegisterRequest): Response<RegisterResponse>

    @POST(Api.LOGIN)
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @GET(Api.VERIFY_TOKEN)
    suspend fun verifyToken(@Path("token") token: String): Response<Boolean>

    @GET(Api.LOGOUt)
    suspend fun logout(@Header("user-token") token: String): Response<BaseResponse>

    @PUT(Api.UPDATE_PROPERTY)
    suspend fun updateProperty(
        @Path("user-id") objectId: String,
        @Header("user-token") token: String,
        @Header("Content-Type") contentType: String = "application/json",
        @Body request: Any
    ): Response<LoginResponse>

    @GET(Api.DATA_USERS)
    suspend fun isRegistered(@Query("where") whereClause: String): Response<List<RegisterResponse>>

    @GET(Api.DATA_USERS)
    suspend fun getUsers(
        @Query("pageSize") pageSize: Int,
        @Query("offset") offset: Int
    ): Response<List<User>>

    @GET(Api.DATA_POSTS)
    suspend fun getPosts(
        @Query("pageSize") pageSize: Int,
        @Query("sortBy") sortBy: String = "created DESC",
        @Query("offset") offset: Int
    ): Response<List<Post>>

    @GET(Api.DATA_POSTS)
    suspend fun getPosts(
        @Query("where") whereClause: String,
        @Query("sortBy") sortBy: String = "created DESC",
        @Query("pageSize") pageSize: Int,
        @Query("offset") offset: Int
    ): Response<List<Post>>

    @POST(Api.DATA_POSTS)
    suspend fun post(
        @Header("user-token") token: String,
        @Header("Content-Type") contentType: String = "application/json",
        @Body request: Any
    ): Response<Post>
}