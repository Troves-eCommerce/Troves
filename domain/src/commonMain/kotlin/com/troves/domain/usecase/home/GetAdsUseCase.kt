package com.troves.domain.usecase.home

import com.troves.domain.Result
import com.troves.domain.entity.Ad
import com.troves.domain.repository.TrovesRepository

/** Fetches the promotional banners for the Home ad slider. */
class GetAdsUseCase(
    private val repository: TrovesRepository,
) {
    suspend operator fun invoke(): Result<List<Ad>> = repository.getAds()
}
