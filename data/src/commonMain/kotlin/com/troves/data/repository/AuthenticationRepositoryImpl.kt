package com.troves.data.repository

import com.troves.data.source.local.preferenceses.TrovesPreferences
import com.troves.data.source.remote.service.StorefrontApiService
import com.troves.data.source.remote.service.admin.ShopifyAdminCustomerService
import com.troves.domain.entity.UserProfile
import com.troves.domain.utils.Result
import com.troves.domain.utils.ShopifyAuthRequiredException
import com.troves.domain.utils.ShopifyProvisioningFailedException
import com.troves.domain.utils.getOrNull
import com.troves.domain.repository.AuthenticationRepository
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.GoogleAuthProvider
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

import com.troves.data.source.remote.RemoteDatasource
import com.troves.data.mapper.toDto
import com.troves.domain.entity.SurveyAnswers

interface PlatformAuthenticationRepository : AuthenticationRepository

expect fun createAuthenticationRepository(
    preferences: TrovesPreferences,
    storefront: StorefrontApiService,
    remoteDatasource: RemoteDatasource,
    adminCustomer: ShopifyAdminCustomerService,
): PlatformAuthenticationRepository

class AuthenticationRepositoryFirebaseImpl(
    private val preferences: TrovesPreferences,
    private val storefront: StorefrontApiService,
    private val remoteDatasource: RemoteDatasource,
    private val adminCustomer: ShopifyAdminCustomerService,
) : PlatformAuthenticationRepository {

    private val firebaseAuth by lazy { Firebase.auth }

    override suspend fun login(email: String, password: String): Result<Unit> = try {
        val result = firebaseAuth.signInWithEmailAndPassword(email, password)
        val token = result.user?.getIdToken(forceRefresh = false) ?: ""
        preferences.saveAuthToken(token)
        preferences.setLoggedIn(true)
        provisionShopify(result.user?.uid, email)
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(e)
    }

    override suspend fun register(email: String, password: String): Result<Unit> = try {
        val result = firebaseAuth.createUserWithEmailAndPassword(email, password)
        val token = result.user?.getIdToken(forceRefresh = false) ?: ""
        preferences.saveAuthToken(token)
        preferences.setLoggedIn(true)
        provisionShopify(result.user?.uid, email)
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(e)
    }

    override suspend fun signInWithGoogle(idToken: String, accessToken: String?): Result<Unit> = try {
        val credential = GoogleAuthProvider.credential(idToken, accessToken)
        val authResult = firebaseAuth.signInWithCredential(credential)
        preferences.setLoggedIn(true)
        val user = authResult.user ?: firebaseAuth.currentUser
        provisionShopify(user?.uid, user?.email)
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(e)
    }

    private suspend fun provisionShopify(uid: String?, email: String?) {
        if (uid.isNullOrBlank() || email.isNullOrBlank()) return
        runCatching { acquireShopifyToken(email, shopifyPasswordFor(uid)) }
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

    private val reloadTrigger = kotlinx.coroutines.flow.MutableSharedFlow<Unit>(
        extraBufferCapacity = 1,
        onBufferOverflow = kotlinx.coroutines.channels.BufferOverflow.DROP_OLDEST
    )

    // NOTE: currentUserStream must be declared BEFORE isLoggedInStream, because
    // isLoggedInStream's initializer references it inside combine(...) at
    // construction time. Kotlin initializes properties top-to-bottom, so if
    // this stayed below, currentUserStream would still be uninitialized when
    // isLoggedInStream tries to read it — that's the exact error you hit.
    override val currentUserStream: Flow<UserProfile?> =
        kotlinx.coroutines.flow.merge(
            firebaseAuth.idTokenChanged,
            reloadTrigger.map { firebaseAuth.currentUser }
        ).map { user ->
            user?.let {
                UserProfile(
                    id = it.uid,
                    email = it.email,
                    isEmailVerified = it.isEmailVerified,
                    displayName = it.displayName,
                    profileImageUrl = it.photoURL
                )
            }
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

    override val isSurveyDoneStream: Flow<Boolean> = preferences.isSurveyDone

    override suspend fun isSurveyDone(): Boolean =
        preferences.isSurveyDone.first()

    override suspend fun setSurveyDone() {
        preferences.setSurveyDone(true)
    }

    override suspend fun saveSurveyAnswers(answers: SurveyAnswers): Result<Unit> {
        val userId = getCurrentUserId() ?: return Result.Error(Exception("User not logged in"))
        
        val dto = answers.toDto()
        val remoteResult = remoteDatasource.saveSurveyAnswers(userId, dto)
        
        if (remoteResult is Result.Success) {
            setSurveyDone()
        }
        return remoteResult
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
        reloadTrigger.tryEmit(Unit)
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(e)
    }

    override suspend fun getCurrentUserProfile(): UserProfile? =
        firebaseAuth.currentUser?.let {
            UserProfile(
                id = it.uid,
                email = it.email,
                isEmailVerified = it.isEmailVerified,
                displayName = it.displayName,
                profileImageUrl = it.photoURL
            )
        }

    override suspend fun ensureShopifyToken(): Result<Unit> {
        val existing = preferences.shopifyCustomerAccessTokenOrNull.first()
        if (!existing.isNullOrBlank()) return Result.Success(Unit)

        val user = firebaseAuth.currentUser
        val email = user?.email
        val uid = user?.uid
        if (email.isNullOrBlank() || uid.isNullOrBlank()) {
            return Result.Error(ShopifyAuthRequiredException())
        }
        return if (acquireShopifyToken(email, shopifyPasswordFor(uid))) {
            Result.Success(Unit)
        } else {
            Result.Error(ShopifyProvisioningFailedException())
        }
    }

    private suspend fun acquireShopifyToken(email: String, password: String): Boolean {
        if (storeShopifyToken(email, password)) return true

        runCatching { storefront.createCustomer(email, password) }
        if (storeShopifyToken(email, password)) return true

        val customerId = runCatching { adminCustomer.findCustomerIdByEmail(email) }.getOrNull()
        if (customerId != null) {
            runCatching { adminCustomer.setCustomerPassword(customerId, password) }
            if (storeShopifyToken(email, password)) return true
        }
        return false
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