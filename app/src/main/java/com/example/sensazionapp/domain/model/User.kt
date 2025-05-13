package com.example.sensazionapp.domain.model

data class User(
    val id: String,
    val name: String,
    val lastName: String,
    val email: String,
    val phone: String,
    val token: String
)