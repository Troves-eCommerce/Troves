package com.troves.domain.usecase.home

import com.troves.domain.entity.Ad
import com.troves.domain.repository.TrovesRepository
import com.troves.domain.utils.Result
import com.troves.domain.utils.map

/** Fetches the promotional banners for the Home ad slider. */
class GetAdsUseCase(
    private val repository: TrovesRepository,
) {
    suspend operator fun invoke(): Result<List<Ad>> {
        return repository.getDiscountCodes().map { discountCodes ->
            discountCodes.map { discountCode ->
                Ad(
                    id = discountCode.title.hashCode().toLong(),
                    titleTop = discountCode.title,
                    titleBottom = "Special Discount Code",
                    description = "use this discount code for take big discount",
                    buttonText = "Copy code"
                )
            }
        }
    }
}


