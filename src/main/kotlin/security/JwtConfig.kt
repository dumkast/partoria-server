package com.partoria.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.util.*

object JwtConfig {
    private const val SECRET = "partoria-secret-key-2024-min-32-chars-for-jwt!!"
    private const val ISSUER = "partoria-api"
    private const val VALIDITY_MS = 60 * 60 * 1000L

    private val algorithm = Algorithm.HMAC256(SECRET)

    fun createToken(username: String): String {
        return JWT.create()
            .withSubject(username)
            .withIssuer(ISSUER)
            .withExpiresAt(Date(System.currentTimeMillis() + VALIDITY_MS))
            .sign(algorithm)
    }
}