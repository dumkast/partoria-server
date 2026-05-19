package com.partoria.presentation.controllers

import com.partoria.domain.usecase.LoginUseCase
import com.partoria.data.models.dto.LoginRequest
import com.partoria.data.models.dto.LoginResponse
import com.partoria.data.models.dto.ErrorResponse
import com.partoria.data.models.dto.RegisterRequest
import com.partoria.data.models.dto.RegisterResponse
import com.partoria.domain.usecase.RegisterUseCase
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*

class AuthController(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase
) {
    suspend fun login(call: ApplicationCall) {
        try {
            val request = call.receive<LoginRequest>()
            val authUser = loginUseCase(request.username, request.password)
            if (authUser != null) {
                call.respond(HttpStatusCode.OK, LoginResponse(authUser.token, authUser.username))
            } else {
                call.respond(HttpStatusCode.Unauthorized, ErrorResponse("invalid_credentials", "Invalid credentials"))
            }
        } catch (e: Exception) {
            call.respond(HttpStatusCode.BadRequest, ErrorResponse("error", e.message ?: "Unknown error"))
        }
    }

    suspend fun register(call: ApplicationCall) {
        try {
            val request = call.receive<RegisterRequest>()
            val success = registerUseCase(request.username, request.password)
            if (success) {
                call.respond(HttpStatusCode.Created, RegisterResponse("User created successfully", request.username))
            } else {
                call.respond(HttpStatusCode.Conflict, ErrorResponse("username_exists", "Username already exists"))
            }
        } catch (e: Exception) {
            call.respond(HttpStatusCode.BadRequest, ErrorResponse("error", e.message ?: "Unknown error"))
        }
    }
}