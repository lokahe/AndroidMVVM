package com.lokahe.androidmvvm.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Textsms
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil.compose.AsyncImage
import com.lokahe.androidmvvm.LocalNavController
import com.lokahe.androidmvvm.R
import com.lokahe.androidmvvm.UserHeaderOption
import com.lokahe.androidmvvm.ui.widget.MainScaffold
import com.lokahe.androidmvvm.ui.widget.UserHeader
import com.lokahe.androidmvvm.viewmodels.PostViewModel

@Composable
fun SendPostScreen() {
    val viewModel: PostViewModel = hiltViewModel()
    val navController = LocalNavController.current
    var content by remember { mutableStateOf("") }
    var images by remember { mutableStateOf("") }
    val me by viewModel.currentUser.collectAsState()
    // Launcher for picking local image
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: android.net.Uri? ->
        uri?.let { images += "$it;" }
    }
    MainScaffold(
        title = stringResource(R.string.send_post),
        bottomBar = {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .imePadding()
            ) {
                if (images.isNotEmpty()) {
                    LazyRow(modifier = Modifier.padding(vertical = 8.dp)) {
                        images.split(";").forEach {
                            item {
                                AsyncImage(
                                    model = it,
                                    contentDescription = stringResource(R.string.image),
                                    modifier = Modifier
                                        .padding(end = 8.dp)
                                        .size(80.dp)
                                        .clip(RoundedCornerShape(10))
                                )
                            }
                        }
                    }
                }
                Button(
                    onClick = { launcher.launch("image/*") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(stringResource(R.string.pick_from_gallery))
                }
            }
        }
    ) {
        Column(
            modifier = Modifier.padding(it)
        ) {
            UserHeader(user = me, option = UserHeaderOption.Send) {
                IconButton(
                    modifier = Modifier.padding(start = 16.dp),
                    onClick = {
                        viewModel.sendPost(content, images) {
                            navController.removeLast()
                        }
                    }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = stringResource(R.string.send_post)
                    )
                }
            }
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text(stringResource(R.string.content)) },
                    leadingIcon = { Icon(Icons.Default.Textsms, contentDescription = null) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    minLines = 9
                )
            }
        }
    }
}