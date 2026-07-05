package com.troves.domain.usecase.home

import com.troves.domain.entity.Ad
import com.troves.domain.repository.TrovesRepository
import com.troves.domain.utils.Result
import com.troves.domain.utils.fold
import com.troves.domain.utils.getOrElse
import com.troves.domain.utils.map

/** Fetches the promotional banners for the Home ad slider. */
class GetAdsUseCase(
    private val repository: TrovesRepository,
) {
    suspend operator fun invoke(): Result<List<Ad>> {
        val staticAds = repository.getAds().getOrElse(emptyList())

        return repository.getDiscountCodes().fold(
            onSuccess = { discountCodes ->
                val dynamicAds = discountCodes.map { discountCode ->
                    Ad(
                        id = discountCode.title.hashCode().toLong(),
                        titleTop = discountCode.title,
                        titleBottom = "Special Discount Code",
                        description = "use this discount code for take big discount",
                        buttonText = "Copy code"
                    )
                }
                Result.Success(staticAds + dynamicAds)
            },
            onError = {
                Result.Success(staticAds)
            },
            onLoading = { Result.Loading }
        )
    }
}


