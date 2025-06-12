package com.example.sensazionapp.feature.map.ui.viewModels

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.sensazionapp.feature.auth.ui.viewModels.AuthViewModel
import com.example.sensazionapp.service.LocationService
import com.example.sensazionapp.util.NetworkUtil
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class MapUiState(
    val currentLocation: Location? = null,
    val isLocationPermissionGranted: Boolean = false,
    val isLoadingLocation: Boolean = false,
    val isLocationSharingActive: Boolean = false,
    val errorMessage: String? = null,
    val mapReady: Boolean = false,
    val lastLocationUpdate: Long = 0L
)

class MapViewModel(
    private val authViewModel: AuthViewModel
) : ViewModel() {

    companion object {
        private const val TAG = "MapViewModel"
        private const val LOCATION_UPDATE_INTERVAL = 10000L // 10 segundos para testing
        private const val FASTEST_UPDATE_INTERVAL = 5000L // 5 segundos
        private const val MIN_DISTANCE_UPDATE = 5f // 5 metros para testing
    }

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var locationCallback: LocationCallback? = null
    private var appContext: Context? = null
    private var locationService: LocationService? = null
    private var lastKnownLocation: Location? = null

    init {
    }

    fun initializeLocationServices(context: Context) {
        appContext = context.applicationContext
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)


        // Inicializar LocationService
        initializeLocationService()
        checkLocationPermission(context)
    }

    private fun initializeLocationService() {
        viewModelScope.launch {
            try {
                val token = authViewModel.getAccessToken()

                val apiService = NetworkUtil.createApiService {
                    authViewModel.getAccessToken()
                }

                locationService = LocationService(apiService)

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Error inicializando servicio: ${e.message}"
                )
            }
        }
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
        } else {
            Log.d(TAG, "Permisos NO concedidos")
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
            errorMessage = "Permiso de ubicación denegado"
        )
    }

    fun toggleLocationSharing() {
        val currentState = _uiState.value.isLocationSharingActive

        if (currentState) {
            stopLocationSharing()
        } else {
            startLocationSharing()
        }
    }

    private fun startLocationSharing() {

        if (locationService == null) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "LocationService no inicializado"
            )
            return
        }

        locationService?.startLocationSharing()
        _uiState.value = _uiState.value.copy(isLocationSharingActive = true)

        // Si ya tenemos una ubicación, enviarla inmediatamente
        _uiState.value.currentLocation?.let { location ->
            sendLocationToServer(location)
        }
    }

    private fun stopLocationSharing() {
        locationService?.stopLocationSharing()
        _uiState.value = _uiState.value.copy(isLocationSharingActive = false)
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
                errorMessage = "Contexto no disponible"
            )
            return
        }

        _uiState.value = _uiState.value.copy(isLoadingLocation = true)

        // Crear request de ubicación
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            LOCATION_UPDATE_INTERVAL
        ).apply {
            setMinUpdateIntervalMillis(FASTEST_UPDATE_INTERVAL)
            setMaxUpdateDelayMillis(LOCATION_UPDATE_INTERVAL + 5000L)
            setMinUpdateDistanceMeters(MIN_DISTANCE_UPDATE)
        }.build()

        // Callback para actualizaciones
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {

                locationResult.lastLocation?.let { location ->

                    val shouldSend = shouldSendLocationUpdate(location)

                    _uiState.value = _uiState.value.copy(
                        currentLocation = location,
                        isLoadingLocation = false,
                        errorMessage = null,
                        lastLocationUpdate = System.currentTimeMillis()
                    )

                    lastKnownLocation = location

                    // Enviar al servidor si está activo
                    if (_uiState.value.isLocationSharingActive && shouldSend) {
                        sendLocationToServer(location)
                    } else {
                        Log.d(TAG, "No enviando: sharing=${_uiState.value.isLocationSharingActive}, shouldSend=$shouldSend")
                    }
                } ?: Log.e(TAG, "LocationResult.lastLocation es NULL")
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

                // Obtener última ubicación conocida
                fusedLocationClient!!.lastLocation.addOnSuccessListener { location ->
                    if (location != null) {

                        _uiState.value = _uiState.value.copy(
                            currentLocation = location,
                            isLoadingLocation = false
                        )
                        lastKnownLocation = location
                    } else {
                        Log.d(TAG, "No hay última ubicación conocida")
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
                errorMessage = "Error de seguridad: ${securityException.message}"
            )
        } catch (exception: Exception) {
            _uiState.value = _uiState.value.copy(
                isLoadingLocation = false,
                errorMessage = "Error inesperado: ${exception.message}"
            )
        }
    }

    private fun shouldSendLocationUpdate(newLocation: Location): Boolean {
        val lastLocation = lastKnownLocation ?: return true

        // Verificar distancia
        val distance = lastLocation.distanceTo(newLocation)

        // Verificar tiempo
        val timeDiff = System.currentTimeMillis() - _uiState.value.lastLocationUpdate
        val shouldSend = distance >= MIN_DISTANCE_UPDATE || timeDiff >= LOCATION_UPDATE_INTERVAL

        return shouldSend
    }

    private fun sendLocationToServer(location: Location) {
        viewModelScope.launch {
            try {
                locationService?.updateLocation(location)

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Error enviando ubicación: ${e.message}"
                )
            }
        }
    }

    fun onMapReady() {
        _uiState.value = _uiState.value.copy(mapReady = true)

        // AUTO-INICIAR location sharing cuando el mapa esté listo
        if (_uiState.value.isLocationPermissionGranted && !_uiState.value.isLocationSharingActive) {
            startLocationSharing()
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    override fun onCleared() {
        super.onCleared()

        locationCallback?.let { callback ->
            fusedLocationClient?.removeLocationUpdates(callback)
        }

        locationService?.stopLocationSharing()
    }
}

class MapViewModelFactory(
    private val authViewModel: AuthViewModel
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MapViewModel::class.java)) {
            return MapViewModel(authViewModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}