package com.partoria.domain.usecase

import com.partoria.domain.model.ComputerPart
import com.partoria.domain.repository.PartRepository

class GetFavoritesUseCase(private val partRepository: PartRepository) {
    suspend operator fun invoke(userId: Int): List<ComputerPart> {
        return partRepository.getFavorites(userId)
    }
}