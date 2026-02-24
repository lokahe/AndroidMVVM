package com.lokahe.androidmvvm.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
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
import com.lokahe.androidmvvm.R
import com.lokahe.androidmvvm.data.remote.Api.PAGE_SIZE
import com.lokahe.androidmvvm.ui.widget.PostItem
import com.lokahe.androidmvvm.viewmodels.PostViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostsScreen(
    paddingValues: PaddingValues,
    userId: String? = null
) {
    val viewModel: PostViewModel = hiltViewModel()
    val posts by remember(userId) { if (userId.isNullOrEmpty()) viewModel.posts else viewModel.myPosts }.collectAsState()
    val selectedIndexes = remember { mutableStateSetOf<Int>() }
    LaunchedEffect(userId) {
        viewModel.fetchPosts(PAGE_SIZE, 0, userId)
    }
    var editMode by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    // Refresh state
    var isRefreshing by remember { mutableStateOf(false) }
    val pullRefreshState = rememberPullToRefreshState()
    // Load more state
    var isLoadingMore by remember { mutableStateOf(false) }
    // Detect end of list for load more
    val isAtBottom by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val visibleItemsInfo = layoutInfo.visibleItemsInfo
            if (layoutInfo.totalItemsCount == 0) {
                false
            } else {
                val lastVisibleItem = visibleItemsInfo.last()
                val viewportHeight = layoutInfo.viewportEndOffset + layoutInfo.viewportStartOffset
                (lastVisibleItem.index + 1 == layoutInfo.totalItemsCount) &&
                        (lastVisibleItem.offset + lastVisibleItem.size <= viewportHeight) &&
                        posts.size % PAGE_SIZE == 0
            }
        }
    }

    LaunchedEffect(isAtBottom) {
        if (isAtBottom && !isLoadingMore && !isRefreshing) {
            isLoadingMore = true
            viewModel.fetchPosts(PAGE_SIZE, posts.size, userId)
            isLoadingMore = false
        }
    }

    Column {
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                isRefreshing = true
                viewModel.fetchPosts(PAGE_SIZE, 0, userId)
                isRefreshing = false
            },
            modifier = Modifier.padding(horizontal = 8.dp).fillMaxWidth().weight(1f),
            state = pullRefreshState
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize().padding(top = 8.dp),
                    contentPadding = paddingValues
                ) {
                    itemsIndexed(posts) { index, post ->
                        PostItem(
                            index, post, editMode, selectedIndexes.contains(index),
                            { editMode = !editMode }) {
                            if (editMode) {
                                if (selectedIndexes.contains(index)) selectedIndexes.remove(index)
                                else selectedIndexes.add(index)
                            }
                        }
                    }

                    if (isLoadingMore) {
                        item {
                            Box(
                                modifier = Modifier.fillMaxSize().padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }
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
                    onClick = {
//                        viewModel.showDialog(AppDialog.Loading)
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