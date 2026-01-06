package com.lokahe.androidmvvm.viewmodels

import androidx.lifecycle.viewModelScope
import com.lokahe.androidmvvm.data.local.UserManager
import com.lokahe.androidmvvm.data.repository.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val prefRepository: PreferencesRepository,
    private val userManager: UserManager
) : BaseViewModel(prefRepository, userManager) {

    fun updateUseAvatarColor(useAvatarColor: Boolean) {
        viewModelScope.launch {
            prefRepository.updateUseAvatarColor(useAvatarColor)
        }
    }

    fun updateDarkMode(darkMode: Int) {
        viewModelScope.launch {
            prefRepository.updateDarkMode(darkMode)
        }
    }


    fun updateUserAge(age: Int) {
        viewModelScope.launch {
            prefRepository.updateUserAge(age)
        }
    }

    fun updateLoginStatus(isLoggedIn: Boolean) {
        viewModelScope.launch {
            prefRepository.updateLoginStatus(isLoggedIn)
        }
    }

    fun updateGender(gender: String) {
        viewModelScope.launch {
            prefRepository.updateGender(gender)
        }
    }

    fun clearData() {
        viewModelScope.launch {
            prefRepository.clearAllData()
        }
    }
}
