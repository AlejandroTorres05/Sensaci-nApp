package com.example.sensazionapp.feature.map.ui.viewModels

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class MapUiState(
    val currentLocation: Location? = null,
    val isLocationPermissionGranted: Boolean = false,
    val isLoadingLocation: Boolean = false,
    val errorMessage: String? = null,
    val mapReady: Boolean = false
)

class MapViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var locationCallback: LocationCallback? = null
    private var appContext: Context? = null

    fun initializeLocationServices(context: Context) {
        appContext = context.applicationContext
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        checkLocationPermission(context)
    }

    private fun checkLocationPermission(context: Context) {
        val hasLocationPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        _uiState.value = _uiState.value.copy(
            isLocationPermissionGranted = hasLocationPermission
        )

        if (hasLocationPermission) {
            startLocationUpdates()
        }
    }

    fun onLocationPermissionGranted() {
        _uiState.value = _uiState.value.copy(
            isLocationPermissionGranted = true,
            errorMessage = null
        )
        startLocationUpdates()
    }

    fun onLocationPermissionDenied() {
        _uiState.value = _uiState.value.copy(
            isLocationPermissionGranted = false,
            errorMessage = "Permiso de ubicación denegado. La app necesita acceso a la ubicación para funcionar correctamente."
        )
    }

    private fun startLocationUpdates() {
        if (fusedLocationClient == null) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Servicio de ubicación no inicializado"
            )
            return
        }

        if (appContext == null) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Contexto de aplicación no disponible"
            )
            return
        }

        _uiState.value = _uiState.value.copy(isLoadingLocation = true)

        // Crear request de ubicación
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            10000L // Actualizar cada 10 segundos
        ).apply {
            setMinUpdateIntervalMillis(5000L) // Mínimo cada 5 segundos
            setMaxUpdateDelayMillis(15000L) // Máximo delay de 15 segundos
        }.build()

        // Callback actualizaciones de ubicación
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    _uiState.value = _uiState.value.copy(
                        currentLocation = location,
                        isLoadingLocation = false,
                        errorMessage = null
                    )
                }
            }
        }

        try {
            if (ContextCompat.checkSelfPermission(
                    appContext!!,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                fusedLocationClient!!.requestLocationUpdates(
                    locationRequest,
                    locationCallback!!,
                    null
                )

                fusedLocationClient!!.lastLocation.addOnSuccessListener { location ->
                    location?.let {
                        _uiState.value = _uiState.value.copy(
                            currentLocation = it,
                            isLoadingLocation = false
                        )
                    }
                }.addOnFailureListener { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoadingLocation = false,
                        errorMessage = "Error obteniendo ubicación: ${exception.message}"
                    )
                }
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoadingLocation = false,
                    errorMessage = "Permisos de ubicación no concedidos"
                )
            }
        } catch (securityException: SecurityException) {
            _uiState.value = _uiState.value.copy(
                isLoadingLocation = false,
                errorMessage = "Error de seguridad al acceder a la ubicación: ${securityException.message}"
            )
        } catch (exception: Exception) {
            _uiState.value = _uiState.value.copy(
                isLoadingLocation = false,
                errorMessage = "Error inesperado: ${exception.message}"
            )
        }
    }

    fun onMapReady() {
        _uiState.value = _uiState.value.copy(mapReady = true)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    override fun onCleared() {
        super.onCleared()
        // Detener actualizaciones de ubicación cuando el ViewModel se destruye
        locationCallback?.let { callback ->
            fusedLocationClient?.removeLocationUpdates(callback)
        }
    }
}