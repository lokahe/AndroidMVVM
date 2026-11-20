package com.lokahe.androidmvvm.ui.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lokahe.androidmvvm.HOME_TABS

@Composable
fun TabScreen(
    curTabIndex: Int,
    onTabSelected: (Int) -> Unit = {},
    tabs: List<String> = HOME_TABS,
    showTab: Boolean = false,
    contentPadding: PaddingValues = PaddingValues(),
    content: @Composable (Int) -> Unit
) {
    content(curTabIndex)
}

@Composable
fun ExploreScreen(contentPadding: PaddingValues) {
    // Gallery grid was post-related and removed; placeholder keeps the tab alive.
    androidx.compose.foundation.layout.Box(
        modifier = Modifier.fillMaxWidth().height(1.dp)
    )
}

fun bottomTabNavigation(
    curTabIndex: Int,
    alpha: Float = 1f,
    onTabSelected: (Int) -> Unit = {}
): @Composable () -> Unit = {
    NavigationBar(
        modifier = Modifier.fillMaxWidth().height(80.dp),
        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = alpha)
    ) {
        HOME_TABS.forEachIndexed { index, title ->
            NavigationBarItem(
                modifier = Modifier.height(65.dp),
                icon = {
                    Icon(
                        when (index) {
                            0 -> Icons.Filled.Image
                            else -> Icons.Filled.People
                        },
                        contentDescription = title
                    )
                },
                selected = curTabIndex == index,
                onClick = { onTabSelected(index) }
            )
        }
    }
}
