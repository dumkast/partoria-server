package com.partoria.presentation.controllers

import com.partoria.domain.usecase.*
import com.partoria.data.models.dto.*
import com.partoria.domain.model.ComputerPart

class PartController(
    private val getAllPartsUseCase: GetAllPartsUseCase,
    private val getPartByIdUseCase: GetPartByIdUseCase,
    private val getPartWithDetailsUseCase: GetPartWithDetailsUseCase,
    private val addToFavoritesUseCase: AddToFavoritesUseCase,
    private val removeFromFavoritesUseCase: RemoveFromFavoritesUseCase,
    private val getFavoritesUseCase: GetFavoritesUseCase,
    private val getFilteredPartsUseCase: GetFilteredPartsUseCase,
    private val getFiltersMetaUseCase: GetFiltersMetaUseCase
) {
    suspend fun getAllParts(): List<PartResponse> {
        return getAllPartsUseCase().map { toResponse(it) }
    }

    suspend fun getPartById(id: Int): PartResponse? {
        return getPartByIdUseCase(id)?.let { toResponse(it) }
    }

    suspend fun getPartWithDetails(id: Int): PartResponse? {
        return getPartWithDetailsUseCase(id)?.let { toResponseWithDetails(it) }
    }

    suspend fun getFilteredParts(filter: FilterRequest): FilterResponse {
        return getFilteredPartsUseCase(filter)
    }

    suspend fun getFiltersMeta(): FiltersMetaResponse {
        return getFiltersMetaUseCase()
    }

    suspend fun addToFavorites(userId: Int, partId: Int) {
        addToFavoritesUseCase(userId, partId)
    }

    suspend fun removeFromFavorites(userId: Int, partId: Int) {
        removeFromFavoritesUseCase(userId, partId)
    }

    suspend fun getFavorites(userId: Int): List<PartResponse> {
        return getFavoritesUseCase(userId).map { toResponse(it) }
    }

    private fun toResponse(part: ComputerPart) = PartResponse(
        id = part.id,
        name = part.name,
        category = part.category,
        brand = part.brand,
        price = part.price,
        specs = part.specs,
        imageUrl = part.imageUrl,
        releaseYear = part.releaseYear
    )

    private fun toResponseWithDetails(part: ComputerPart) = PartResponse(
        id = part.id,
        name = part.name,
        category = part.category,
        brand = part.brand,
        price = part.price,
        specs = part.specs,
        imageUrl = part.imageUrl,
        releaseYear = part.releaseYear,
        details = part.details.map { PartDetailResponse(it.id, it.specification, it.value) }
    )
}