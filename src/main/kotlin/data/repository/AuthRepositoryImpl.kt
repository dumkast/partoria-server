package com.partoria.data.repository

import com.partoria.database.UserTable
import com.partoria.domain.model.AuthUser
import com.partoria.domain.model.User
import com.partoria.domain.repository.AuthRepository
import com.partoria.security.JwtConfig
import com.partoria.security.PasswordHasher
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class AuthRepositoryImpl : AuthRepository {
    override suspend fun authenticate(username: String, password: String): AuthUser? = newSuspendedTransaction {
        val row = UserTable.selectAll().where { UserTable.username eq username }.firstOrNull()
            ?: return@newSuspendedTransaction null
        if (PasswordHasher.verify(password, row[UserTable.passwordHash])) {
            AuthUser(username, JwtConfig.createToken(username))
        } else null
    }

    override suspend fun getUserByUsername(username: String): User? = newSuspendedTransaction {
        UserTable.selectAll().where { UserTable.username eq username }.firstOrNull()
            ?.let { User(it[UserTable.id], it[UserTable.username], it[UserTable.role]) }
    }
}