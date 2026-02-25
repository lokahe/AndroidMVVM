package com.lokahe.androidmvvm.ui.activites

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.navigation3.runtime.rememberNavBackStack
import com.lokahe.androidmvvm.LocalViewModel
import com.lokahe.androidmvvm.ProvideLocals
import com.lokahe.androidmvvm.R
import com.lokahe.androidmvvm.ui.Screen
import com.lokahe.androidmvvm.ui.preference.ListPreference
import com.lokahe.androidmvvm.ui.preference.SliderDialogPreference
import com.lokahe.androidmvvm.ui.preference.SwitchPreference
import com.lokahe.androidmvvm.ui.theme.AndroidMVVMTheme
import com.lokahe.androidmvvm.ui.widget.SettingScaffold
import com.lokahe.androidmvvm.ui.widget.settingsCard
import com.lokahe.androidmvvm.ui.widget.settingsDivider
import com.lokahe.androidmvvm.viewmodels.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsActivity : AppCompatActivity() {
    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ProvideLocals(
                navController = rememberNavBackStack(Screen.Home),
                viewModel = viewModel
            ) {
                AndroidMVVMTheme {
                    SettingsScreen()
                }
            }
        }
    }
}

@Composable
fun SettingsScreen(viewModel: SettingsViewModel = LocalViewModel.current as SettingsViewModel) {
    val preferences by viewModel.userPreferences.collectAsState()

    SettingScaffold(
        titleRes = R.string.settings
    ) {
        settingsCard(
            titleRes = R.string.general
        ) {
            SwitchPreference(
                title = stringResource(R.string.use_avatar_color),
                summary = stringResource(R.string.summary_use_avatar_color),
                value = preferences.useAvatarColor,
            ) { viewModel.updateUseAvatarColor(it) }
            settingsDivider()
            ListPreference(
                title = stringResource(R.string.dark_mode),
                value = preferences.darkMode,
                names = R.array.dark_mode,
                values = R.array.dark_mode_values,
                icons = R.array.dark_mode_icons,
            ) { viewModel.updateDarkMode(it as Int) }
            settingsDivider()
            SliderDialogPreference(
                title = stringResource(R.string.option_slider),
                value = preferences.userAge,
                defaultValue = 0,
                valueRange = 0f..100f
            ) { viewModel.updateUserAge(it as Int) }
        }
    }
}