package com.lokahe.androidmvvm

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.lokahe.androidmvvm.models.UserPreferences
import com.lokahe.androidmvvm.ui.preference.PreferenceTheme
import com.lokahe.androidmvvm.ui.preference.preferenceTheme

val LocalPreferenceTheme =
    compositionLocalOf<PreferenceTheme> { noLocalProvidedFor("LocalPreferenceTheme") }

val LocalViewModel = compositionLocalOf<ViewModel> { noLocalProvidedFor("LocalViewModel") }

val LocalPreference = compositionLocalOf<UserPreferences> { noLocalProvidedFor("LocalPreference") }

val LocalNavController =
    compositionLocalOf<NavController> { noLocalProvidedFor("LocalNavController") }

//@Composable
//fun ProvidePreferenceTheme(
//    theme: PreferenceTheme = preferenceTheme(),
//    content: @Composable () -> Unit,
//) {
//    CompositionLocalProvider(LocalPreferenceTheme provides theme, content = content)
//}

@Composable
fun ProvidePreferenceLocals(
    navController: NavController,
    viewModel: ViewModel,
    theme: PreferenceTheme = preferenceTheme(),
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalNavController provides navController,
        LocalPreferenceTheme provides theme,
        LocalViewModel provides viewModel,
        content = content,
    )
}

internal fun noLocalProvidedFor(name: String): Nothing {
    error("CompositionLocal $name not present")
}
