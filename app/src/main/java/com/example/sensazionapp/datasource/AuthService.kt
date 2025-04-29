package com.example.sensazionapp.datasource


import android.util.Log
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthService {
    @POST("auth/login")
    suspend fun login(@Body loginData: LoginData): LoginResponse

    @GET("")
    suspend fun getAllUsers(@Header("Authorization") authorization:String): UserResponse
}

data class LoginResponse(
    val data: LoginResponseData
)

data class LoginResponseData(
    val access_token:String,
    val refresh_token:String,
)

data class LoginData(
    val email:String,
    val password:String
)

data class UserResponse(
    val data : List<UserDTO>
)

data class UserDTO(
    val email: String
)