package com.lokahe.androidmvvm.ui.widget

import android.annotation.SuppressLint
import android.webkit.WebResourceRequest
import android.webkit.WebView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.lokahe.backend.rovortex.Api
import com.lokahe.androidmvvm.LocalViewModel
import com.lokahe.androidmvvm.viewmodels.MainViewModel

/**
 * In-app GitHub OAuth flow. The rovortex gateway renders the session JSON
 * directly at its callback URL (no redirect back to the app), so the WebView
 * intercepts that request, grabs the body and closes the dialog.
 */
object GithubWebViewContract {
    const val CALLBACK_PREFIX = "api/auth/oauth/github/callback"
}

@Composable
fun GithubWebViewDialog(onDismiss: () -> Unit) {
    val viewModel = LocalViewModel.current as MainViewModel
    var resultJson by remember { mutableStateOf<String?>(null) }
    var errorText by remember { mutableStateOf<String?>(null) }

    resultJson?.let { json ->
        // Deliver once, then close.
        remember(json) { viewModel.onGitHubCallback(json) }
        onDismiss()
        return
    }

    if (errorText != null) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("GitHub sign-in failed") },
            text = { Text(errorText ?: "") },
            confirmButton = {
                TextButton(onClick = onDismiss) { Text("OK") }
            }
        )
        return
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Sign in with GitHub") },
        text = {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { context ->
                    WebView(context).apply {
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        @SuppressLint("SetJavaScriptEnabled")
                        webViewClient = object : android.webkit.WebViewClient() {
                            // Let every navigation (including the callback page)
                            // load normally — the capture happens in onPageFinished.
                            override fun shouldOverrideUrlLoading(
                                view: WebView?,
                                request: WebResourceRequest?
                            ): Boolean = false

                            override fun onPageFinished(view: WebView?, url: String?) {
                                if (url != null && url.contains(GithubWebViewContract.CALLBACK_PREFIX)) {
                                    // The gateway renders the AuthResponse (or error
                                    // envelope) as the page body — capture it.
                                    view?.evaluateJavascript(
                                        "document.body.innerText"
                                    ) { value ->
                                        val body = value
                                            ?.removeSurrounding("\"")
                                            ?.replace("\\\"", "\"")
                                            ?.replace("\\\\", "\\")
                                            ?.trim()
                                            .orEmpty()
                                        if (body.contains("\"token\"")) {
                                            resultJson = body
                                        } else {
                                            errorText = if (body.isEmpty())
                                                "Callback returned an empty response"
                                            else body
                                        }
                                    }
                                }
                            }
                        }
                        loadUrl(Api.authorizeUrl("github"))
                    }
                }
            )
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
