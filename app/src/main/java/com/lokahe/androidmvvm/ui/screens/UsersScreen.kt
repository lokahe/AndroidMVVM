package com.lokahe.androidmvvm.ui.screens

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lokahe.androidmvvm.R
import com.lokahe.androidmvvm.UserHeaderOption
import com.lokahe.androidmvvm.ui.widget.MainScaffold
import com.lokahe.androidmvvm.ui.widget.SuperLazyColum
import com.lokahe.androidmvvm.ui.widget.UserHeader
import com.lokahe.androidmvvm.viewmodels.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsersScreen(
    ids: List<String>,
    onScroll: (Int) -> Unit = {}
) {
    val viewModel = viewModel<UserViewModel>()
    val me by viewModel.currentUser.collectAsState()
    val users by viewModel.users.collectAsState()
    LaunchedEffect(ids) { viewModel.fetchUsers(ids) }
    val listState = rememberLazyListState()
    MainScaffold(
        title = stringResource(R.string.users)
    ) { contentPadding ->
        SuperLazyColum(
            modifier = Modifier.padding(horizontal = 8.dp).fillMaxWidth(),
            paddingValues = contentPadding,
            items = users,
            onScroll = onScroll,
            onRefresh = { },
            onLoadMore = { },
        ) { index, user ->
            UserHeader(user, UserHeaderOption.Edit) {
                if (me != null && user.id != me!!.id) {
                    val followed =
                        me!!.profile?.followingList?.any { it.targetId == user.id } ?: false
                    Button(modifier = Modifier.padding(8.dp), onClick = {
                        if (followed) viewModel.unFollow(me!!.id, user.id)
                        else viewModel.follow(me!!.id, user.id)
                    }) {
                        Text(text = stringResource(if (followed) R.string.followed else R.string.follow))
                    }
                }
            }
            Spacer(Modifier.height(2.dp))
        }
    }
}
