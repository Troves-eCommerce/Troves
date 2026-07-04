package com.troves.domain.usecase.home

import com.troves.domain.entity.DiscountCode
import com.troves.domain.repository.TrovesRepository
import com.troves.domain.utils.Result

class GetDiscountCodesUseCase(
    private val repository: TrovesRepository,
) {
    suspend operator fun invoke(): Result<List<DiscountCode>> = repository.getDiscountCodes()
}
