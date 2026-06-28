package com.troves.domain.home

import com.troves.domain.Result

/** Fetches the promotional banners for the Home ad slider. */
class GetAdsUseCase(
    private val repository: HomeRepository,
) {
    suspend operator fun invoke(): Result<List<Ad>> = repository.getAds()
}
