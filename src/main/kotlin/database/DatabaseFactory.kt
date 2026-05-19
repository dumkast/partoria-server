package com.partoria.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    fun init() {
        val config = HikariConfig().apply {
            jdbcUrl = "jdbc:postgresql://ep-delicate-bread-aptjegvn.c-7.us-east-1.aws.neon.tech/neondb?sslmode=require"
            driverClassName = "org.postgresql.Driver"
            username = "neondb_owner"
            password = "npg_BcwYko9hSMz3"
            maximumPoolSize = 10
            minimumIdle = 2
            connectionTimeout = 30000
            maxLifetime = 600000
            isAutoCommit = true
        }
        val dataSource = HikariDataSource(config)
        Database.connect(dataSource)

        transaction {
            try {
                SchemaUtils.createMissingTablesAndColumns(
                    UserTable,
                    PartTable,
                    PartDetailTable,
                    UserFavoritePartTable
                )
                println("Tables created/verified successfully")
            } catch (e: Exception) {
                println("Error creating tables: ${e.message}")
                e.printStackTrace()
            }
        }
    }
}