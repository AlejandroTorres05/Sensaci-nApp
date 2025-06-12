package com.example.sensazionapp.data.remote.dto

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

/**
 * DTO que coincide exactamente con tu UserDTO de Spring Boot
 */
data class UserDTO(
    val id: String? = null,
    val email: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val phone: String? = null,
    val avatarUrl: String? = null,
    val notificationsEnabled: Boolean? = null,
    val locationSharingEnabled: Boolean? = null,
    val notificationRadius: Double? = null,
    val totalIncidentsReported: Int? = null,
    val totalConfirmations: Int? = null,
    val verificationScore: Double? = null,
    val profileCompleted: Boolean? = null,
    val createdAt: String? = null,
    val lastActiveAt: String? = null
)