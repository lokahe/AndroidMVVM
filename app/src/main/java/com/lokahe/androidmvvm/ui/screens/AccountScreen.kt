package com.lokahe.androidmvvm.ui.screens

import androidx.compose.runtime.Composable
import com.lokahe.androidmvvm.ui.activites.MainScaffold

@Composable
fun AccountScreen() {
    MainScaffold() {
        TabScreen(it)
    }
}