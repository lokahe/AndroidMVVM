package com.lokahe.androidmvvm.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.lokahe.androidmvvm.HOME_TABS
import com.lokahe.androidmvvm.LocalViewModel
import com.lokahe.androidmvvm.R
import com.lokahe.androidmvvm.ui.widget.MainScaffold
import com.lokahe.androidmvvm.viewmodels.MainViewModel

@Composable
fun MainScreen() {
    val viewModel = LocalViewModel.current as MainViewModel
    var curTab by remember { mutableIntStateOf(0) }
    MainScaffold(
        title = stringResource(R.string.app_name),
        bottomBar = bottomTabNavigation(
            curTabIndex = curTab,
            onTabSelected = { curTab = it }
        )
    ) { contentPadding ->
        TabScreen(
            curTabIndex = curTab,
            onTabSelected = { curTab = it },
            HOME_TABS,
            showTab = false
        ) { selectedTabIndex ->
            when (selectedTabIndex) {
                0 -> ExploreScreen(contentPadding)
                else -> PersonsScreen()
            }
        }
    }
}
