package com.lokahe.androidmvvm.ui.screens

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.lokahe.androidmvvm.HOME_TABS
import com.lokahe.androidmvvm.LocalNavController
import com.lokahe.androidmvvm.LocalViewModel
import com.lokahe.androidmvvm.R
import com.lokahe.androidmvvm.px2Dp
import com.lokahe.androidmvvm.ui.widget.MainScaffold
import com.lokahe.androidmvvm.viewmodels.MainViewModel

@Composable
fun MainScreen() {
    val viewModel = LocalViewModel.current as MainViewModel
    val isLoggedIn by viewModel.isSignedIn.collectAsState()
    val navController = LocalNavController.current
    var curTab by remember { mutableIntStateOf(0) }
    var alpha by remember { mutableFloatStateOf(1f) }
    MainScaffold(
        title = stringResource(R.string.app_name),
        alpha = alpha,
        bottomBar = bottomTabNavigation(
            curTabIndex = curTab,
            alpha = alpha,
            onTabSelected = { curTab = it }
        ),
        floatingActionButton = {
            if (isLoggedIn) {
                FloatingActionButton(
                    onClick = { navController.add(Screen.SendPost) },
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = alpha)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.add)
                    )
                }
            }
        }
    ) { contentPadding ->
        TabScreen(
            curTabIndex = curTab,
            onTabSelected = { curTab = it },
            HOME_TABS,
            showTab = false
        ) { selectedTabIndex ->
            when (selectedTabIndex) {
                0 -> PostsScreen(contentPadding) {
                    alpha = (1f - it.px2Dp() / 300f).coerceAtLeast(0.5f)
                }

                1 -> ExploreScreen(contentPadding)
                2 -> UsersScreen(contentPadding)
            }
        }
    }
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
                            0 -> Icons.Filled.Home
                            1 -> Icons.Filled.Image
                            2 -> Icons.Filled.People
                            else -> Icons.Filled.Home
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