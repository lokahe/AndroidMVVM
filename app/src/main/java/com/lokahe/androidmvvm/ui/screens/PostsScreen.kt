package com.lokahe.androidmvvm.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.lokahe.androidmvvm.AppDialog
import com.lokahe.androidmvvm.LocalNavController
import com.lokahe.androidmvvm.R
import com.lokahe.androidmvvm.data.models.supabase.liked
import com.lokahe.androidmvvm.data.remote.Api.PAGE_SIZE
import com.lokahe.androidmvvm.ui.widget.PostItem
import com.lokahe.androidmvvm.ui.widget.SuperLazyColum
import com.lokahe.androidmvvm.viewmodels.PostViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostsScreen(
    paddingValues: PaddingValues,
    authorId: String? = null,
    onScroll: (Int) -> Unit = {}
) {
    val viewModel: PostViewModel = hiltViewModel()
    val navController = LocalNavController.current
    val me by viewModel.currentUser.collectAsState()
    val isMe = authorId != null && authorId == me?.id
    val posts by remember(authorId) { if (authorId.isNullOrEmpty()) viewModel.posts else viewModel.userPosts }.collectAsState()
    val selectedIndexes = remember { mutableStateSetOf<Int>() }
    LaunchedEffect(authorId) { viewModel.fetchPosts(PAGE_SIZE, 0, authorId) }
    var editMode by remember { mutableStateOf(false) }

    Column {
        SuperLazyColum(
            modifier = Modifier.padding(horizontal = 8.dp).fillMaxWidth().weight(1f),
            paddingValues = paddingValues,
            items = posts,
            onScroll = onScroll,
            onRefresh = { viewModel.fetchPosts(PAGE_SIZE, 0, authorId) },
            onLoadMore = { viewModel.fetchPosts(PAGE_SIZE, posts.size, authorId) },
        ) { index, post ->
            val liked = me?.liked(post.id) ?: false
            PostItem(
                index = index,
                post = post,
                liked = liked,
                editMode = editMode,
                selected = selectedIndexes.contains(index),
                onLongClick = { if (isMe) editMode = !editMode },
                onAuthorClick = { userId ->
                    if (userId != authorId) navController.add(Screen.Account(userId))
                },
                onLikeClick = { viewModel.toggleLike(it, liked) }
            ) {
                if (editMode) {
                    if (selectedIndexes.contains(index)) selectedIndexes.remove(index)
                    else selectedIndexes.add(index)
                } else {
                    navController.add(Screen.PostDetail(post))
                }
            }
        }
        if (editMode) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    modifier = Modifier.padding(8.dp).weight(1f),
                    enabled = !selectedIndexes.isEmpty(),
                    onClick = {
                        viewModel.showDialog(AppDialog.Delete {
                            viewModel.deletePost(posts.filterIndexed { index, _ ->
                                selectedIndexes.contains(index)
                            }.map { it.id })
                        })
                    }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(R.string.delete)
                    )
                    Text(text = stringResource(R.string.delete))
                }
                Button(
                    modifier = Modifier.padding(8.dp).weight(1f),
                    onClick = { editMode = false }) {
                    Icon(
                        imageVector = Icons.Default.Cancel,
                        contentDescription = stringResource(R.string.cancel)
                    )
                    Text(text = stringResource(R.string.cancel))
                }
            }
        }
    }
}