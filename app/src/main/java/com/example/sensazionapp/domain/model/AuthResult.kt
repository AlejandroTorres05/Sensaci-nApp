package com.example.sensazionapp.domain.model

data class AuthResult(
    val success: Boolean,
    val user: User?,
    val errorMessage: String?
)