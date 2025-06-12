package com.example.sensazionapp.feature.incidents.data.model

import com.google.gson.annotations.SerializedName

/**
 * Enums que coinciden con el backend
 */
enum class IncidentSeverity {
    @SerializedName("LOW")
    LOW,

    @SerializedName("MEDIUM")
    MEDIUM,

    @SerializedName("HIGH")
    HIGH,

    @SerializedName("CRITICAL")
    CRITICAL
}