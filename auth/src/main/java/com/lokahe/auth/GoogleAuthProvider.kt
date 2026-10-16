package com.lokahe.auth

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.PublicKeyCredential
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.lokahe.log.L

/** [AuthProvider] backed by Android Credential Manager + Google ID. */
class GoogleAuthProvider(
    private val webClientId: String
) : AuthProvider {

    override val id: String = "google"

    private val TAG = this.javaClass.simpleName

    private val googleIdOption by lazy {
        GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(webClientId)
            .build()
    }

    override suspend fun signIn(context: Context): String? = try {
        val request = GetCredentialRequest.Builder().addCredentialOption(googleIdOption).build()
        handleSignIn(CredentialManager.create(context).getCredential(context, request))
    } catch (e: GetCredentialException) {
        // Handle error
        L.e(TAG) { e.message.toString() }
        null
    }

    private fun handleSignIn(result: GetCredentialResponse): String? {
        when (val credential = result.credential) {
            // Check if the credential is a Google ID Token
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        // Use the helper to parse the credential from the bundle
                        val googleIdTokenCredential =
                            GoogleIdTokenCredential.createFrom(credential.data)
                        // SUCCESS: send idToken to the backend for validation
                        return googleIdTokenCredential.idToken
                    } catch (e: GoogleIdTokenParsingException) {
                        L.e(TAG, e) { "Received invalid google id token response" }
                    }
                }
            }
            // Handle Passkeys or other types if you added them to your request
            is PublicKeyCredential -> {
            }

            else -> {
                L.e(TAG) { "Unexpected credential type" }
            }
        }
        return null
    }
}