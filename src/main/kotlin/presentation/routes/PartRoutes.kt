package com.partoria.presentation.routes

import com.partoria.presentation.controllers.AuthController
import com.partoria.presentation.controllers.PartController
import com.partoria.domain.usecase.GetCurrentUserUseCase
import com.partoria.data.models.dto.ErrorResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.partRoutes(
    authController: AuthController,
    partController: PartController,
    getCurrentUserUseCase: GetCurrentUserUseCase
) {
    // Public routes
    post("/auth/login") {
        authController.login(call)
    }

    post("/auth/register") {
        authController.register(call)
    }

    get("/health") {
        call.respond(mapOf("status" to "ok", "service" to "Partoria API"))
    }

    // Protected routes
    authenticate("auth-jwt") {
        suspend fun ApplicationCall.getUserId(): Int? {
            val principal = principal<JWTPrincipal>() ?: return null
            val username = principal.payload.subject ?: return null
            return getCurrentUserUseCase(username)?.id
        }

        get("/parts") {
            call.respond(partController.getAllParts())
        }

        get("/parts/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("invalid_id", "Invalid part ID"))
                return@get
            }
            val part = partController.getPartById(id)
            if (part != null) {
                call.respond(part)
            } else {
                call.respond(HttpStatusCode.NotFound, ErrorResponse("not_found", "Part not found"))
            }
        }

        get("/favorites") {
            val userId = call.getUserId()
            if (userId == null) {
                call.respond(HttpStatusCode.Unauthorized, ErrorResponse("unauthorized", "User not found"))
                return@get
            }
            call.respond(partController.getFavorites(userId))
        }

        post("/favorites/{partId}") {
            val userId = call.getUserId()
            if (userId == null) {
                call.respond(HttpStatusCode.Unauthorized, ErrorResponse("unauthorized", "User not found"))
                return@post
            }
            val partId = call.parameters["partId"]?.toIntOrNull()
            if (partId == null) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("invalid_id", "Invalid part ID"))
                return@post
            }
            partController.addToFavorites(userId, partId)
            call.respond(HttpStatusCode.Created, mapOf("message" to "Added to favorites"))
        }

        delete("/favorites/{partId}") {
            val userId = call.getUserId()
            if (userId == null) {
                call.respond(HttpStatusCode.Unauthorized, ErrorResponse("unauthorized", "User not found"))
                return@delete
            }
            val partId = call.parameters["partId"]?.toIntOrNull()
            if (partId == null) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("invalid_id", "Invalid part ID"))
                return@delete
            }
            partController.removeFromFavorites(userId, partId)
            call.respond(mapOf("message" to "Removed from favorites"))
        }
    }
}