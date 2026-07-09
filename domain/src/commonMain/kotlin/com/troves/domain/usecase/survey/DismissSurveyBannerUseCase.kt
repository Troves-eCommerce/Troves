package com.troves.domain.usecase.survey

import com.troves.domain.repository.AuthenticationRepository

/**
 * Stops the survey banner from reappearing for the signed-in user.
 *
 * Distinct from [CompleteSurveyUseCase]: dismissing the banner must not write a
 * survey document, or it would overwrite answers the user gave earlier. The
 * choice is scoped per account, so it does not follow the device.
 */
class DismissSurveyBannerUseCase(
    private val repository: AuthenticationRepository,
) {
    suspend operator fun invoke() = repository.dismissSurveyBanner()
}
