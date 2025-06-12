package com.example.sensazionapp.data.remote.dto

data class UserLocationRequest(
    val latitude: Double,
    val longitude: Double,
    val accuracy: Double? = null
)