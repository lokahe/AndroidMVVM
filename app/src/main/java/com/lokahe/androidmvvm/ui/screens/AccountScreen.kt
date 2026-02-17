package com.lokahe.androidmvvm.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.lokahe.androidmvvm.ACCOUNT_TABS
import com.lokahe.androidmvvm.LocalViewModel
import com.lokahe.androidmvvm.R
import com.lokahe.androidmvvm.UserHeaderOption
import com.lokahe.androidmvvm.copy
import com.lokahe.androidmvvm.ui.widget.MainScaffold
import com.lokahe.androidmvvm.ui.widget.UserHeader
import com.lokahe.androidmvvm.viewmodels.MainViewModel

@Composable
fun AccountScreen() {
    val viewModel = LocalViewModel.current as MainViewModel
    val user by viewModel.currentUser.collectAsState()
    var isEditing by remember { mutableStateOf(false) }
    // State for editable fields
    val userEdited by remember { mutableStateOf(user) }
    MainScaffold(
        title = stringResource(R.string.account)
    ) { contentPadding ->
        Column(
            modifier = Modifier.padding(contentPadding)
        ) {
            UserHeader(option = UserHeaderOption.Edit) {
                IconButton(
                    modifier = Modifier.padding(start = 16.dp),
                    onClick = {
                        if (isEditing) {
                            viewModel.updateUserProfile(
                                phone = userEdited?.phone ?: "","","","","",
//                                address = userEdited?.address ?: "",
//                                birthDate = userEdited?.birthDate ?: "",
//                                description = userEdited?.description ?: "",
//                                gender = userEdited?.gender ?: ""
                            )
                            isEditing = false
                        } else {
                            isEditing = true
                        }
                    }) {
                    Icon(
                        imageVector = if (isEditing) Icons.Default.Save else Icons.Default.Edit,
                        contentDescription = stringResource(if (isEditing) R.string.save else R.string.edit)
                    )
                }
            }
            TabScreen(
                selectedTabIndexState = viewModel.homeTabIndex,
                tabs = ACCOUNT_TABS,
                onTabSelected = { index -> viewModel.setHomeTabIndex(index) },
            ) { selectedTabIndex ->
                when (selectedTabIndex) {
                    0 -> PostsScreen(contentPadding.copy(top = 0.dp), user?.id ?: "")
                    1 -> ProfileScreen(isEditing, user, contentPadding.copy(top = 0.dp))
                }
            }
        }
    }
}
