package com.partoria.domain.usecase

import com.partoria.domain.repository.AuthRepository

class RegisterUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(username: String, password: String): Boolean {
        require(username.isNotBlank()) { "Username cannot be empty" }
        require(password.isNotBlank()) { "Password cannot be empty" }
        require(password.length >= 6) { "Password must be at least 6 characters" }
        return authRepository.register(username, password)
    }
}