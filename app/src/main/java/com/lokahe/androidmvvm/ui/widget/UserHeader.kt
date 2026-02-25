package com.lokahe.androidmvvm.ui.widget

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.automirrored.filled.Logout
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.lokahe.androidmvvm.AppDialog
import com.lokahe.androidmvvm.LocalViewModel
import com.lokahe.androidmvvm.R
import com.lokahe.androidmvvm.UserHeaderOption
import com.lokahe.androidmvvm.data.models.supabase.User
import com.lokahe.androidmvvm.data.models.supabase.avatar
import com.lokahe.androidmvvm.data.models.supabase.name
import com.lokahe.androidmvvm.emailCover
import com.lokahe.androidmvvm.ui.Screen
import com.lokahe.androidmvvm.viewmodels.MainViewModel

@Composable
fun UserHeader(
    user: User? = null,
    option: UserHeaderOption = UserHeaderOption.None,
    onItemSelected: (Screen) -> Unit = {},
    optionBtn: @Composable () -> Unit = {},
) {
    val viewModel = LocalViewModel.current as MainViewModel
    val isLoggedIn by viewModel.isSignedIn.collectAsState()
    Surface(
        modifier = Modifier.fillMaxWidth().clickable(onClick = {
            if (isLoggedIn) onItemSelected(Screen.Account())
            else viewModel.showDialog(AppDialog.SignIn)
        }),
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        Column {
            Row(
                modifier = Modifier.padding(8.dp),
                verticalAlignment = CenterVertically
            ) {
                AvatarIcon(
                    modifier = Modifier.padding(8.dp).size(50.dp).clickable(onClick = {
                        if (isLoggedIn) viewModel.showDialog(AppDialog.Avatar)
                        else viewModel.showDialog(AppDialog.SignIn)
                    }),
                    url = user?.avatar() ?: ""
                )
                Column(modifier = Modifier.padding(vertical = 8.dp).weight(1f)) {
                    Text(
                        text = user?.name() ?: stringResource(R.string.guest),
                        modifier = Modifier.padding(bottom = 4.dp).basicMarquee(),
                        maxLines = 1,
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        modifier = Modifier.basicMarquee(),
                        text = user?.email?.emailCover() ?: stringResource(R.string.signInUp),
                        maxLines = 1,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                when (option) {
                    is UserHeaderOption.Sign ->
                        IconButton(
                            modifier = Modifier.padding(start = 16.dp),
                            onClick = {
                                viewModel.showDialog(if (isLoggedIn) AppDialog.SignOut else AppDialog.SignIn)
                            }) {
                            Icon(
                                imageVector = if (isLoggedIn) Icons.AutoMirrored.Filled.Logout
                                else Icons.AutoMirrored.Filled.Login,
                                contentDescription = stringResource(
                                    if (isLoggedIn) R.string.logout else R.string.login
                                )
                            )
                        }

                    is UserHeaderOption.Edit ->
                        optionBtn()

                    is UserHeaderOption.Send ->
                        optionBtn()

                    else -> {}
                }
            }
            if (option == UserHeaderOption.Edit) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = CenterVertically
                ) {
                    Text(
                        text = stringResource(
                            R.string.following_count,
                            user?.profile?.followingList?.size ?: 0
                        )
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = stringResource(
                            R.string.followers_count,
                            user?.profile?.followers[0]?.count ?: 0
                        )
                    )
                }
            }
        }
    }
}