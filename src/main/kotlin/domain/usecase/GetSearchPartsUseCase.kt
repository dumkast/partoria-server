package com.partoria.domain.usecase

import com.partoria.data.models.dto.PartsResponse
import com.partoria.domain.repository.PartRepository

class GetSearchPartsUseCase(private val partRepository: PartRepository) {
    suspend operator fun invoke(query: String): PartsResponse {
        require(query.isNotBlank()) { "Search query cannot be empty" }
        return partRepository.searchParts(query)
    }
}