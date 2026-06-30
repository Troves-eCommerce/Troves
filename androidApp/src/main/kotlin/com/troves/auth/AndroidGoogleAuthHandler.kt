package com.troves.auth

import android.app.Activity
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.troves.presintation.ui.auth.google.GoogleAuthHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.security.MessageDigest
import java.util.UUID

class AndroidGoogleAuthHandler(
    private val activity: Activity,
    private val webClientId: String
) : GoogleAuthHandler {

    private val credentialManager = CredentialManager.create(activity)
    private val scope = CoroutineScope(Dispatchers.Main)

    override fun signIn(
        onSuccess: (idToken: String, accessToken: String?) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val rawNonce = UUID.randomUUID().toString()
        val bytes = rawNonce.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        val hashedNonce = digest.joinToString("") { "%02x".format(it) }

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(webClientId)
            .setNonce(hashedNonce)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        scope.launch {
            try {
                val result = credentialManager.getCredential(activity, request)
                val credential = result.credential
                
                if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                    onSuccess(googleIdTokenCredential.idToken, null)
                } else {
                    onError(IllegalStateException("Unexpected credential type"))
                }
            } catch (e: GetCredentialException) {
                onError(e)
            } catch (e: Exception) {
                onError(e)
            }
        }
    }
}
