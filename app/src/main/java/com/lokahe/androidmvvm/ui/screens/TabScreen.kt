package com.lokahe.androidmvvm.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale.Companion.Crop
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.lokahe.androidmvvm.GALLERIES

@Composable
fun TabScreen(
    selectedTabIndexState: State<Int>,
    onTabSelected: (Int) -> Unit = {},
    tabs: List<String>,
    showTab: Boolean = true,
    content: @Composable (Int) -> Unit
) {
    val selectedTabIndex by selectedTabIndexState
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Tab Row
        if (showTab) {
            PrimaryTabRow(selectedTabIndex = selectedTabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { onTabSelected(index) },
                        text = { Text(title) }
                    )
                }
            }
        }
        // Tab Content
        content(selectedTabIndex)
    }
}

@Composable
fun ExploreScreen(contentPadding: PaddingValues) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 1.dp)
    ) {
        LazyColumn(
            contentPadding = contentPadding
        ) {
            for (i in 0..GALLERIES.size - 3 step 2) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(1.dp)
                    ) {
                        repeat(2) {
                            Box(
                                modifier = Modifier.weight(1f)
                            ) {
                                AsyncImage(
                                    modifier = Modifier.fillMaxSize(),
                                    model = GALLERIES[i + it],
                                    contentDescription = null,
                                    contentScale = Crop
                                )
                                Row(
                                    modifier = Modifier
                                        .align(Alignment.BottomCenter)
                                        .background(
                                            brush = Brush.linearGradient(
                                                colors = listOf(
                                                    Color(0x00000000),
                                                    Color(0xFF000000)
                                                ),
                                                start = Offset(x = 0f, y = 0f),
                                                end = Offset(x = 0f, y = Float.POSITIVE_INFINITY)
                                            )
                                        )
                                        .fillMaxWidth()
                                        .height(60.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Share,
                                        contentDescription = "Share",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .weight(1f)
                                            .size(20.dp),
                                        tint = Color.White
                                    )
                                    Icon(
                                        imageVector = Icons.Filled.ThumbUp,
                                        contentDescription = "Thumb up",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .weight(1f)
                                            .size(20.dp),
                                        tint = Color.White
                                    )
                                    Icon(
                                        imageVector = Icons.Filled.Star,
                                        contentDescription = "Star",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .weight(1f)
                                            .size(20.dp),
                                        tint = Color.White
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(1.dp))
                }
            }
        }
    }
}
