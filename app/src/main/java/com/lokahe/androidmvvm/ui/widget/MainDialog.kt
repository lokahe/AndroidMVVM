package com.lokahe.androidmvvm.ui.widget

import android.util.Patterns.EMAIL_ADDRESS
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType.Companion.Password
import androidx.compose.ui.autofill.ContentType.Companion.Username
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalAutofillManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentType
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.lokahe.androidmvvm.LocalViewModel
import com.lokahe.androidmvvm.R
import com.lokahe.androidmvvm.viewmodels.AppDialog
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
                                .padding(bottom = 8.dp)
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

        else -> {}
    }
}