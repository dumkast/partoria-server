package com.partoria.domain.repository

import com.partoria.domain.model.ComputerPart

interface PartRepository {
    suspend fun getAllParts(): List<ComputerPart>
    suspend fun getPartById(id: Int): ComputerPart?
    suspend fun addToFavorites(userId: Int, partId: Int)
    suspend fun removeFromFavorites(userId: Int, partId: Int)
    suspend fun getFavorites(userId: Int): List<ComputerPart>
}