package com.lokahe.androidmvvm.ui.widget

import android.util.Patterns.EMAIL_ADDRESS
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType.Companion.Username
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentType
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import com.lokahe.androidmvvm.R

@Composable
fun EmailField(email: String, onValueChange: (String) -> Unit) {
    var emailError by remember { mutableStateOf(false) }
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .semantics { contentType = Username }
            .onFocusChanged { focusState ->
                emailError = !focusState.isFocused && email.isNotEmpty()
                        && !EMAIL_ADDRESS.matcher(email).matches()
            },
        value = email,
        onValueChange = { onValueChange(it); emailError = false },
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
}