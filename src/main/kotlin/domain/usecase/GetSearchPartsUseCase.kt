package com.partoria.domain.usecase

import com.partoria.data.models.dto.FilterResponse
import com.partoria.domain.repository.PartRepository

class GetSearchPartsUseCase(private val partRepository: PartRepository) {
    suspend operator fun invoke(query: String, page: Int, pageSize: Int): FilterResponse {
        require(query.isNotBlank()) { "Search query cannot be empty" }
        return partRepository.searchParts(query, page, pageSize)
    }
}