package com.example.sensazionapp.feature.auth.data.dto

data class LoginRequestDTO(
    val email: String,
    val password: String
)

data class RegisterRequestDTO(
    val name: String,
    val lastName: String,
    val email: String,
    val phone: String,
    val password: String
)

data class AuthResponseDTO(
    val id: String,
    val name: String,
    val lastName: String,
    val email: String,
    val phone: String,
    val token: String
)