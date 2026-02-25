package com.lokahe.androidmvvm.data.auth

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.PublicKeyCredential
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.lokahe.androidmvvm.data.remote.Api
import jakarta.inject.Inject

class GoogleAuther @Inject constructor() {
    private val TAG = this.javaClass.simpleName

    val googleIdOption by lazy {
        GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(Api.GOOGLE_WEB_CLIENT_ID)
            .build()
    }

    suspend fun gOauth(context: Context, onResult: suspend (String) -> Unit = {}) {
        val request = GetCredentialRequest.Builder().addCredentialOption(googleIdOption).build()
        try {
            onResult(
                handleSignIn(
                    CredentialManager.create(context).getCredential(context, request)
                )
            )
        } catch (e: GetCredentialException) {
            // Handle error
            Log.e(TAG, e.message.toString())
        }
    }

    fun handleSignIn(result: GetCredentialResponse): String {
        when (val credential = result.credential) {
            // Check if the credential is a Google ID Token
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        // Use the helper to parse the credential from the bundle
                        val googleIdTokenCredential =
                            GoogleIdTokenCredential.createFrom(credential.data)
                        val idToken = googleIdTokenCredential.idToken

                        // SUCCESS: Send idToken to your server for validation
                        return idToken
                    } catch (e: GoogleIdTokenParsingException) {
                        Log.e(TAG, "Received invalid google id token response", e)
                    }
                }
            }
            // Handle Passkeys or other types if you added them to your request
            is PublicKeyCredential -> {
                // Handle Passkey result
            }

            else -> {
                Log.e(TAG, "Unexpected credential type")
            }
        }
        return ""
    }
}