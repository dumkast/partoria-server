package com.partoria.presentation.routes

import com.partoria.data.models.dto.CreatePartRequest
import com.partoria.presentation.controllers.AuthController
import com.partoria.presentation.controllers.PartController
import com.partoria.domain.usecase.GetCurrentUserUseCase
import com.partoria.data.models.dto.ErrorResponse
import com.partoria.data.models.dto.FilterRequest
import com.partoria.data.models.dto.UpdatePartRequest
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.partRoutes(
    authController: AuthController,
    partController: PartController,
    getCurrentUserUseCase: GetCurrentUserUseCase
) {
    post("/auth/login") {
        authController.login(call)
    }

    post("/auth/register") {
        authController.register(call)
    }

    get("/health") {
        call.respond(mapOf("status" to "ok", "service" to "Partoria API"))
    }

    authenticate("auth-jwt") {
        suspend fun ApplicationCall.getUserId(): Int? {
            val principal = principal<JWTPrincipal>() ?: return null
            val username = principal.payload.subject ?: return null
            return getCurrentUserUseCase(username)?.id
        }

        suspend fun ApplicationCall.isAdmin(): Boolean {
            val principal = principal<JWTPrincipal>() ?: return false
            val username = principal.payload.subject ?: return false
            val user = getCurrentUserUseCase(username)
            return user?.role == "admin"
        }

        get("/parts") {
            call.respond(partController.getAllParts())
        }

        get("/parts/{id}/details") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("invalid_id", "Invalid part ID"))
                return@get
            }
            val part = partController.getPartWithDetails(id)
            if (part != null) {
                call.respond(part)
            } else {
                call.respond(HttpStatusCode.NotFound, ErrorResponse("not_found", "Part not found"))
            }
        }

        post("/parts/filter") {
            try {
                val filter = call.receive<FilterRequest>()
                val result = partController.getFilteredParts(filter)
                call.respond(result)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("invalid_filter", e.message ?: "Invalid filter parameters"))
            }
        }

        get("/parts/filters/meta") {
            val meta = partController.getFiltersMeta()
            call.respond(meta)
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

        get("/parts/search") {
            val query = call.request.queryParameters["q"] ?: ""
            if (query.isBlank()) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("invalid_query", "Search query cannot be empty"))
                return@get
            }
            call.respond(partController.searchParts(query))
        }

        post("/admin/parts") {
            if (!call.isAdmin()) {
                call.respond(HttpStatusCode.Forbidden, ErrorResponse("forbidden", "Admin access required"))
                return@post
            }
            val request = call.receive<CreatePartRequest>()
            val id = partController.createPart(request)
            call.respond(HttpStatusCode.Created, mapOf("message" to "Part created with id: $id"))
        }

        put("/admin/parts") {
            if (!call.isAdmin()) {
                call.respond(HttpStatusCode.Forbidden, ErrorResponse("forbidden", "Admin access required"))
                return@put
            }
            val request = call.receive<UpdatePartRequest>()
            partController.updatePart(request)
            call.respond(mapOf("message" to "Part updated"))
        }

        delete("/admin/parts/{id}") {
            if (!call.isAdmin()) {
                call.respond(HttpStatusCode.Forbidden, ErrorResponse("forbidden", "Admin access required"))
                return@delete
            }
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("invalid_id", "Invalid part ID"))
                return@delete
            }
            try {
                partController.deletePart(id)
                call.respond(mapOf("message" to "Part deleted"))
            } catch (e: Exception) {
                if (e.message?.contains("not found") == true) {
                    call.respond(HttpStatusCode.NotFound, ErrorResponse("not_found", e.message ?: "Part not found"))
                } else {
                    throw e
                }
            }
        }
    }
}