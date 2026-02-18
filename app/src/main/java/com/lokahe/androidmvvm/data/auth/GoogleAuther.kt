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
//    val googleWebIdOption by lazy {
//        GetGoogleIdOption.Builder()
//            .setFilterByAuthorizedAccounts(true)
//            .setNonce(generateSecureRandomNonce())
//            .setServerClientId(Api.GOOGLE_WEB_CLIENT_ID)
//            .build()
//    }

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
                        val userEmail = googleIdTokenCredential.id
                        val displayName = googleIdTokenCredential.displayName

                        // SUCCESS: Send idToken to your server for validation
                        Log.d(
                            TAG,
                            "Got ID Token: $idToken for user: $userEmail with name: $displayName"
                        )
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
    /*
        suspend fun gOauthWeb(context: Context) {
            val request = GetCredentialRequest.Builder().addCredentialOption(googleWebIdOption).build()
            // Attempt to sign in with the created request using an authorized account
            val e = signIn(request, context)
            // If the sign-in fails with NoCredentialException,  there are no authorized accounts.
            // In this case, we attempt to sign in again with filtering disabled.
            if (e is NoCredentialException) {
                val googleIdOptionFalse: GetGoogleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(Api.GOOGLE_WEB_CLIENT_ID)
                    .setNonce(generateSecureRandomNonce())
                    .build()

                val requestFalse: GetCredentialRequest = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOptionFalse)
                    .build()

                //We will build out this function in a moment
                signIn(requestFalse, context)
            }
            try {
                val result = CredentialManager.create(context).getCredential(context, request)
                Log.d(TAG, result.toString())
            } catch (e: GetCredentialException) {
                // Handle error
                Log.e(TAG, e.message.toString())
            }
        }

        suspend fun signIn(request: GetCredentialRequest, context: Context): Exception? {
            val credentialManager = CredentialManager.create(context)
            val failureMessage = "Sign in failed!"
            val e: Exception? = null
            //using delay() here helps prevent NoCredentialException when the BottomSheet Flow is triggered
            //on the initial running of our app
            delay(250)
            try {
                // The getCredential is called to request a credential from Credential Manager.
                val result = credentialManager.getCredential(
                    request = request,
                    context = context,
                )
                Log.i(TAG, result.toString())

                Toast.makeText(context, "Sign in successful!", Toast.LENGTH_SHORT).show()
                Log.i(TAG, "(☞ﾟヮﾟ)☞  Sign in Successful!  ☜(ﾟヮﾟ☜)")

            } catch (e: GetCredentialException) {
                Toast.makeText(context, failureMessage, Toast.LENGTH_SHORT).show()
                Log.e(TAG, "$failureMessage: Failure getting credentials", e)

            } catch (e: GoogleIdTokenParsingException) {
                Toast.makeText(context, failureMessage, Toast.LENGTH_SHORT).show()
                Log.e(TAG, "$failureMessage: Issue with parsing received GoogleIdToken", e)

            } catch (e: NoCredentialException) {
                Toast.makeText(context, failureMessage, Toast.LENGTH_SHORT).show()
                Log.e(TAG, "$failureMessage: No credentials found", e)
                return e

            } catch (e: GetCredentialCustomException) {
                Toast.makeText(context, failureMessage, Toast.LENGTH_SHORT).show()
                Log.e(TAG, "$failureMessage: Issue with custom credential request", e)

            } catch (e: GetCredentialCancellationException) {
                Toast.makeText(context, ": Sign-in cancelled", Toast.LENGTH_SHORT).show()
                Log.e(TAG, "$failureMessage: Sign-in was cancelled", e)
            }
            return e
        }


        //This function is used to generate a secure nonce to pass in with our request
        fun generateSecureRandomNonce(byteLength: Int = 32): String {
            val randomBytes = ByteArray(byteLength)
            SecureRandom.getInstanceStrong().nextBytes(randomBytes)
            return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes)
        }
    */
}