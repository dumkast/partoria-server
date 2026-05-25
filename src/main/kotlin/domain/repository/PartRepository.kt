package com.partoria.domain.repository

import com.partoria.domain.model.ComputerPart
import com.partoria.data.models.dto.FilterRequest
import com.partoria.data.models.dto.FilterResponse
import com.partoria.data.models.dto.FiltersMetaResponse

interface PartRepository {
    suspend fun getAllParts(): List<ComputerPart>
    suspend fun getPartById(id: Int): ComputerPart?
    suspend fun getPartWithDetails(id: Int): ComputerPart?
    suspend fun getFilteredParts(filter: FilterRequest): FilterResponse
    suspend fun getFiltersMeta(): FiltersMetaResponse
    suspend fun addToFavorites(userId: Int, partId: Int)
    suspend fun removeFromFavorites(userId: Int, partId: Int)
    suspend fun getFavorites(userId: Int): List<ComputerPart>
    suspend fun searchParts(query: String, page: Int, pageSize: Int): FilterResponse
}