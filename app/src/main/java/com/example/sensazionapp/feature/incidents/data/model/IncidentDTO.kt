package com.example.sensazionapp.feature.incidents.data.model

import com.google.gson.annotations.SerializedName

/**
 * Modelo de respuesta del servidor
 */
data class IncidentDTO(
    @SerializedName("id")
    val id: String,

    @SerializedName("reporterId")
    val reporterId: String,

    @SerializedName("reporterEmail")
    val reporterEmail: String,

    @SerializedName("latitude")
    val latitude: Double,

    @SerializedName("longitude")
    val longitude: Double,

    @SerializedName("address")
    val address: String?,

    @SerializedName("title")
    val title: String,

    @SerializedName("description")
    val description: String?,

    @SerializedName("severity")
    val severity: IncidentSeverity,

    @SerializedName("category")
    val category: IncidentCategory,

    @SerializedName("status")
    val status: IncidentStatus,

    @SerializedName("createdAt")
    val createdAt: String, // ISO 8601 string

    @SerializedName("updatedAt")
    val updatedAt: String,

    @SerializedName("expiresAt")
    val expiresAt: String,

    @SerializedName("confirmationCount")
    val confirmationCount: Int,

    @SerializedName("denialCount")
    val denialCount: Int,

    @SerializedName("radius")
    val radius: Double,

    @SerializedName("intensityLevel")
    val intensityLevel: Double, // 0-100, para determinar el color del overlay

    @SerializedName("lastConfirmationAt")
    val lastConfirmationAt: String?,

    @SerializedName("imageUrls")
    val imageUrls: List<String>?,

    @SerializedName("audioUrl")
    val audioUrl: String?,

    @SerializedName("distance")
    val distance: Double?, // Distancia al usuario actual

    @SerializedName("userHasConfirmed")
    val userHasConfirmed: Boolean
)