package com.partoria

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import com.partoria.database.DatabaseFactory
import com.partoria.database.UserTable
import com.partoria.security.PasswordHasher
import com.partoria.data.repository.AuthRepositoryImpl
import com.partoria.data.repository.PartRepositoryImpl
import com.partoria.domain.usecase.*
import com.partoria.presentation.controllers.AuthController
import com.partoria.presentation.controllers.PartController
import com.partoria.presentation.routes.partRoutes
import org.jetbrains.exposed.sql.insert

fun main() {
    println("Starting Partoria Server...")
    DatabaseFactory.init()
    println("Database initialized")

    transaction {
        if (UserTable.selectAll().where { UserTable.username eq "user" }.empty()) {
            UserTable.insert {
                it[UserTable.username] = "user"
                it[UserTable.passwordHash] = PasswordHasher.hash("user123")
                it[UserTable.role] = "user"
            }
        }
        if (UserTable.selectAll().where { UserTable.username eq "admin" }.empty()) {
            UserTable.insert {
                it[UserTable.username] = "admin"
                it[UserTable.passwordHash] = PasswordHasher.hash("admin123")
                it[UserTable.role] = "admin"
            }
        }
        println("Users verified")
    }

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
    val getPartWithDetailsUseCase = GetPartWithDetailsUseCase(partRepository)
    val addToFavoritesUseCase = AddToFavoritesUseCase(partRepository)
    val removeFromFavoritesUseCase = RemoveFromFavoritesUseCase(partRepository)
    val getFavoritesUseCase = GetFavoritesUseCase(partRepository)
    val getFilteredPartsUseCase = GetFilteredPartsUseCase(partRepository)
    val getFiltersMetaUseCase = GetFiltersMetaUseCase(partRepository)

    val authController = AuthController(loginUseCase, registerUseCase)
    val partController = PartController(
        getAllPartsUseCase,
        getPartByIdUseCase,
        getPartWithDetailsUseCase,
        addToFavoritesUseCase,
        removeFromFavoritesUseCase,
        getFavoritesUseCase,
        getFilteredPartsUseCase,
        getFiltersMetaUseCase
    )

    routing {
        partRoutes(authController, partController, getCurrentUserUseCase)
    }
}