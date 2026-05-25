package com.partoria.domain.usecase

import com.partoria.domain.repository.PartRepository

class DeletePartUseCase(private val partRepository: PartRepository) {
    suspend operator fun invoke(id: Int) {
        require(id > 0) { "Invalid part id" }
        partRepository.deletePart(id)
    }
}