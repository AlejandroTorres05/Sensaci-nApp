package com.example.sensazionapp.feature.incidents.data.repository

import android.util.Log
import com.example.sensazionapp.data.remote.ApiService
import com.example.sensazionapp.feature.incidents.data.model.IncidentDTO
import com.example.sensazionapp.feature.incidents.data.model.IncidentRequest
import com.example.sensazionapp.feature.incidents.data.model.IncidentResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class IncidentRepository(
    private val apiService: ApiService
) {
    companion object {
        private const val TAG = "IncidentRepository"
    }

    /**
     * Crear un nuevo reporte/incidente
     */
    fun createIncident(request: IncidentRequest): Flow<IncidentResult> = flow {
        try {
            emit(IncidentResult.Loading)

            Log.d(TAG, "Creando incidente: $request")

            val response = apiService.createIncident(request)

            if (response.isSuccessful) {
                val incident = response.body()
                if (incident != null) {
                    Log.d(TAG, "Incidente creado exitosamente: ${incident.id}")
                    emit(IncidentResult.Success(incident))
                } else {
                    Log.e(TAG, "Respuesta exitosa pero body nulo")
                    emit(IncidentResult.Error("Error: respuesta vacía del servidor"))
                }
            } else {
                val errorMessage = response.errorBody()?.string() ?: "Error desconocido"
                Log.e(TAG, "Error al crear incidente: ${response.code()} - $errorMessage")
                emit(IncidentResult.Error("Error ${response.code()}: $errorMessage"))
            }

        } catch (e: Exception) {
            Log.e(TAG, "Excepción al crear incidente", e)
            emit(IncidentResult.Error("Error de conexión: ${e.message}"))
        }
    }

    /**
     * Obtener incidentes cercanos a una ubicación
     */
    fun getIncidentsNearby(
        latitude: Double,
        longitude: Double,
        radius: Double = 1000.0
    ): Flow<List<IncidentDTO>> = flow {
        try {
            Log.d(TAG, "Obteniendo incidentes cercanos a ($latitude, $longitude) en radio $radius")

            val response = apiService.getIncidentsNearby(latitude, longitude, radius)

            if (response.isSuccessful) {
                val incidents = response.body() ?: emptyList()
                Log.d(TAG, "Incidentes encontrados: ${incidents.size}")
                emit(incidents)
            } else {
                Log.e(TAG, "Error al obtener incidentes: ${response.code()}")
                emit(emptyList())
            }

        } catch (e: Exception) {
            Log.e(TAG, "Excepción al obtener incidentes cercanos", e)
            emit(emptyList())
        }
    }

    /**
     * Obtener un incidente específico por ID
     */
    suspend fun getIncidentById(incidentId: String): IncidentDTO? {
        return try {
            Log.d(TAG, "Obteniendo incidente por ID: $incidentId")

            val response = apiService.getIncidentById(incidentId)

            if (response.isSuccessful) {
                response.body()
            } else {
                Log.e(TAG, "Error al obtener incidente: ${response.code()}")
                null
            }

        } catch (e: Exception) {
            Log.e(TAG, "Excepción al obtener incidente por ID", e)
            null
        }
    }
}