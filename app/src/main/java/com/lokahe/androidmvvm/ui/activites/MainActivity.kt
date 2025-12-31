package com.lokahe.androidmvvm.ui.activites

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.activity
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.lokahe.androidmvvm.LocalDrawerState
import com.lokahe.androidmvvm.LocalNavController
import com.lokahe.androidmvvm.ProvideLocals
import com.lokahe.androidmvvm.R
import com.lokahe.androidmvvm.SIDE_MENU_ITEMS
import com.lokahe.androidmvvm.Screen
import com.lokahe.androidmvvm.UserHeaderOption
import com.lokahe.androidmvvm.ui.screens.AccountScreen
import com.lokahe.androidmvvm.ui.screens.MainScreen
import com.lokahe.androidmvvm.ui.screens.PersonsScreen
import com.lokahe.androidmvvm.ui.screens.SendPostScreen
import com.lokahe.androidmvvm.ui.theme.AndroidMVVMTheme
import com.lokahe.androidmvvm.ui.widget.MainDialog
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
            val navController = rememberNavController()
            ProvideLocals(
                navController = navController,
                viewModel = viewModel
            ) {
                AndroidMVVMTheme {
                    SideMenu {
                        NavHost(
                            navController = navController,
                            startDestination = Screen.Home.route
                        ) {
                            composable(Screen.Home.route) { MainScreen() }
                            composable(Screen.Account.route) { AccountScreen() }
                            composable(Screen.Persons.route) { PersonsScreen() }
                            composable(Screen.SendPost.route) { SendPostScreen() }
                            activity(Screen.Settings.route) {
                                activityClass = SettingsActivity::class
                            }
                        }
                    }
                    MainDialog()
                }
            }
        }
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
                    navController.navigate(item.route) {
                        // 1. Avoid multiple copies of the same destination when
                        // reselecting the same item
                        launchSingleTop = true

                        // 2. (Recommended) Pop up to the start destination of the graph to
                        // avoid building up a large stack of destinations on the back stack
                        // as you select different menu items
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }

                        // 3. (Recommended) Restore state when reselecting a previously selected item
                        restoreState = true
                    }
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
    ModalDrawerSheet(
        modifier = Modifier.width(300.dp),
    ) {
        UserHeader(option = UserHeaderOption.Sign, onItemSelected = onItemSelected)
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