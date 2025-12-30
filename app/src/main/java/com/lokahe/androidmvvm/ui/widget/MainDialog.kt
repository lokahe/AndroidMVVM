package com.lokahe.androidmvvm.ui.widget

import android.util.Patterns.EMAIL_ADDRESS
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType.Companion.Password
import androidx.compose.ui.autofill.ContentType.Companion.Username
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale.Companion.Crop
import androidx.compose.ui.platform.LocalAutofillManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentType
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.lokahe.androidmvvm.AVATARS
import com.lokahe.androidmvvm.AppDialog
import com.lokahe.androidmvvm.LocalViewModel
import com.lokahe.androidmvvm.R
import com.lokahe.androidmvvm.viewmodels.MainViewModel

@Composable
fun MainDialog() {
    val viewModel = LocalViewModel.current as MainViewModel
    val activeDialog by viewModel.activeDialog
    when (activeDialog) {
        is AppDialog.Logout -> {
            AlertDialog(
                onDismissRequest = { viewModel.dismissDialog() },
                title = { Text(text = stringResource(R.string.logout)) },
                text = { Text(text = "Are you sure you want to logout?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.dismissDialog()
                            viewModel.logout()
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

        is AppDialog.Login -> {
            var email by remember { mutableStateOf("") }
            var name by remember { mutableStateOf("") }
            var password by remember { mutableStateOf("") }
            var passwordRepeat by remember { mutableStateOf("") }
            var emailError by remember { mutableStateOf(false) }
            var repeatError by remember { mutableStateOf(false) }
            val signUp by viewModel.isNewAccount
            val autofillManager = LocalAutofillManager.current
            AlertDialog(
                onDismissRequest = { viewModel.dismissDialog() },
                title = { Text(text = stringResource(R.string.signInUp)) },
                text = {
                    Column {
                        OutlinedTextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .semantics { contentType = Username }
                                .onFocusChanged { focusState ->
                                    if (!focusState.isFocused) {
                                        if (EMAIL_ADDRESS.matcher(email).matches()) {
                                            viewModel.isNewAccount(email)
                                        } else if (email.isNotEmpty()) {
                                            emailError = true
                                            viewModel.resetNewAccountCheck()
                                        }
                                    }
                                },
                            value = email,
                            onValueChange = {
                                email = it
                                emailError = false
                                viewModel.resetNewAccountCheck()
                            },
                            label = {
                                Text(
                                    stringResource(
                                        if (emailError) R.string.invalid_email_format
                                        else R.string.email
                                    )
                                )
                            },
                            isError = emailError,
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Email, // or default
                                autoCorrectEnabled = false // Recommended for usernames/emails
                            ),
                            singleLine = true
                        )
                        if (signUp) {
                            if (name.isEmpty() && EMAIL_ADDRESS.matcher(email).matches()) {
                                name = email.subSequence(0, email.indexOf("@")) as String
                            }
                            OutlinedTextField(
                                value = name,
                                onValueChange = { name = it },
                                label = { Text(stringResource(R.string.name)) },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Text
                                )
                            )
                        }
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text(stringResource(R.string.password)) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .semantics { contentType = Password },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Password,
                                autoCorrectEnabled = false // Important: Turn off dictionary for passwords
                            )
                        )
                        if (signUp) {
                            OutlinedTextField(
                                value = passwordRepeat,
                                onValueChange = {
                                    passwordRepeat = it
                                    repeatError = false
                                },
                                label = { Text(stringResource(R.string.password_repeat)) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .onFocusChanged { focusState ->
                                        if (!focusState.isFocused && passwordRepeat.isNotEmpty()) {
                                            repeatError = password != passwordRepeat
                                        }
                                    },
                                singleLine = true,
                                isError = repeatError,
                                visualTransformation = PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Password
                                )
                            )
                        }
//                        Row(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .padding(top = 8.dp)
//                        ) {
//                            IconButton(onClick = {
//
//                            }) {
//                                Icon(
//                                    painter = painterResource(id = R.drawable.ic_google), // You need to add ic_google.xml to res/drawable
//                                    contentDescription = "Google" // Provide a content description for accessibility
//                                )
//                            }
//                        }
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            // Pass credentials to your ViewModel login function
                            if (signUp) viewModel.signUp(email, password, name)
                            else viewModel.login(email, password)
                            autofillManager?.commit()
//                            CredentialsClient.save()
                        },
                        enabled = EMAIL_ADDRESS.matcher(email).matches() && password.isNotEmpty()
                                && ((signUp && password == passwordRepeat && name.isNotEmpty()) || !signUp)
                    ) {
                        Text(stringResource(if (signUp) R.string.sign_up else R.string.sign_in))
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

        AppDialog.Avatar -> {
            // Launcher for picking local image
            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.GetContent()
            ) { uri: android.net.Uri? ->
                uri?.let {
                    viewModel.updateAvatar(it.toString())
                    viewModel.dismissDialog()
                }
            }

            AlertDialog(
                onDismissRequest = { viewModel.dismissDialog() },
                title = { Text(text = "Select Avatar") },
                text = {
                    Column {
                        // 1. Grid of Predefined Avatars
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(4),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(AVATARS.size) { index ->
                                val url = AVATARS[index]
                                Box(
                                    modifier = Modifier
                                        .aspectRatio(1f)
                                        .clip(RoundedCornerShape(20))
                                        .clickable {
                                            viewModel.updateAvatar(url)
                                            viewModel.dismissDialog()
                                        }
                                ) {
                                    coil.compose.AsyncImage(
                                        model = url,
                                        contentDescription = "Avatar $index",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = Crop
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // 2. Button to pick from Gallery
                        Button(
                            onClick = { launcher.launch("image/*") },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = null,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text(stringResource(R.string.pick_from_gallery))
                        }
                    }
                },
                confirmButton = {} // No confirm needed, clicking an image selects it immediately
            )
        }

        is AppDialog.Loading -> {
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

        else -> {}
    }
}