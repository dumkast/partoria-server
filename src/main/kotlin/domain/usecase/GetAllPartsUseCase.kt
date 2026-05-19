package com.partoria.domain.usecase

import com.partoria.domain.model.ComputerPart
import com.partoria.domain.repository.PartRepository

class GetAllPartsUseCase(private val partRepository: PartRepository) {
    suspend operator fun invoke(): List<ComputerPart> {
        return partRepository.getAllParts()
    }
}