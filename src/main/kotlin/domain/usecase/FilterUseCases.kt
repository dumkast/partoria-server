package com.partoria.domain.usecase

import com.partoria.domain.repository.PartRepository
import com.partoria.data.models.dto.FilterRequest
import com.partoria.data.models.dto.FilterResponse
import com.partoria.data.models.dto.FiltersMetaResponse

class GetFilteredPartsUseCase(private val partRepository: PartRepository) {
    suspend operator fun invoke(filter: FilterRequest): FilterResponse {
        require(filter.page > 0) { "Page must be greater than 0" }
        require(filter.pageSize in 1..100) { "Page size must be between 1 and 100" }
        return partRepository.getFilteredParts(filter)
    }
}

class GetFiltersMetaUseCase(private val partRepository: PartRepository) {
    suspend operator fun invoke(): FiltersMetaResponse {
        return partRepository.getFiltersMeta()
    }
}