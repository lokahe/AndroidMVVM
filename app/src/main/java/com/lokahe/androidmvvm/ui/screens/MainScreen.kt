package com.lokahe.androidmvvm.ui.screens

import androidx.compose.runtime.Composable
import com.lokahe.androidmvvm.Screen
import com.lokahe.androidmvvm.ui.activites.MainScaffold

@Composable
fun MainScreen() {
    MainScaffold(bottomBar = bottomTabNavigation()) {
        TabScreen(it, false)
    }
}