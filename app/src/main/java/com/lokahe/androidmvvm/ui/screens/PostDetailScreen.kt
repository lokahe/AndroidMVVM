package com.lokahe.androidmvvm.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lokahe.androidmvvm.LocalNavController
import com.lokahe.androidmvvm.R
import com.lokahe.androidmvvm.copy
import com.lokahe.androidmvvm.data.models.supabase.Post
import com.lokahe.androidmvvm.data.models.supabase.liked
import com.lokahe.androidmvvm.data.remote.Api.PAGE_SIZE
import com.lokahe.androidmvvm.ui.widget.MainScaffold
import com.lokahe.androidmvvm.ui.widget.PostItem
import com.lokahe.androidmvvm.ui.widget.SuperLazyColum
import com.lokahe.androidmvvm.viewmodels.PostViewModel

@Composable
fun PostDetailScreen(post: Post) {
    val viewModel = viewModel<PostViewModel>()
    val navController = LocalNavController.current
    val replyPosts by viewModel.replyPosts.collectAsState()
    LaunchedEffect(post) { viewModel.fetchReplyPosts(PAGE_SIZE, 0, post.id) }
    val me by viewModel.currentUser.collectAsState()
    val liked = me?.profile?.likedList?.any { it.postId == post.id } ?: false
    var replying by remember { mutableStateOf(false) }
    var replyContent by remember { mutableStateOf("") }
    var images by remember { mutableStateOf("") }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: android.net.Uri? ->
        uri?.let { images += "$it;" }
    }
    MainScaffold(title = stringResource(R.string.post)) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(top = paddingValues.calculateTopPadding())
                .padding(horizontal = 8.dp)
        ) {
            PostItem(
                index = 0, post = post, liked = liked, editMode = false,
                onAuthorClick = { navController.add(Screen.Account(it)) },
                onLikeClick = { viewModel.toggleLike(it, liked) }
            )
            SuperLazyColum(
                modifier = Modifier.padding(start = 8.dp).fillMaxWidth().weight(1f),
                paddingValues = paddingValues.copy(vertical = 0.dp),
                items = replyPosts,
                onRefresh = { viewModel.fetchReplyPosts(PAGE_SIZE, 0, post.id) },
                onLoadMore = { viewModel.fetchReplyPosts(PAGE_SIZE, replyPosts.size, post.id) },
            ) { index, post ->
                val liked = me?.liked(post.id) ?: false
                PostItem(
                    index = index,
                    post = post,
                    liked = liked,
                    onAuthorClick = { userId -> navController.add(Screen.Account(userId)) },
                    onLikeClick = { viewModel.toggleLike(it, liked) }
                ) { navController.add(Screen.PostDetail(post)) }
            }
            Row(
                modifier = Modifier.height(IntrinsicSize.Min).fillMaxWidth()
                    .padding(paddingValues.copy(top = 8.dp)).imePadding(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!replying) {
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { replying = true }) {
                        Text(text = stringResource(R.string.tap_to_reply))
                    }
                } else {
                    OutlinedTextField(
                        modifier = Modifier.fillMaxHeight().weight(1f),
                        value = replyContent,
                        onValueChange = { replyContent = it },
                        label = { Text(stringResource(R.string.reply)) },
                    )
                    Column(modifier = Modifier.padding(start = 8.dp)) {
                        Button(onClick = { replying = false }) {
                            Icon(
                                imageVector = Icons.Default.Cancel,
                                contentDescription = stringResource(R.string.cancel)
                            )
                        }
                        Button(onClick = { launcher.launch("image/*") }) {
                            Icon(
                                imageVector = Icons.Filled.Image,
                                contentDescription = stringResource(R.string.image)
                            )
                        }
                        Button(onClick = {
                            viewModel.sendPost(replyContent, reply2PostId = post.id) {
                                replyContent = ""
                                replying = false
                            }
                        }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = stringResource(R.string.send)
                            )
                        }
                    }
                }
            }
        }
    }
}