package com.partoria.data.models.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val username: String,
    val password: String
)

@Serializable
data class LoginResponse(
    val token: String,
    val username: String
)

@Serializable
data class PartResponse(
    val id: Int,
    val name: String,
    val category: String,
    val brand: String,
    val price: Double,
    val specs: String,
    val imageUrl: String,
    val releaseYear: Int
)

@Serializable
data class ErrorResponse(
    val error: String,
    val message: String
)