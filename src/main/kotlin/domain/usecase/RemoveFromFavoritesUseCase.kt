package com.partoria.domain.usecase

import com.partoria.domain.repository.PartRepository

class RemoveFromFavoritesUseCase(private val partRepository: PartRepository) {
    suspend operator fun invoke(userId: Int, partId: Int) {
        partRepository.removeFromFavorites(userId, partId)
    }
}