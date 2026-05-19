package com.partoria.domain.usecase

import com.partoria.domain.repository.PartRepository

class AddToFavoritesUseCase(private val partRepository: PartRepository) {
    suspend operator fun invoke(userId: Int, partId: Int) {
        partRepository.addToFavorites(userId, partId)
    }
}