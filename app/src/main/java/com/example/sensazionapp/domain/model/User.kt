package com.example.sensazionapp.domain.model

import java.time.LocalDateTime

data class User(
    val id: String? = null,
    val email: String,
    val firstName: String? = null,
    val lastName: String? = null,
    val phone: String? = null,
    val avatarUrl: String? = null,
    val notificationsEnabled: Boolean = true,
    val locationSharingEnabled: Boolean = true,
    val notificationRadius: Double = 1000.0,
    val totalIncidentsReported: Int = 0,
    val totalConfirmations: Int = 0,
    val verificationScore: Double = 0.0,
    val profileCompleted: Boolean = false,
    val createdAt: LocalDateTime? = null,
    val lastActiveAt: LocalDateTime? = null,

    // Campos específicos de Auth0
    val auth0Id: String? = null,
    val token: String? = null
)