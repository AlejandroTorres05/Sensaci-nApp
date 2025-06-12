// IncidentViewModel.kt
package com.example.sensazionapp.feature.incidents.ui.viewmodel

import android.location.Location
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.sensazionapp.feature.incidents.data.model.*
import com.example.sensazionapp.feature.incidents.data.repository.IncidentRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class IncidentViewModel(
    private val incidentRepository: IncidentRepository
) : ViewModel() {

    companion object {
        private const val TAG = "IncidentViewModel"
    }

    // Estados del formulario de reporte
    private val _formState = MutableStateFlow(IncidentFormState())
    val formState: StateFlow<IncidentFormState> = _formState.asStateFlow()

    // Resultado del último reporte creado
    private val _reportResult = MutableStateFlow<IncidentResult?>(null)
    val reportResult: StateFlow<IncidentResult?> = _reportResult.asStateFlow()

    // Lista de incidentes cercanos
    private val _nearbyIncidents = MutableStateFlow<List<IncidentDTO>>(emptyList())
    val nearbyIncidents: StateFlow<List<IncidentDTO>> = _nearbyIncidents.asStateFlow()

    // Estado de carga para incidentes cercanos
    private val _isLoadingIncidents = MutableStateFlow(false)
    val isLoadingIncidents: StateFlow<Boolean> = _isLoadingIncidents.asStateFlow()

    init {
        Log.d(TAG, "IncidentViewModel inicializado")
    }

    /**
     * Actualizar el título del reporte
     */
    fun updateTitle(title: String) {
        _formState.value = _formState.value.copy(
            title = title,
            error = null
        )
        Log.d(TAG, "Título actualizado: $title")
    }

    /**
     * Actualizar la descripción del reporte
     */
    fun updateDescription(description: String) {
        _formState.value = _formState.value.copy(
            description = description,
            error = null
        )
        Log.d(TAG, "Descripción actualizada")
    }

    /**
     * Actualizar la severidad del reporte
     */
    fun updateSeverity(severity: IncidentSeverity) {
        _formState.value = _formState.value.copy(
            severity = severity,
            error = null
        )
        Log.d(TAG, "Severidad actualizada: $severity")
    }

    /**
     * Actualizar la categoría del reporte
     */
    fun updateCategory(category: IncidentCategory) {
        _formState.value = _formState.value.copy(
            category = category,
            error = null
        )
        Log.d(TAG, "Categoría actualizada: $category")
    }

    /**
     * Crear un nuevo reporte
     */
    fun createIncident(currentLocation: Location?) {
        if (currentLocation == null) {
            _formState.value = _formState.value.copy(
                error = "No se pudo obtener la ubicación actual"
            )
            return
        }

        val formData = _formState.value

        // Validaciones básicas
        if (formData.title.isBlank()) {
            _formState.value = formData.copy(
                error = "El título es obligatorio"
            )
            return
        }

        Log.d(TAG, "=== CREANDO REPORTE ===")
        Log.d(TAG, "Ubicación: ${currentLocation.latitude}, ${currentLocation.longitude}")
        Log.d(TAG, "Título: ${formData.title}")
        Log.d(TAG, "Categoría: ${formData.category}")
        Log.d(TAG, "Severidad: ${formData.severity}")

        val request = IncidentRequest(
            latitude = currentLocation.latitude,
            longitude = currentLocation.longitude,
            title = formData.title,
            description = formData.description.takeIf { it.isNotBlank() },
            severity = formData.severity,
            category = formData.category,
            radius = 100.0 // Radio fijo de 100m por ahora
        )

        viewModelScope.launch {
            incidentRepository.createIncident(request).collect { result ->
                _reportResult.value = result

                when (result) {
                    is IncidentResult.Loading -> {
                        _formState.value = formData.copy(
                            isLoading = true,
                            error = null
                        )
                        Log.d(TAG, "Creando reporte...")
                    }

                    is IncidentResult.Success -> {
                        _formState.value = formData.copy(
                            isLoading = false,
                            isSubmitted = true,
                            error = null
                        )
                        Log.d(TAG, "Reporte creado exitosamente: ${result.incident.id}")

                        // Actualizar la lista de incidentes cercanos
                        loadNearbyIncidents(currentLocation.latitude, currentLocation.longitude)
                    }

                    is IncidentResult.Error -> {
                        _formState.value = formData.copy(
                            isLoading = false,
                            error = result.message
                        )
                        Log.e(TAG, "Error al crear reporte: ${result.message}")
                    }
                }
            }
        }
    }

    /**
     * Cargar incidentes cercanos a una ubicación
     */
    fun loadNearbyIncidents(latitude: Double, longitude: Double, radius: Double = 1000.0) {
        Log.d(TAG, "=== CARGANDO INCIDENTES CERCANOS ===")
        Log.d(TAG, "Ubicación: ($latitude, $longitude), Radio: ${radius}m")

        _isLoadingIncidents.value = true

        viewModelScope.launch {
            incidentRepository.getIncidentsNearby(latitude, longitude, radius).collect { incidents ->
                _nearbyIncidents.value = incidents
                _isLoadingIncidents.value = false

                Log.d(TAG, "Incidentes cargados: ${incidents.size}")
                incidents.forEach { incident ->
                    Log.d(TAG, "- ${incident.title} (${incident.latitude}, ${incident.longitude}) - Intensidad: ${incident.intensityLevel}")
                }
            }
        }
    }

    /**
     * Limpiar el formulario
     */
    fun clearForm() {
        _formState.value = IncidentFormState()
        _reportResult.value = null
        Log.d(TAG, "Formulario limpiado")
    }

    /**
     * Limpiar el resultado del reporte
     */
    fun clearReportResult() {
        _reportResult.value = null
    }
}

/**
 * Factory para crear el ViewModel con dependencias
 */
class IncidentViewModelFactory(
    private val incidentRepository: IncidentRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(IncidentViewModel::class.java)) {
            return IncidentViewModel(incidentRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}