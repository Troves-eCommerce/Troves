package com.troves.data.repository

import com.troves.data.source.local.preferenceses.TrovesPreferences
import com.troves.data.source.remote.service.StorefrontApiService
import com.troves.domain.entity.UserProfile
import com.troves.domain.utils.Result
import com.troves.domain.utils.getOrNull
import com.troves.domain.repository.AuthenticationRepository
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.GoogleAuthProvider
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

interface PlatformAuthenticationRepository : AuthenticationRepository

expect fun createAuthenticationRepository(
    preferences: TrovesPreferences,
    storefront: StorefrontApiService,
): PlatformAuthenticationRepository

class AuthenticationRepositoryFirebaseImpl(
    private val preferences: TrovesPreferences,
    private val storefront: StorefrontApiService,
) : PlatformAuthenticationRepository {

    private val firebaseAuth by lazy { Firebase.auth }

    override suspend fun login(email: String, password: String): Result<Unit> = try {
        val result = firebaseAuth.signInWithEmailAndPassword(email, password)
        val token = result.user?.getIdToken(forceRefresh = false) ?: ""
        preferences.saveAuthToken(token)
        preferences.setLoggedIn(true)
        acquireShopifyToken(email, password)
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(e)
    }

    override suspend fun register(email: String, password: String): Result<Unit> = try {
        val result = firebaseAuth.createUserWithEmailAndPassword(email, password)
        val token = result.user?.getIdToken(forceRefresh = false) ?: ""
        preferences.saveAuthToken(token)
        preferences.setLoggedIn(true)
        // acquireShopifyToken provisions the Shopify customer itself when one doesn't exist yet.
        acquireShopifyToken(email, password)
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(e)
    }

    override suspend fun signInWithGoogle(idToken: String, accessToken: String?): Result<Unit> = try {
        val credential = GoogleAuthProvider.credential(idToken, accessToken)
        val authResult = firebaseAuth.signInWithCredential(credential)
        preferences.setLoggedIn(true)
        val user = authResult.user ?: firebaseAuth.currentUser
        val email = user?.email
        val uid = user?.uid
        if (!email.isNullOrBlank() && !uid.isNullOrBlank()) {
            val password = shopifyPasswordFor(uid)
            runCatching { storefront.createCustomer(email, password) }
            acquireShopifyToken(email, password)
        }
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(e)
    }

    override suspend fun logout() {
        val existingToken = preferences.shopifyCustomerAccessTokenOrNull.first()
        if (!existingToken.isNullOrBlank()) {
            runCatching { storefront.deleteAccessToken(existingToken) }
        }
        preferences.clearShopifyCustomerAccessToken()
        firebaseAuth.signOut()
        preferences.setLoggedIn(false)
    }

    // NOTE: currentUserStream must be declared BEFORE isLoggedInStream, because
    // isLoggedInStream's initializer references it inside combine(...) at
    // construction time. Kotlin initializes properties top-to-bottom, so if
    // this stayed below, currentUserStream would still be uninitialized when
    // isLoggedInStream tries to read it — that's the exact error you hit.
    override val currentUserStream: Flow<UserProfile?> =
        firebaseAuth.idTokenChanged.map { user ->
            user?.let { UserProfile(id = it.uid, email = it.email, isEmailVerified = it.isEmailVerified) }
        }

    /**
     * "Logged in" from the app's point of view requires BOTH the stored session
     * flag AND a verified Firebase email. An account that registered but never
     * confirmed its email is never treated as logged in — so closing the app
     * on the verification screen and reopening it will not drop the user into
     * Home. Google sign-ins are verified by Google itself, so they pass through
     * immediately.
     */
    override val isLoggedInStream: Flow<Boolean> =
        combine(preferences.isLoggedIn, currentUserStream) { storedFlag, user ->
            storedFlag && user?.isEmailVerified == true
        }

    override suspend fun isLoggedIn(): Boolean {
        val storedFlag = preferences.isLoggedIn.first()
        if (!storedFlag) return false
        return firebaseAuth.currentUser?.isEmailVerified == true
    }

    override suspend fun isOnboardingDone(): Boolean =
        preferences.isOnboardingDone.first()

    override suspend fun setOnboardingDone() {
        preferences.setOnboardingDone(true)
    }

    override fun getCurrentUserId(): String? = firebaseAuth.currentUser?.uid

    override fun getCurrentUserEmail(): String? = firebaseAuth.currentUser?.email

    override suspend fun sendEmailVerification(): Result<Unit> = try {
        firebaseAuth.currentUser?.sendEmailVerification()
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(e)
    }

    override suspend fun reloadUser(): Result<Unit> = try {
        firebaseAuth.currentUser?.reload()
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(e)
    }

    override suspend fun getCurrentUserProfile(): UserProfile? =
        firebaseAuth.currentUser?.let {
            UserProfile(id = it.uid, email = it.email, isEmailVerified = it.isEmailVerified)
        }

    private suspend fun acquireShopifyToken(email: String, password: String): Boolean {
        if (storeShopifyToken(email, password)) return true
        runCatching { storefront.createCustomer(email, password) }
        return storeShopifyToken(email, password)
    }

    private suspend fun storeShopifyToken(email: String, password: String): Boolean {
        val token = runCatching { storefront.createAccessToken(email, password) }
            .getOrNull()
            ?.getOrNull()
            ?: return false
        preferences.setShopifyCustomerAccessToken(token.accessToken)
        return true
    }

    private fun shopifyPasswordFor(uid: String): String {
        val raw = "${com.troves.data.BuildKonfig.SHOPIFY_CUSTOMER_PASSWORD_SECRET}:$uid"
        val digest = kotlin.math.abs(raw.hashCode())
        return "Tg!${digest}Aa"
    }
}