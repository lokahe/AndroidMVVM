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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.stringResource
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
            var isEmailError by remember { mutableStateOf(false) }
            var password by remember { mutableStateOf("") }
            var passwordRepeat by remember { mutableStateOf("") }
            var repeatError by remember { mutableStateOf(false) }
            var signUp by remember { mutableStateOf(false) }
            AlertDialog(
                onDismissRequest = { viewModel.dismissDialog() },
                title = { Text(text = stringResource(R.string.signInUp)) },
                text = {
                    Column {
                        OutlinedTextField(
                            value = email,
                            onValueChange = {
                                email = it
                                isEmailError = false
                            },
                            label = {
                                Text(
                                    stringResource(
                                        if (isEmailError) R.string.invalid_email_format
                                        else R.string.email
                                    )
                                )
                            },
                            isError = isEmailError,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                                .onFocusChanged { focusState ->
                                    if (!focusState.isFocused) {
                                        if (EMAIL_ADDRESS.matcher(email).matches()
                                        ) {
                                            signUp = !viewModel.isSignedUp(email)
                                        } else if (email.isNotEmpty()) {
                                            isEmailError = true
                                            signUp = false
                                        }
                                    }
                                },
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text(stringResource(R.string.password)) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password
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
                            viewModel.dismissDialog()
                            // Pass credentials to your ViewModel login function
                            viewModel.login(email, password)
                        },
                        enabled = EMAIL_ADDRESS.matcher(email).matches() && password.isNotEmpty()
                                && ((signUp && password == passwordRepeat) || !signUp)
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