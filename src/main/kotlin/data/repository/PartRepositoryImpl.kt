package com.partoria.data.repository

import com.partoria.database.PartTable
import com.partoria.database.UserFavoritePartTable
import com.partoria.domain.model.ComputerPart
import com.partoria.domain.repository.PartRepository
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class PartRepositoryImpl : PartRepository {

    override suspend fun getAllParts(): List<ComputerPart> = newSuspendedTransaction {
        PartTable.selectAll().map { row ->
            ComputerPart(
                id = row[PartTable.id].value,
                name = row[PartTable.name],
                category = row[PartTable.category],
                brand = row[PartTable.brand],
                price = row[PartTable.price],
                specs = row[PartTable.specs],
                imageUrl = row[PartTable.imageUrl],
                releaseYear = row[PartTable.releaseYear]
            )
        }
    }

    override suspend fun getPartById(id: Int): ComputerPart? = newSuspendedTransaction {
        PartTable.selectAll().where { PartTable.id eq id }
            .firstOrNull()
            ?.let { row ->
                ComputerPart(
                    id = row[PartTable.id].value,
                    name = row[PartTable.name],
                    category = row[PartTable.category],
                    brand = row[PartTable.brand],
                    price = row[PartTable.price],
                    specs = row[PartTable.specs],
                    imageUrl = row[PartTable.imageUrl],
                    releaseYear = row[PartTable.releaseYear]
                )
            }
    }

    override suspend fun addToFavorites(userId: Int, partId: Int) {
        newSuspendedTransaction {
            UserFavoritePartTable.insert {
                it[UserFavoritePartTable.userId] = userId
                it[UserFavoritePartTable.partId] = partId
                it[UserFavoritePartTable.addedAt] = java.time.LocalDateTime.now().toString()
            }
        }
    }

    override suspend fun removeFromFavorites(userId: Int, partId: Int) {
        newSuspendedTransaction {
            UserFavoritePartTable.deleteWhere {
                (UserFavoritePartTable.userId eq userId) and (UserFavoritePartTable.partId eq partId)
            }
        }
    }

    override suspend fun getFavorites(userId: Int): List<ComputerPart> = newSuspendedTransaction {
        (PartTable innerJoin UserFavoritePartTable)
            .selectAll()
            .where { UserFavoritePartTable.userId eq userId }
            .map { row ->
                ComputerPart(
                    id = row[PartTable.id].value,
                    name = row[PartTable.name],
                    category = row[PartTable.category],
                    brand = row[PartTable.brand],
                    price = row[PartTable.price],
                    specs = row[PartTable.specs],
                    imageUrl = row[PartTable.imageUrl],
                    releaseYear = row[PartTable.releaseYear]
                )
            }
    }
}