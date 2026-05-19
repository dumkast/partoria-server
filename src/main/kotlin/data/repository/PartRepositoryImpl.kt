package com.partoria.data.repository

import com.partoria.database.PartDetailTable
import com.partoria.database.PartTable
import com.partoria.database.UserFavoritePartTable
import com.partoria.domain.model.ComputerPart
import com.partoria.domain.model.PartDetail
import com.partoria.domain.repository.PartRepository
import com.partoria.data.models.dto.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class PartRepositoryImpl : PartRepository {

    override suspend fun getAllParts(): List<ComputerPart> = newSuspendedTransaction {
        PartTable.selectAll().map { row -> mapRowToPart(row) }
    }

    override suspend fun getPartById(id: Int): ComputerPart? = newSuspendedTransaction {
        PartTable.selectAll().where { PartTable.id eq id }
            .firstOrNull()
            ?.let { mapRowToPart(it) }
    }

    override suspend fun getPartWithDetails(id: Int): ComputerPart? = newSuspendedTransaction {
        val partRow = PartTable.selectAll().where { PartTable.id eq id }.firstOrNull() ?: return@newSuspendedTransaction null

        val details = PartDetailTable.selectAll()
            .where { PartDetailTable.partId eq id }
            .map { detailRow ->
                PartDetail(
                    id = detailRow[PartDetailTable.id].value.toString(),
                    specification = detailRow[PartDetailTable.specification],
                    value = detailRow[PartDetailTable.value]
                )
            }

        mapRowToPart(partRow).copy(details = details)
    }

    override suspend fun getFilteredParts(filter: FilterRequest): FilterResponse = newSuspendedTransaction {
        var query = PartTable.selectAll()

        if (!filter.categories.isNullOrEmpty()) {
            query = query.andWhere { PartTable.category inList filter.categories }
        }
        if (!filter.brands.isNullOrEmpty()) {
            query = query.andWhere { PartTable.brand inList filter.brands }
        }
        filter.minPrice?.let { query = query.andWhere { PartTable.price greaterEq it } }
        filter.maxPrice?.let { query = query.andWhere { PartTable.price lessEq it } }
        filter.minYear?.let { query = query.andWhere { PartTable.releaseYear greaterEq it } }
        filter.maxYear?.let { query = query.andWhere { PartTable.releaseYear lessEq it } }

        val sortColumn = when (filter.sortBy) {
            "price" -> PartTable.price
            "name" -> PartTable.name
            "year" -> PartTable.releaseYear
            "brand" -> PartTable.brand
            else -> PartTable.id
        }

        val sortOrder = if (filter.sortDirection == "desc") SortOrder.DESC else SortOrder.ASC
        query = query.orderBy(sortColumn to sortOrder)

        val totalCount = query.count()
        val offset = (filter.page - 1) * filter.pageSize
        query = query.limit(filter.pageSize, offset.toLong())

        // 1. Извлекаем из базы данных список доменных моделей ComputerPart
        val domainItems = query.map { row -> mapRowToPart(row) }

        // 2. Исправлено: Мапим доменные модели в PartResponse, чтобы FilterResponse скомпилировался без ошибок!
        val responseItems = domainItems.map { part ->
            PartResponse(
                id = part.id,
                name = part.name,
                category = part.category,
                brand = part.brand,
                price = part.price,
                specs = part.specs,
                imageUrl = part.imageUrl,
                releaseYear = part.releaseYear,
                details = emptyList() // В общем списке деталей изначально нет
            )
        }

        FilterResponse(
            items = responseItems, // Теперь типы совпадают: ожидался List<PartResponse> и передан List<PartResponse>
            totalCount = totalCount.toInt(),
            page = filter.page,
            pageSize = filter.pageSize,
            totalPages = ((totalCount + filter.pageSize - 1) / filter.pageSize).toInt()
        )
    }

    override suspend fun getFiltersMeta(): FiltersMetaResponse = newSuspendedTransaction {
        val categories = PartTable.select(PartTable.category).withDistinct()
            .map { it[PartTable.category] }

        val brands = PartTable.select(PartTable.brand).withDistinct()
            .map { it[PartTable.brand] }

        val minPrice = PartTable.selectAll().minByOrNull { it[PartTable.price] }?.get(PartTable.price) ?: 0.0
        val maxPrice = PartTable.selectAll().maxByOrNull { it[PartTable.price] }?.get(PartTable.price) ?: 0.0

        val minYear = PartTable.selectAll().minByOrNull { it[PartTable.releaseYear] }?.get(PartTable.releaseYear) ?: 2000
        val maxYear = PartTable.selectAll().maxByOrNull { it[PartTable.releaseYear] }?.get(PartTable.releaseYear) ?: 2026

        FiltersMetaResponse(
            categories = categories,
            brands = brands,
            priceRange = PriceRange(min = minPrice, max = maxPrice),
            yearRange = YearRange(min = minYear, max = maxYear)
        )
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
            .map { row -> mapRowToPart(row) }
    }

    private fun mapRowToPart(row: ResultRow): ComputerPart {
        return ComputerPart(
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