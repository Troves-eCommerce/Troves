package com.troves.domain.repository
import com.troves.domain.entity.UserProfile
import com.troves.domain.utils.Result
import kotlinx.coroutines.flow.Flow


interface AuthenticationRepository {
    suspend fun login(email: String, password: String): Result<Unit>

    suspend fun register(email: String, password: String): Result<Unit>

    suspend fun signInWithGoogle(idToken: String, accessToken: String? = null): Result<Unit>

    suspend fun logout()

    val isLoggedInStream: Flow<Boolean>

    val currentUserStream: Flow<UserProfile?>

    suspend fun isLoggedIn(): Boolean

    suspend fun isOnboardingDone(): Boolean

    suspend fun setOnboardingDone()

    suspend fun isSurveyDone(): Boolean

    suspend fun setSurveyDone()

    fun getCurrentUserId(): String?

    fun getCurrentUserEmail(): String?
}
