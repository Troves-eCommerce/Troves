package com.troves.domain.home

import com.troves.domain.Result
import com.troves.domain.entity.Ad

class GetAdsUseCase(
    private val repository: HomeRepository,
) {
    suspend operator fun invoke(): Result<List<Ad>> = repository.getAds()
}
