package com.example.sensazionapp.repository

import android.util.Log
import com.example.sensazionapp.config.RetrofitConfig
import com.example.sensazionapp.datasource.AuthService
import com.example.sensazionapp.datasource.LoginData
import com.example.sensazionapp.datasource.local.LocalDataSourceProvider
import com.example.sensazionapp.datasource.local.LocalDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull

class AuthRepository(
    val authService: AuthService= RetrofitConfig.directusRetrofit.create(AuthService::class.java)
){
    suspend fun login(loginData: LoginData){

        val response=authService.login(loginData)
        LocalDataSourceProvider.get().save("accesstoken", response.data.access_token)
        Log.e(">>>",response.data.access_token)

    }

    suspend fun getAccessToken():String?{
        var token = LocalDataSourceProvider.get().load("accesstoken").firstOrNull()
        Log.e(">>>",token.toString())
        return token
    }

    suspend fun getAllUsers(){
        var token = LocalDataSourceProvider.get().load("accesstoken").firstOrNull()
        val response = authService.getAllUsers("Bearer $token")
        response.data.forEach{ user ->
            Log.e(">>>",user.email)

        }
    }
}