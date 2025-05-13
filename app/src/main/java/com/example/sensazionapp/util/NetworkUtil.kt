package com.example.sensazionapp.util

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NetworkUtil {
    private const val BASE_URL = "https://tu-api-spring-boot.com/api/"
    /*
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }*/

    /*
    private val httpClient = OkHttpClient.Builder()
        .addInterceptor()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()
*/
    /*
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
*/
    /*
    fun <T> createService(serviceClass: Class<T>): T {
        return retrofit.create(serviceClass)
    }*/
}