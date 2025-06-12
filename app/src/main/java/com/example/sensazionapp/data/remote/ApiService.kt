package com.example.sensazionapp.data.remote

import com.example.sensazionapp.data.remote.dto.UserDTO
import com.example.sensazionapp.data.remote.dto.LocationDTO
import com.example.sensazionapp.data.remote.dto.UserLocationRequest
import com.example.sensazionapp.feature.incidents.data.model.IncidentDTO
import com.example.sensazionapp.feature.incidents.data.model.IncidentRequest
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

    // === INCIDENT ENDPOINTS ===

    /**
     * Crear un nuevo reporte/incidente
     */
    @POST("incidents")
    suspend fun createIncident(@Body request: IncidentRequest): Response<IncidentDTO>

    /**
     * Obtener incidentes cercanos a una ubicación
     */
    @GET("incidents/nearby")
    suspend fun getIncidentsNearby(
        @Query("lat") latitude: Double,
        @Query("lng") longitude: Double,
        @Query("radius") radius: Double = 1000.0 // Radio por defecto 1km
    ): Response<List<IncidentDTO>>

    /**
     * Obtener un incidente específico por ID
     */
    @GET("incidents/{id}")
    suspend fun getIncidentById(@Path("id") incidentId: String): Response<IncidentDTO>

    /**
     * Confirmar o negar un incidente (para futuras funcionalidades)
     */
    @PUT("incidents/{id}/confirm")
    suspend fun confirmIncident(
        @Path("id") incidentId: String,
        @Body confirmationRequest: Any // IncidentConfirmationRequest cuando lo implementemos
    ): Response<IncidentDTO>
}