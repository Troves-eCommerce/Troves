package com.troves.data.repository

import com.troves.data.source.local.preferenceses.TrovesPreferences
import com.troves.data.source.remote.service.ShopifyApiService
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import com.troves.data.source.remote.service.shopify_dtos.AccessTokenResponse
import com.troves.domain.entity.Customer
import com.troves.domain.repository.AuthenticationRepository
import com.troves.domain.utils.Result
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.auth.GoogleAuthProvider
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

interface PlatformAuthenticationRepository : AuthenticationRepository

expect fun createAuthenticationRepository(
    preferences: TrovesPreferences
): PlatformAuthenticationRepository

class AuthenticationRepositoryFirebaseImpl(
    private val preferences: TrovesPreferences,
    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : PlatformAuthenticationRepository, KoinComponent {

    private val shopifyApiService: ShopifyApiService by inject()

    companion object {
        lateinit var firebaseUser: FirebaseUser
    }

    private val firebaseAuth by lazy { Firebase.auth }

    override suspend fun login(email: String, password: String): Result<Unit> =
        withContext(coroutineDispatcher) {
            try {
                val result = firebaseAuth.signInWithEmailAndPassword(email, password)
                firebaseUser = result.user!!
                val token = result.user?.getIdToken(forceRefresh = false) ?: ""
                preferences.saveAuthToken(token)
                preferences.setLoggedIn(true)
                Result.Success(Unit)
            } catch (e: Exception) {
                Result.Error(e)
            }
        }


    /*
    * register -> firebase + crete customer + save token
    *   email password
    *   username
    * login -> save token
    * */

    override suspend fun register(email: String, password: String): Result<Unit> =
        withContext(coroutineDispatcher) {
            try {
                val result = firebaseAuth.createUserWithEmailAndPassword(email, password)
                val token = result.user?.getIdToken(forceRefresh = false) ?: ""
                firebaseUser = result.user!!
                preferences.saveAuthToken(token)
                preferences.setLoggedIn(true)
                Result.Success(Unit)
            } catch (e: Exception) {
                Result.Error(e)
            }
        }

    override suspend fun signInWithGoogle(idToken: String, accessToken: String?): Result<Unit> =
        withContext(coroutineDispatcher) {
            try {
                val credential = GoogleAuthProvider.credential(idToken, accessToken)
                val result = firebaseAuth.signInWithCredential(credential)
                preferences.setLoggedIn(true)
                firebaseUser = result.user!!
                Result.Success(Unit)
            } catch (e: Exception) {
                Result.Error(e)
            }
        }

    override suspend fun logout() {
        withContext(coroutineDispatcher) {
            firebaseAuth.signOut()
            preferences.setLoggedIn(false)
        }
    }

    override suspend fun createCustomer() {
        withContext(coroutineDispatcher){
            shopifyApiService.createCustomer(
                customer = Customer(
                    firstName = firebaseUser.displayName ?: "",
                    lastName = firebaseUser.displayName ?: "",
                    phone = firebaseUser.phoneNumber ?: "",
                    password = firebaseUser.uid,
                    email = firebaseUser.email ?: ""
                )
            )
        }
    }

    override suspend fun cacheCredincials() {
        withContext(coroutineDispatcher) {
           val token =  shopifyApiService.getCustomerAccessToken(
                email = firebaseUser.email ?: "",
                password = firebaseUser.uid
            )
            when(token){
                is Result.Error -> error("User is not registered")
                Result.Loading -> Unit
                is Result.Success<AccessTokenResponse> -> {
                    require(token.value.customerUserErrors?.isEmpty() == true){
                        "Can't cache the user's access token"
                    }
                    preferences.setShopifyCustomerAccessToken(
                        accessToken = token.value.customerAccessToken?.accessToken ?: error("Couldn't save the user's accessToken"),
                        accessTokenTimestamp = token.value.customerAccessToken?.expiresAt?.toLong() ?: error("Couldn't save the accessTokenTimestamp")
                    )
                }
            }


        }
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
