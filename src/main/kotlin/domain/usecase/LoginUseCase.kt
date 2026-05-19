package com.partoria.domain.usecase

import com.partoria.domain.model.AuthUser
import com.partoria.domain.repository.AuthRepository

class LoginUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(username: String, password: String): AuthUser? {
        require(username.isNotBlank()) { "Username cannot be empty" }
        require(password.isNotBlank()) { "Password cannot be empty" }
        return authRepository.authenticate(username, password)
    }
}