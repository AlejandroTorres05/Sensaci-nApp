package com.example.sensazionapp.feature.incidents.data.model

/**
 * Estado UI para el formulario de reporte
 */
data class IncidentFormState(
    val title: String = "",
    val description: String = "",
    val severity: IncidentSeverity = IncidentSeverity.MEDIUM,
    val category: IncidentCategory = IncidentCategory.OTHER,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSubmitted: Boolean = false
)