package com.partoria.domain.repository

import com.partoria.domain.model.AuthUser
import com.partoria.domain.model.User

interface AuthRepository {
    suspend fun authenticate(username: String, password: String): AuthUser?
    suspend fun getUserByUsername(username: String): User?
}