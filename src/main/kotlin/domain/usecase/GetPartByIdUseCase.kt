package com.partoria.domain.usecase

import com.partoria.domain.model.ComputerPart
import com.partoria.domain.repository.PartRepository

class GetPartByIdUseCase(private val partRepository: PartRepository) {
    suspend operator fun invoke(id: Int): ComputerPart? {
        return partRepository.getPartById(id)
    }
}