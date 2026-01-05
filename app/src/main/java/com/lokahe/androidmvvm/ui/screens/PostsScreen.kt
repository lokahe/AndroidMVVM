package com.lokahe.androidmvvm.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lokahe.androidmvvm.LocalViewModel
import com.lokahe.androidmvvm.PAGE_SIZE
import com.lokahe.androidmvvm.ui.widget.PostItem
import com.lokahe.androidmvvm.viewmodels.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostsScreen(
    paddingValues: PaddingValues,
    ownerId: String = ""
) {
    val viewModel = LocalViewModel.current as MainViewModel
    val posts by if (ownerId.isEmpty()) viewModel.posts.collectAsState()
    else viewModel.myPosts.collectAsState()
    if (ownerId.isNotEmpty() && posts.isEmpty()) {
        viewModel.fetchPosts(PAGE_SIZE, 0, ownerId)
    }
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
            viewModel.fetchPosts(PAGE_SIZE, posts.size)
            isLoadingMore = false
        }
    }

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = {
            isRefreshing = true
            viewModel.fetchPosts(PAGE_SIZE, 0)
            isRefreshing = false
        },
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .fillMaxSize(),
        state = pullRefreshState
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 8.dp),
                contentPadding = paddingValues
            ) {
                itemsIndexed(posts) { index, user ->
                    PostItem(index, user)
                }

                if (isLoadingMore) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }
        }
    }
}