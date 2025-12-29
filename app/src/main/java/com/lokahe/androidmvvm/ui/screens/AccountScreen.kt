package com.lokahe.androidmvvm.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.lokahe.androidmvvm.R
import com.lokahe.androidmvvm.ui.widget.MainScaffold

@Composable
fun AccountScreen() {
    MainScaffold(
        title = stringResource(R.string.account)
    ) {
        TabScreen(it)
    }
}