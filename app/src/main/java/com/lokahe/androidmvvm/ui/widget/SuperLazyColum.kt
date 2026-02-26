package com.lokahe.androidmvvm.ui.widget

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lokahe.androidmvvm.data.remote.Api.PAGE_SIZE

@Composable
fun <T> SuperLazyColum(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues,
    pageSize: Int = PAGE_SIZE,
    items: List<T>,
    onScroll: (Int) -> Unit = {},
    onRefresh: () -> Unit = {},
    onLoadMore: () -> Unit = {},
    itemContent: @Composable (index: Int, item: T) -> Unit
) {
    val listState = rememberLazyListState()
    val heightMap = remember { mutableMapOf<Int, Int>() }
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
                        (lastVisibleItem.offset + lastVisibleItem.size <= viewportHeight)
                        && items.size % pageSize == 0
            }
        }
    }
    LaunchedEffect(isAtBottom) {
        if (isAtBottom && !isLoadingMore && !isRefreshing) {
            isLoadingMore = true
            onLoadMore()
            isLoadingMore = false
        }
    }
    LaunchedEffect(listState) {
        snapshotFlow {
            Pair(listState.firstVisibleItemIndex, listState.firstVisibleItemScrollOffset)
        }.collect { (index, offset) ->
            if (heightMap[index] == null || heightMap[index]!! < offset) heightMap[index] = offset
            onScroll(heightMap.filterKeys { it < index }.values.sum() + offset)
        }
    }
    PullToRefreshBox(
        modifier = modifier,
        isRefreshing = isRefreshing,
        onRefresh = {
            isRefreshing = true
            onRefresh()
            isRefreshing = false
        },
        indicator = {
            Indicator(
                modifier = Modifier.align(Alignment.TopCenter)
                    .padding(top = paddingValues.calculateTopPadding()),
                isRefreshing = isRefreshing,
                state = pullRefreshState,
            )
        },
        state = pullRefreshState
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize().padding(top = 4.dp),
                contentPadding = paddingValues
            ) {
                itemsIndexed(items) { index, item ->
                    itemContent(index, item)
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
}