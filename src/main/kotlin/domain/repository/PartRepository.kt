package com.partoria.domain.repository

import com.partoria.data.models.dto.CreatePartRequest
import com.partoria.domain.model.ComputerPart
import com.partoria.data.models.dto.FilterRequest
import com.partoria.data.models.dto.FiltersMetaResponse
import com.partoria.data.models.dto.PartsResponse
import com.partoria.data.models.dto.UpdatePartRequest

interface PartRepository {
    suspend fun getAllParts(): List<ComputerPart>
    suspend fun getPartWithDetails(id: Int): ComputerPart?
    suspend fun getFilteredParts(filter: FilterRequest): PartsResponse
    suspend fun getFiltersMeta(): FiltersMetaResponse
    suspend fun addToFavorites(userId: Int, partId: Int)
    suspend fun removeFromFavorites(userId: Int, partId: Int)
    suspend fun getFavorites(userId: Int): List<ComputerPart>
    suspend fun searchParts(query: String): PartsResponse
    suspend fun createPart(part: CreatePartRequest): Int
    suspend fun updatePart(part: UpdatePartRequest)
}