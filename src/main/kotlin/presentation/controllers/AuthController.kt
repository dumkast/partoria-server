package com.partoria.presentation.controllers

import com.partoria.domain.usecase.LoginUseCase
import com.partoria.data.models.dto.LoginRequest
import com.partoria.data.models.dto.LoginResponse
import com.partoria.data.models.dto.ErrorResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*

class AuthController(private val loginUseCase: LoginUseCase) {
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
}