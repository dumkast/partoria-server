package com.partoria.domain.usecase

import com.partoria.domain.repository.PartRepository
import com.partoria.data.models.dto.FilterRequest
import com.partoria.data.models.dto.FiltersMetaResponse
import com.partoria.data.models.dto.PartsResponse

class GetFilteredPartsUseCase(private val partRepository: PartRepository) {
    suspend operator fun invoke(filter: FilterRequest): PartsResponse {
        return partRepository.getFilteredParts(filter)
    }
}

class GetFiltersMetaUseCase(private val partRepository: PartRepository) {
    suspend operator fun invoke(): FiltersMetaResponse {
        return partRepository.getFiltersMeta()
    }
}