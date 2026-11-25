package com.lokahe.androidmvvm.ui

import android.util.Patterns.EMAIL_ADDRESS
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale.Companion.Crop
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.lokahe.androidmvvm.AppDialog
import com.lokahe.androidmvvm.LocalViewModel
import com.lokahe.androidmvvm.R
import com.lokahe.androidmvvm.ui.widget.OAuthWebViewDialog
import com.lokahe.androidmvvm.ui.widget.OtpInputField
import com.lokahe.androidmvvm.viewmodels.MainViewModel

@Composable
fun MainDialog() {
    val viewModel = LocalViewModel.current as MainViewModel
    val loading by viewModel.loadingState.collectAsState(false)
    viewModel.activeDialogs.forEach {
        when (it) {
            AppDialog.Loading -> LoadingDialog()
            AppDialog.SignIn -> SignInDialog()
            AppDialog.SignOut -> SignOutDialog()
            AppDialog.Avatar -> AvatarSelectDialog()
        }
    }
}

@Composable
fun LoadingDialog() {
    val viewModel = LocalViewModel.current as MainViewModel
    Dialog(
        onDismissRequest = { viewModel.dismissDialog() },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(Color.White, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}

@Composable
fun SignOutDialog() {
    val viewModel = LocalViewModel.current as MainViewModel
    AlertDialog(
        onDismissRequest = { viewModel.dismissDialog() },
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = stringResource(R.string.sign_out)
                )
                Text(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    text = stringResource(R.string.sign_out_confirm),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    viewModel.dismissDialog()
                    viewModel.signOut()
                }
            ) {
                Text(stringResource(R.string.confirm))
            }
        },
        dismissButton = {
            TextButton(
                onClick = { viewModel.dismissDialog() }
            ) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@Composable
fun SignInDialog() {
    val viewModel = LocalViewModel.current as MainViewModel
    var showGithubWebView by remember { mutableStateOf(false) }
    var showGoogleWebView by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // Step 2 of the unified email entry is owned by the ViewModel
    // (a 202 from email-entry means the verification email is out).
    val codeSent by viewModel.entryCodeSent.collectAsState()
    var code by remember { mutableStateOf("") }

    val emailValid = EMAIL_ADDRESS.matcher(email).matches()
    val passwordValid = password.isEmpty() || password.length in 8..128
    val canSubmit = if (codeSent) code.length == 6 else emailValid && passwordValid

    AlertDialog(
        onDismissRequest = { viewModel.dismissDialog() },
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Login,
                    contentDescription = stringResource(R.string.sign)
                )
                Text(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    text = stringResource(R.string.signInUp),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        },
        text = {
            Column {
                if (codeSent) {
                    Text(
                        text = stringResource(R.string.code_sent_to, viewModel.pendingEntryEmailFlow ?: email),
                        style = MaterialTheme.typography.bodySmall
                    )
                    OtpInputField(modifier = Modifier.fillMaxWidth()) { code = it }
                } else {
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = email,
                        onValueChange = { email = it },
                        label = {
                            Text(
                                stringResource(
                                    if (email.isEmpty() || emailValid) R.string.email
                                    else R.string.invalid_email_format
                                )
                            )
                        },
                        isError = email.isNotEmpty() && !emailValid,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true
                    )
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = password,
                        onValueChange = { password = it },
                        label = {
                            Text(
                                stringResource(
                                    if (passwordValid) R.string.password
                                    else R.string.invalid_password_format
                                )
                            )
                        },
                        isError = !passwordValid,
                        visualTransformation =
                            if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Filled.Visibility
                                    else Icons.Filled.VisibilityOff,
                                    contentDescription = null
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true
                    )
                    TextButton(onClick = { viewModel.requestPasswordReset(email) }) {
                        Text(
                            stringResource(R.string.forgot_password),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Row(modifier = Modifier.fillMaxWidth().padding(top = 4.dp)) {
                        IconButton(
                            onClick = { showGithubWebView = true },
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.background)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_github),
                                contentDescription = "GitHub",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        IconButton(
                            onClick = { showGoogleWebView = true },
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.background)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_google),
                                contentDescription = "Google",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
                if (codeSent) {
                    TextButton(onClick = {
                        code = ""
                        viewModel.resetEntryCodeStep()
                    }) {
                        Text(
                            stringResource(R.string.back_to_entry),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = canSubmit,
                onClick = {
                    if (codeSent) {
                        viewModel.confirmEntryCode(code)
                    } else {
                        viewModel.signIn(email, password)
                    }
                }
            ) {
                Text(stringResource(if (codeSent) R.string.confirm else R.string.next))
            }
        },
        dismissButton = {
            TextButton(
                onClick = { viewModel.dismissDialog() }
            ) {
                Text(stringResource(R.string.cancel))
            }
        }
    )

    if (showGithubWebView) {
        OAuthWebViewDialog(provider = "github", title = "Sign in with GitHub", onDismiss = { showGithubWebView = false })
    }
    if (showGoogleWebView) {
        OAuthWebViewDialog(provider = "google", title = "Sign in with Google", onDismiss = { showGoogleWebView = false })
    }
}

/** Provider raw id → display name (same mapping as AccountScreen). */
internal fun platformName(provider: String): String = when (provider) {
    "github" -> "GitHub"
    "magic" -> "Magic Link"
    else -> provider
}

@Composable
fun AvatarSelectDialog() {
    val viewModel = LocalViewModel.current as MainViewModel
    val candidates by viewModel.identityAvatars.collectAsState()
    val user by viewModel.currentUser.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadIdentityAvatars() }

    AlertDialog(
        onDismissRequest = { viewModel.dismissDialog() },
        title = { Text(text = stringResource(R.string.preset_avatar)) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(R.string.avatar_pick_hint),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                if (candidates.isEmpty()) {
                    Text(
                        text = stringResource(R.string.no_identities),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    val current = user?.profile?.avatar
                    candidates.forEach { identity ->
                        val selected = current != null && current == identity.avatarUrl
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    viewModel.updateAvatar(identity.avatarUrl!!)
                                    viewModel.dismissDialog()
                                }
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = identity.avatarUrl,
                                contentDescription = platformName(identity.provider),
                                modifier = Modifier
                                    .padding(end = 12.dp)
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(22.dp))
                                    .border(
                                        width = if (selected) 2.dp else 0.dp,
                                        color = MaterialTheme.colorScheme.primary,
                                        shape = RoundedCornerShape(22.dp)
                                    ),
                                contentScale = Crop
                            )
                            Column {
                                Text(
                                    text = platformName(identity.provider),
                                    style = MaterialTheme.typography.titleMedium
                                )
                                if (selected) {
                                    Text(
                                        text = stringResource(R.string.avatar_current),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {}
    )
}