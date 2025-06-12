package com.example.sensazionapp.feature.incidents.data.model

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

/**
 * Modelo para crear un nuevo reporte
 */
data class IncidentRequest(
    @SerializedName("latitude")
    val latitude: Double,

    @SerializedName("longitude")
    val longitude: Double,

    @SerializedName("address")
    val address: String? = null,

    @SerializedName("title")
    val title: String,

    @SerializedName("description")
    val description: String? = null,

    @SerializedName("severity")
    val severity: IncidentSeverity,

    @SerializedName("category")
    val category: IncidentCategory,

    @SerializedName("radius")
    val radius: Double = 100.0, // Radio por defecto 100m

    @SerializedName("imageUrls")
    val imageUrls: List<String>? = null,

    @SerializedName("audioUrl")
    val audioUrl: String? = null
)