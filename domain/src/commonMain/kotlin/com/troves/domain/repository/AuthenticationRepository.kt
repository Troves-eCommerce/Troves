package com.troves.domain.repository

import com.troves.domain.entity.UserProfile
import com.troves.domain.utils.Result
import kotlinx.coroutines.flow.Flow

interface AuthenticationRepository {
    suspend fun login(email: String, password: String): Result<Unit>

    suspend fun register(email: String, password: String): Result<Unit>

    suspend fun signInWithGoogle(idToken: String, accessToken: String? = null): Result<Unit>

    suspend fun ensureShopifyToken(): Result<Unit>

    suspend fun logout()

    val isLoggedInStream: Flow<Boolean>

    val currentUserStream: Flow<UserProfile?>

    suspend fun isLoggedIn(): Boolean

    suspend fun isOnboardingDone(): Boolean

    suspend fun setOnboardingDone()

    /** Per-account: true when the signed-in user completed the survey, or dismissed its banner. */
    val isSurveyDoneStream: Flow<Boolean>

    suspend fun isSurveyDone(): Boolean

    /** Suppresses the banner for the signed-in user without writing a survey document. */
    suspend fun dismissSurveyBanner()

    suspend fun saveSurveyAnswers(answers: com.troves.domain.entity.SurveyAnswers): Result<Unit>

    /** The survey saved on the user's Firestore document, or null if they haven't taken it. */
    suspend fun getSurveyAnswers(): com.troves.domain.entity.SurveyAnswers?

    fun getCurrentUserId(): String?

    fun getCurrentUserEmail(): String?

    suspend fun sendEmailVerification(): Result<Unit>

    suspend fun reloadUser(): Result<Unit>

    suspend fun getCurrentUserProfile(): UserProfile?
}