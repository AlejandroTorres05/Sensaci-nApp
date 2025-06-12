package com.example.sensazionapp.data.remote

import com.example.sensazionapp.data.remote.dto.UserDTO
import com.example.sensazionapp.data.remote.dto.LocationDTO
import com.example.sensazionapp.data.remote.dto.UserLocationRequest
import retrofit2.Response
import retrofit2.http.*

/**
 * Interface que define todos los endpoints de tu API Spring Boot
 */
interface ApiService {

    /**
     * 1. Registra un usuario en tu API después de Auth0
     * Se llama inmediatamente después del login/signup exitoso de Auth0
     */
    @POST("users/register")
    suspend fun registerUser(
        @Body registrationData: Map<String, String>
    ): Response<UserDTO>

    /**
     * 2. Completa el perfil del usuario con datos obligatorios
     * Se llama desde CompleteProfileScreen
     */
    @PUT("users/complete-profile")
    suspend fun completeProfile(
        @Body userDTO: UserDTO
    ): Response<UserDTO>

    /**
     * 3. Obtiene el perfil completo del usuario
     * Para verificar si profileCompleted = true
     */
    @GET("users/profile")
    suspend fun getUserProfile(): Response<UserDTO>

    /**
     * 4. Actualizar configuraciones del usuario
     */
    @PUT("users/settings")
    suspend fun updateUserSettings(
        @Body settings: Map<String, Any>
    ): Response<UserDTO>

    /**
     * 5. Actualizar ubicación del usuario en tiempo real
     */
    @POST("users/location")
    suspend fun updateUserLocation(
        @Body locationRequest: UserLocationRequest
    ): Response<LocationDTO>

    /**
     * 6. Obtener ubicación actual del usuario
     */
    @GET("users/location/current")
    suspend fun getCurrentLocation(): Response<LocationDTO>
}