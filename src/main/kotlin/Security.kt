package com.partoria

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.http.*
import java.util.*

fun Application.configureSecurity() {
    val jwtSecret = "partoria-secret-key-2024-min-32-chars-for-jwt!!"
    val algorithm = Algorithm.HMAC256(jwtSecret)
    val verifier = JWT.require(algorithm)
        .withIssuer("partoria-api")
        .build()

    authentication {
        jwt("auth-jwt") {
            verifier(verifier)
            validate { credential ->
                val username = credential.payload.subject
                if (username != null && credential.payload.expiresAt?.after(Date()) == true) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
            challenge { _, _ ->
                call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Unauthorized"))
            }
        }
    }
}