package com.partoria

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import com.partoria.database.DatabaseFactory
import com.partoria.data.repository.AuthRepositoryImpl
import com.partoria.data.repository.PartRepositoryImpl
import com.partoria.domain.usecase.*
import com.partoria.presentation.controllers.AuthController
import com.partoria.presentation.controllers.PartController
import com.partoria.presentation.routes.partRoutes

fun main() {
    println("Starting Partoria Server...")
    DatabaseFactory.init()
    println("Database initialized")

    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module).start(wait = true)
}

fun Application.module() {
    configureHttp()
    configureSerialization()
    configureSecurity()
    configureStatusPages()

    val authRepository = AuthRepositoryImpl()
    val partRepository = PartRepositoryImpl()

    val loginUseCase = LoginUseCase(authRepository)
    val registerUseCase = RegisterUseCase(authRepository)
    val getCurrentUserUseCase = GetCurrentUserUseCase(authRepository)
    val getAllPartsUseCase = GetAllPartsUseCase(partRepository)
    val getPartByIdUseCase = GetPartByIdUseCase(partRepository)
    val addToFavoritesUseCase = AddToFavoritesUseCase(partRepository)
    val removeFromFavoritesUseCase = RemoveFromFavoritesUseCase(partRepository)
    val getFavoritesUseCase = GetFavoritesUseCase(partRepository)

    val authController = AuthController(loginUseCase, registerUseCase)
    val partController = PartController(
        getAllPartsUseCase,
        getPartByIdUseCase,
        addToFavoritesUseCase,
        removeFromFavoritesUseCase,
        getFavoritesUseCase
    )

    routing {
        partRoutes(authController, partController, getCurrentUserUseCase)
    }
}