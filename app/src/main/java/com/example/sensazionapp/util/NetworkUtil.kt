package com.example.sensazionapp.util

import com.example.sensazionapp.data.remote.ApiService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NetworkUtil {
    private const val BASE_URL = "https://7f8a-191-156-6-27.ngrok-free.app/api/"

    // Interceptor para agregar automáticamente el token de Auth0 a todas las requests
    private fun createAuthInterceptor(getToken: () -> String?): Interceptor {
        return Interceptor { chain ->
            val token = getToken()
            val requestBuilder = chain.request().newBuilder()

            if (token != null) {
                requestBuilder.addHeader("Authorization", "Bearer $token")
            }

            chain.proceed(requestBuilder.build())
        }
    }

    private fun createHttpClient(getToken: () -> String?): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(createAuthInterceptor(getToken))
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    private fun createRetrofit(getToken: () -> String?): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(createHttpClient(getToken))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /**
     * Crea el ApiService con autenticación automática
     * @param getToken función que devuelve el token actual de Auth0
     */
    fun createApiService(getToken: () -> String?): ApiService {
        return createRetrofit(getToken).create(ApiService::class.java)
    }
}