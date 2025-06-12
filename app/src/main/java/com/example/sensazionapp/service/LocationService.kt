package com.example.sensazionapp.service

import android.location.Location
import android.util.Log
import com.example.sensazionapp.data.remote.ApiService
import com.example.sensazionapp.data.remote.dto.UserLocationRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LocationService(
    private val apiService: ApiService
) {
    companion object {
        private const val TAG = "LocationService"
        private const val LOCATION_UPDATE_INTERVAL = 30_000L // 30 segundos
        private const val MIN_DISTANCE_METERS = 10f // 10 metros
    }

    private var lastSentLocation: Location? = null
    private var locationSendingJob: Job? = null
    private var isServiceActive = false

    fun startLocationSharing() {
        if (isServiceActive) {
            Log.d(TAG, "LocationService ya está activo")
            return
        }

        isServiceActive = true
        Log.d(TAG, "Iniciando LocationService")
    }

    fun stopLocationSharing() {
        isServiceActive = false
        locationSendingJob?.cancel()
        locationSendingJob = null
        lastSentLocation = null
        Log.d(TAG, "LocationService detenido")
    }

    fun updateLocation(newLocation: Location) {
        if (!isServiceActive) {
            Log.d(TAG, "LocationService no está activo, ignorando ubicación")
            return
        }

        // Verificar si debemos enviar la ubicación
        if (shouldSendLocation(newLocation)) {
            sendLocationToAPI(newLocation)
        }
    }

    private fun shouldSendLocation(newLocation: Location): Boolean {
        val lastLocation = lastSentLocation

        // Primera ubicación, siempre enviar
        if (lastLocation == null) {
            Log.d(TAG, "Primera ubicación, enviando")
            return true
        }

        // Verificar distancia
        val distance = lastLocation.distanceTo(newLocation)
        if (distance >= MIN_DISTANCE_METERS) {
            Log.d(TAG, "Distancia suficiente: ${distance}m, enviando ubicación")
            return true
        }

        // Verificar tiempo (como backup)
        val timeDifference = newLocation.time - lastLocation.time
        if (timeDifference >= LOCATION_UPDATE_INTERVAL) {
            Log.d(TAG, "Tiempo suficiente: ${timeDifference}ms, enviando ubicación")
            return true
        }

        Log.d(TAG, "No es necesario enviar ubicación. Distancia: ${distance}m, Tiempo: ${timeDifference}ms")
        return false
    }

    private fun sendLocationToAPI(location: Location) {
        // Cancelar trabajo anterior si existe
        locationSendingJob?.cancel()

        locationSendingJob = CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d(TAG, "Enviando ubicación: lat=${location.latitude}, lon=${location.longitude}")

                val locationRequest = UserLocationRequest(
                    latitude = location.latitude,
                    longitude = location.longitude,
                    accuracy = location.accuracy.toDouble()
                )

                val response = apiService.updateUserLocation(locationRequest)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Log.d(TAG, "Ubicación enviada exitosamente")
                        lastSentLocation = location
                    } else {
                        Log.e(TAG, "Error enviando ubicación: ${response.code()} - ${response.message()}")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e(TAG, "Excepción enviando ubicación", e)
                }
            }
        }
    }

    fun isActive(): Boolean = isServiceActive
}