package com.lokahe.androidmvvm.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lokahe.androidmvvm.ACCOUNT_TABS
import com.lokahe.androidmvvm.R
import com.lokahe.androidmvvm.UserHeaderOption
import com.lokahe.androidmvvm.copy
import com.lokahe.androidmvvm.ui.widget.MainScaffold
import com.lokahe.androidmvvm.ui.widget.UserHeader
import com.lokahe.androidmvvm.viewmodels.UserViewModel

@Composable
fun AccountScreen(id: String? = null) {
    val viewModel = viewModel<UserViewModel>()
    val me by viewModel.currentUser.collectAsState()
    val isMe = id.isNullOrEmpty() || id == me?.id
    val user by if (isMe) viewModel.currentUser.collectAsState() else viewModel.user.collectAsState()
    LaunchedEffect(id) { if (!id.isNullOrEmpty()) viewModel.fetchUser(id) }
    var curTab by remember { mutableIntStateOf(0) }
    var isEditing by remember { mutableStateOf(false) }
    // State for editable fields
    val userEdited by remember { mutableStateOf(user) }
    MainScaffold(
        title = stringResource(R.string.account)
    ) { contentPadding ->
        Column(
            modifier = Modifier.padding(contentPadding)
        ) {
            UserHeader(user, UserHeaderOption.Edit) {
                if (!isMe && me != null) {
                    val followed = me!!.profile?.followingList?.any { it.targetId == id } ?: false
                    Button(modifier = Modifier.padding(8.dp), onClick = {
                        if (followed) viewModel.unFollow(me!!.id, id)
                        else viewModel.follow(me!!.id, id)
                    }) {
                        Text(text = stringResource(if (followed) R.string.followed else R.string.follow))
                    }
                } else {
                    Icon(
                        modifier = Modifier.padding(8.dp).clickable { viewModel.refreshMe() },
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = stringResource(R.string.refresh)
                    )
                }
            }
//                IconButton(
//                    modifier = Modifier.padding(start = 16.dp),
//                    onClick = {
//                        if (isEditing) {
//                            viewModel.updateUserProfile(
//                                phone = userEdited?.phone ?: "",
//                                address = userEdited?.profile?.address ?: "",
//                                birthDate = userEdited?.profile?.birth ?: "",
//                                description = userEdited?.profile?.description ?: "",
//                                gender = userEdited?.profile?.gender ?: ""
//                            )
//                            isEditing = false
//                        } else {
//                            isEditing = true
//                        }
//                    }) {
//                    Icon(
//                        imageVector = if (isEditing) Icons.Default.Save else Icons.Default.Edit,
//                        contentDescription = stringResource(if (isEditing) R.string.save else R.string.edit)
//                    )
//                }
            TabScreen(
                curTabIndex = curTab,
                tabs = ACCOUNT_TABS,
                onTabSelected = { curTab = it },
            ) { selectedTabIndex ->
                when (selectedTabIndex) {
                    0 -> PostsScreen(contentPadding.copy(top = 0.dp), user?.id ?: "")
                    1 -> ProfileScreen(isEditing, user, contentPadding.copy(top = 0.dp))
                }
            }
        }
    }
}
