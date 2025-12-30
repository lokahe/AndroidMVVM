package com.lokahe.androidmvvm.ui.widget

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale.Companion.Crop
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.lokahe.androidmvvm.AppDialog
import com.lokahe.androidmvvm.LocalViewModel
import com.lokahe.androidmvvm.R
import com.lokahe.androidmvvm.Screen
import com.lokahe.androidmvvm.viewmodels.MainViewModel

@Composable
fun UserHeader(onItemSelected: (Screen) -> Unit = {}) {
    val viewModel = LocalViewModel.current as MainViewModel
    val user by viewModel.currentUser.collectAsState()
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = {
                if (isLoggedIn) onItemSelected(Screen.Account)
                else viewModel.showDialog(AppDialog.Login)
            }),
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = CenterVertically
        ) {
            if (user?.avatar?.isNotEmpty() == true) {
                AsyncImage(
                    model = user?.avatar,
                    contentDescription = stringResource(R.string.avatar),
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .size(50.dp)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(20))
                        .clickable(onClick = {
                            if (isLoggedIn) viewModel.showDialog(AppDialog.Avatar)
                            else viewModel.showDialog(AppDialog.Login)
                        }),
                    contentScale = Crop
                )
            } else {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = "",
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .size(50.dp)
                        .clickable(onClick = {
                            if (isLoggedIn) viewModel.showDialog(AppDialog.Avatar)
                            else viewModel.showDialog(AppDialog.Login)
                        })
                )
            }
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .weight(1f)
            ) {
                Text(
                    text = user?.name ?: stringResource(R.string.guest),
                    modifier = Modifier.padding(bottom = 4.dp),
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = user?.email ?: stringResource(R.string.signInUp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            IconButton(
                modifier = Modifier.padding(start = 16.dp),
                onClick = {
                    viewModel.showDialog(if (isLoggedIn) AppDialog.Logout else AppDialog.Login)
                }) {
                Icon(
                    imageVector = if (isLoggedIn) Icons.AutoMirrored.Filled.Logout
                    else Icons.AutoMirrored.Filled.Login,
                    contentDescription = stringResource(
                        if (isLoggedIn) R.string.logout else R.string.login
                    )
                )
            }
        }
    }
}