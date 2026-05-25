package com.partoria.domain.usecase

import com.partoria.data.models.dto.CreatePartRequest
import com.partoria.domain.repository.PartRepository

class CreatePartUseCase(private val partRepository: PartRepository) {
    suspend operator fun invoke(part: CreatePartRequest): Int {
        require(part.name.isNotBlank()) { "Name cannot be empty" }
        require(part.category.isNotBlank()) { "Category cannot be empty" }
        require(part.brand.isNotBlank()) { "Brand cannot be empty" }
        require(part.price > 0) { "Price must be positive" }
        require(part.releaseYear in 2000..2026) { "Invalid release year" }
        return partRepository.createPart(part)
    }
}