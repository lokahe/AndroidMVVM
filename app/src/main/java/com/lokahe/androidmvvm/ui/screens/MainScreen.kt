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
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.lokahe.androidmvvm.HOME_TABS
import com.lokahe.androidmvvm.LocalNavController
import com.lokahe.androidmvvm.LocalViewModel
import com.lokahe.androidmvvm.R
import com.lokahe.androidmvvm.Screen
import com.lokahe.androidmvvm.ui.widget.MainScaffold
import com.lokahe.androidmvvm.viewmodels.MainViewModel

@Composable
fun MainScreen() {
    val viewModel = LocalViewModel.current as MainViewModel
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val navController = LocalNavController.current
    MainScaffold(
        title = stringResource(R.string.app_name),
        bottomBar = bottomTabNavigation(
            selectedTabIndexState = viewModel.homeTabIndex,
            onTabSelected = { index -> viewModel.setHomeTabIndex(index) }
        ),
        floatingActionButton = {
            if (isLoggedIn) {
                FloatingActionButton(
                    onClick = { navController.navigate(Screen.SendPost.route) },
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
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
            selectedTabIndexState = viewModel.homeTabIndex,
            onTabSelected = { index -> viewModel.setHomeTabIndex(index) },
            HOME_TABS,
            showTab = false
        ) { selectedTabIndex ->
            when (selectedTabIndex) {
                0 -> PostsScreen(contentPadding)
                1 -> ExploreScreen(contentPadding)
                2 -> UsersScreen(contentPadding)
            }
        }
    }
}

fun bottomTabNavigation(
    selectedTabIndexState: State<Int>,
    onTabSelected: (Int) -> Unit = {},
): @Composable () -> Unit = {
    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .height(75.dp),
        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
    ) {
        val selectedTabIndex by selectedTabIndexState
        HOME_TABS.forEachIndexed { index, title ->
            NavigationBarItem(
                modifier = Modifier.height(60.dp),
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
                selected = selectedTabIndex == index,
                onClick = { onTabSelected(index) }
            )
        }
    }
}