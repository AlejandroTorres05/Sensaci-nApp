package com.example.sensazionapp.feature.incidents.data.model

/**
 * Resultado de operaciones
 */
sealed class IncidentResult {
    object Loading : IncidentResult()
    data class Success(val incident: IncidentDTO) : IncidentResult()
    data class Error(val message: String) : IncidentResult()
}