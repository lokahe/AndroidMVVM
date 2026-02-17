package com.lokahe.androidmvvm.ui.widget

import android.util.Patterns.EMAIL_ADDRESS
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.autofill.ContentType.Companion.Username
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale.Companion.Crop
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentType
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.lokahe.androidmvvm.AVATARS
import com.lokahe.androidmvvm.AppDialog
import com.lokahe.androidmvvm.LocalViewModel
import com.lokahe.androidmvvm.R
import com.lokahe.androidmvvm.viewmodels.MainViewModel

@Composable
fun MainDialog() {
    val viewModel = LocalViewModel.current as MainViewModel
    val context = LocalContext.current
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
            var emailError by remember { mutableStateOf(false) }
            var verifyCode by remember { mutableStateOf("") }
            val verifyEmail by viewModel.verifyEmail
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
                        if (verifyEmail.isNotEmpty()) {
                            Text(
                                modifier = Modifier.padding(horizontal = 8.dp).weight(1f)
                                    .basicMarquee(iterations = Int.MAX_VALUE),
                                text = verifyEmail,
                                style = MaterialTheme.typography.titleMedium,
                                maxLines = 1,
                                softWrap = false // Ensure text doesn't wrap so it can scroll
                            )
                            IconButton(
                                modifier = Modifier.size(30.dp).padding(0.dp),
                                onClick = { viewModel.resetVerifyEmail() }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.baseline_edit_24),
                                    contentDescription = R.string.edit.toString(),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                },
                text = {
                    Column {
                        if (verifyEmail.isEmpty()) {
                            OutlinedTextField(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .semantics { contentType = Username }
                                    .onFocusChanged { focusState ->
                                        emailError = !focusState.isFocused && email.isNotEmpty()
                                                && !EMAIL_ADDRESS.matcher(email).matches()
                                    },
                                value = email,
                                onValueChange = { email = it; emailError = false },
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
                        } else {
                            OutlinedTextField(
                                modifier = Modifier.fillMaxWidth(),
                                value = verifyCode,
                                onValueChange = {
                                    // Only allow up to 6 digits
                                    if (it.length <= 6 && it.all { char -> char.isDigit() }) {
                                        verifyCode = it
                                        if (verifyCode.length == 6)
                                            viewModel.verifyEmail(verifyEmail, verifyCode)
                                    }
                                },
                                label = { Text("6-Digit Verification Code") },
                                placeholder = { Text("000000") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions.Default.copy(
                                    keyboardType = KeyboardType.NumberPassword
                                )
                            )
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp)
                        ) {
                            IconButton(
                                onClick = { viewModel.loginWithTwitter(context = context) },
                                modifier = Modifier.clip(RoundedCornerShape(8.dp))
                                    .background(Color.Black)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_x), // You need to add ic_google.xml to res/drawable
                                    contentDescription = "X" // Provide a content description for accessibility
                                )
                            }
                            IconButton(
                                onClick = { viewModel.signWithGoogle(context = context) },
                                modifier = Modifier.padding(start = 8.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.White)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_google), // You need to add ic_google.xml to res/drawable
                                    contentDescription = "Google", // Provide a content description for accessibility
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    if (verifyEmail.isEmpty()) {
                        TextButton(
                            enabled = EMAIL_ADDRESS.matcher(email).matches(),
                            onClick = { viewModel.sign(email) }
                        ) {
                            Text(stringResource(R.string.confirm))
                        }
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
                                    AsyncImage(
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