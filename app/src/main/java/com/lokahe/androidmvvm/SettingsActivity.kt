package com.lokahe.androidmvvm

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.rememberNavController
import com.lokahe.androidmvvm.repository.UserPreferencesRepository
import com.lokahe.androidmvvm.repository.dataStore
import com.lokahe.androidmvvm.ui.preference.ListPreference
import com.lokahe.androidmvvm.ui.preference.SliderDialogPreference
import com.lokahe.androidmvvm.ui.preference.SwitchPreference
import com.lokahe.androidmvvm.ui.widget.SettingScaffold
import com.lokahe.androidmvvm.ui.widget.settingsCard
import com.lokahe.androidmvvm.ui.widget.settingsDivider
import com.lokahe.androidmvvm.viewmodels.SettingsViewModel
import com.lokahe.androidmvvm.viewmodels.SettingsViewModelFactory

class SettingsActivity : AppCompatActivity() {
    private val viewModel: SettingsViewModel by viewModels {
        SettingsViewModelFactory(UserPreferencesRepository(dataStore))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ProvidePreferenceLocals(rememberNavController(), viewModel) {
                SettingsScreen()
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
                title = stringResource(R.string.option_switch),
                value = preferences.isLoggedIn,
            ) { viewModel.updateLoginStatus(it) }
            settingsDivider()
            ListPreference(
                title = stringResource(R.string.option_list),
                value = preferences.userGender,
                names = R.array.genders,
                values = R.array.genders
            ) { viewModel.updateGender(it as String) }
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