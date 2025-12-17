//package com.lokahe.androidmvvm
//
//import android.content.Intent
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.material3.Button
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalContext
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import androidx.navigation.compose.rememberNavController
//import com.lokahe.androidmvvm.ui.activites.SettingsActivity
//
//@Composable
//fun AppNavigation() {
//    val navController = rememberNavController()
//
//    NavHost(navController = navController, startDestination = Screen.Home) {
//        composable(Screen.Home.route) {
//            HomeScreen(
//                onNavigateToSettings = {
//                    // Navigate to SettingsActivity instead of a composable screen
//                    // navController.navigate("settings")
//
//                }
//            )
//        }
//        composable(Screen.Account.route) {
//            SettingsScreen(
//                onNavigateBack = { navController.popBackStack() }
//            )
//        }
//    }
//}
//
//@Composable
//fun HomeScreen(onNavigateToSettings: () -> Unit) {
//
//}
//
//@Composable
//fun SettingsScreen(onNavigateBack: () -> Unit) {
//    // TODO: Implement Settings Screen UI
//    val context = LocalContext.current
//    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//        Button(onClick = {
//            context.startActivity(Intent(context, SettingsActivity::class.java))
//        }) {
//            Text(text = "Go to Settings")
//        }
//    }
//}
