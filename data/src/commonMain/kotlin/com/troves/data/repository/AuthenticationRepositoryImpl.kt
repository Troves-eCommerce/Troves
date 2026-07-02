package com.troves.data.repository

import com.troves.data.source.local.preferenceses.AppPreferencesDataSource
import com.troves.domain.entity.UserProfile
import com.troves.domain.utils.Result
import com.troves.domain.repository.AuthenticationRepository
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.GoogleAuthProvider
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

interface PlatformAuthenticationRepository : AuthenticationRepository

expect fun createAuthenticationRepository(
    preferences: AppPreferencesDataSource
): PlatformAuthenticationRepository

class AuthenticationRepositoryFirebaseImpl(
    private val preferences: AppPreferencesDataSource
) : PlatformAuthenticationRepository {

    private val firebaseAuth by lazy { Firebase.auth }

    override suspend fun login(email: String, password: String): Result<Unit> = try {
        val result = firebaseAuth.signInWithEmailAndPassword(email, password)
        val token = result.user?.getIdToken(forceRefresh = false) ?: ""
        preferences.saveAuthToken(token)
        preferences.setLoggedIn(true)
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(e)
    }

    override suspend fun register(email: String, password: String): Result<Unit> = try {
        val result = firebaseAuth.createUserWithEmailAndPassword(email, password)
        val token = result.user?.getIdToken(forceRefresh = false) ?: ""
        preferences.saveAuthToken(token)
        preferences.setLoggedIn(true)
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(e)
    }

    override suspend fun signInWithGoogle(idToken: String, accessToken: String?): Result<Unit> = try {
        val credential = GoogleAuthProvider.credential(idToken, accessToken)
        firebaseAuth.signInWithCredential(credential)
        preferences.setLoggedIn(true)
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(e)
    }

    override suspend fun logout() {
        firebaseAuth.signOut()
        preferences.setLoggedIn(false)
    }

    override val isLoggedInStream: Flow<Boolean> = preferences.isLoggedIn

    override val currentUserStream: Flow<UserProfile?> =
        firebaseAuth.idTokenChanged.map { user ->
            user?.let { UserProfile(id = it.uid, email = it.email) }
        }
    override suspend fun isLoggedIn(): Boolean =
        preferences.isLoggedIn.first()

    override suspend fun isOnboardingDone(): Boolean =
        preferences.isOnboardingDone.first()

    override suspend fun setOnboardingDone() {
        preferences.setOnboardingDone(true)
    }

    override fun getCurrentUserId(): String? = firebaseAuth.currentUser?.uid.also {
        println("DEBUG currentUserId = $it")
    }
}
