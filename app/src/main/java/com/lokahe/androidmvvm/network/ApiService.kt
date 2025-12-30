package com.lokahe.androidmvvm.network

import com.lokahe.androidmvvm.models.network.BaseResponse
import com.lokahe.androidmvvm.models.network.LoginRequest
import com.lokahe.androidmvvm.models.network.LoginResponse
import com.lokahe.androidmvvm.models.network.RegisterRequest
import com.lokahe.androidmvvm.models.network.RegisterResponse
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

    @GET(Api.REGISTERED)
    suspend fun isRegistered(@Query("where") whereClause: String): Response<List<RegisterResponse>>

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
}