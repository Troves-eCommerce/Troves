package com.troves.data.repository

import com.troves.domain.AuthenticationRepository
import com.troves.domain.Result
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth

class AuthenticationRepositoryImpl : AuthenticationRepository {

    private val firebaseAuth = Firebase.auth

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
}
