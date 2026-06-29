package com.troves.data.repository

import com.troves.data.local.preferenceses.AppPreferencesDataSource
import com.troves.domain.AuthenticationRepository
import com.troves.domain.Result
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.flow.first

interface PlatformAuthenticationRepository : AuthenticationRepository

expect fun createAuthenticationRepository(
    preferences: AppPreferencesDataSource
): PlatformAuthenticationRepository

class AuthenticationRepositoryFirebaseImpl(
    private val preferences: AppPreferencesDataSource
) : PlatformAuthenticationRepository {

    // Lazily resolved so that constructing this repository (e.g. for an
    // onboarding/preferences read at startup) does not touch Firebase before
    // it is initialized. Firebase is only accessed on the first auth call.
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

    override suspend fun logout() {
        firebaseAuth.signOut()
        preferences.setLoggedIn(false)
    }

    // The logged-in flag is persisted in DataStore so a gated action (e.g. the
    // cart) can decide whether to prompt for sign-up without touching Firebase.
    override suspend fun isLoggedIn(): Boolean =
        preferences.isLoggedIn.first()

    override suspend fun isOnboardingDone(): Boolean =
        preferences.isOnboardingDone.first()

    override suspend fun setOnboardingDone() {
        preferences.setOnboardingDone(true)
    }
}
