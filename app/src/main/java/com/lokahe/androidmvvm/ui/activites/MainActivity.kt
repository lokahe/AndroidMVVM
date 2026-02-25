package com.lokahe.androidmvvm.ui.activites

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.lokahe.androidmvvm.LocalDrawerState
import com.lokahe.androidmvvm.LocalNavController
import com.lokahe.androidmvvm.ProvideLocals
import com.lokahe.androidmvvm.R
import com.lokahe.androidmvvm.SIDE_MENU_ITEMS
import com.lokahe.androidmvvm.UserHeaderOption
import com.lokahe.androidmvvm.ui.MainDialog
import com.lokahe.androidmvvm.ui.Screen
import com.lokahe.androidmvvm.ui.screens.AccountScreen
import com.lokahe.androidmvvm.ui.screens.MainScreen
import com.lokahe.androidmvvm.ui.screens.PersonsScreen
import com.lokahe.androidmvvm.ui.screens.SendPostScreen
import com.lokahe.androidmvvm.ui.theme.AndroidMVVMTheme
import com.lokahe.androidmvvm.ui.widget.UserHeader
import com.lokahe.androidmvvm.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val backStack = rememberNavBackStack(Screen.Home)
            ProvideLocals(
                navController = backStack,
                viewModel = viewModel
            ) {
                AndroidMVVMTheme {
                    SideMenu {
                        NavDisplay(
                            backStack = backStack,
                            onBack = { if (backStack.size > 1) backStack.removeLast() },
                            entryProvider = entryProvider {
                                entry<Screen.Home> { MainScreen() }
                                entry<Screen.Account> { AccountScreen(id = it.id) }
                                entry<Screen.Persons> { PersonsScreen() }
                                entry<Screen.SendPost> { SendPostScreen() }
                                entry<Screen.Settings> {
                                    val context = LocalContext.current
                                    LaunchedEffect(Unit) {
                                        context.startActivity(
                                            Intent(context, SettingsActivity::class.java)
                                        )
                                    }
                                }
                            })
                    }
                    MainDialog()
                }
            }
        }
        android.util.Log.d("MainActivity", "onCreate: ${intent.data}")
        intent.data?.let { viewModel.handleMagicLink(it) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SideMenu(content: @Composable () -> Unit = {}) {
    val drawerState = LocalDrawerState.current
    val navController = LocalNavController.current
    val scope = rememberCoroutineScope()
    var selectedMenuItem: Screen by remember { mutableStateOf(Screen.Home) }
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                selectedItem = selectedMenuItem,
                onItemSelected = { item ->
                    selectedMenuItem = item
                    scope.launch { drawerState.close() }
                    navController.add(item)
                }
            )
        }
    ) {
        content()
    }
}

@Composable
fun DrawerContent(
    selectedItem: Screen,
    onItemSelected: (Screen) -> Unit
) {
    val viewModel = viewModel<MainViewModel>()
    val user by viewModel.currentUser.collectAsState()
    ModalDrawerSheet(
        modifier = Modifier.width(300.dp),
    ) {
        UserHeader(user, UserHeaderOption.Sign, onItemSelected)
        Spacer(modifier = Modifier.height(8.dp))
        SIDE_MENU_ITEMS.forEach { (screen, icon, label) ->
            NavigationDrawerItem(
                icon = { Icon(icon, contentDescription = label) },
                label = { Text(label) },
                selected = selectedItem == screen,
                onClick = { onItemSelected(screen) },
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = CenterVertically
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.large)
                    .clickable(onClick = { onItemSelected(Screen.Settings) })
                    .padding(16.dp),
                imageVector = Icons.Filled.Settings,
                contentDescription = stringResource(R.string.settings)
            )
        }
    }
}