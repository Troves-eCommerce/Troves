package com.troves.data.repository

import com.troves.data.local.preferenceses.AppPreferencesDataSource
import com.troves.domain.AuthenticationRepository
import com.troves.domain.Result
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.flow.first

class AuthenticationRepositoryImpl(
    private val preferences: AppPreferencesDataSource
) : AuthenticationRepository {

    // Lazily resolved so that constructing this repository (e.g. for an
    // onboarding/preferences read at startup) does not touch Firebase before
    // it is initialized. Firebase is only accessed on the first auth call.
    private val firebaseAuth by lazy { Firebase.auth }

    override suspend fun login(email: String, password: String): Result<Unit> = try {
        firebaseAuth.signInWithEmailAndPassword(email, password)
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(e)
    }

    override suspend fun register(email: String, password: String): Result<Unit> = try {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(e)
    }

    override suspend fun logout() {
        firebaseAuth.signOut()
    }

    override suspend fun isLoggedIn(): Boolean =
        firebaseAuth.currentUser != null

    override suspend fun isOnboardingDone(): Boolean =
        preferences.isOnboardingDone.first()

    override suspend fun setOnboardingDone() {
        preferences.setOnboardingDone(true)
    }
}
