package com.partoria.domain.usecase

import com.partoria.domain.model.User
import com.partoria.domain.repository.AuthRepository

class GetCurrentUserUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(username: String): User? {
        return authRepository.getUserByUsername(username)
    }
}