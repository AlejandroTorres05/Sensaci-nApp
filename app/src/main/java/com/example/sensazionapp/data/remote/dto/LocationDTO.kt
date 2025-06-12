package com.example.sensazionapp.data.remote.dto

data class LocationDTO(
    val id: String,
    val userId: String,
    val latitude: Double,
    val longitude: Double,
    val accuracy: Double,
    val timestamp: String,
    val isActive: Boolean
)