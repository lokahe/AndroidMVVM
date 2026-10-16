package com.lokahe.androidmvvm.viewmodels

import androidx.lifecycle.viewModelScope
import com.lokahe.androidmvvm.data.local.UserManager
import com.lokahe.androidmvvm.data.repository.PreferencesRepository
import com.lokahe.domain.repository.AuthRepository
import com.lokahe.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel @Inject constructor(
    repository: PreferencesRepository,
    authRepository: AuthRepository,
    userRepository: UserRepository,
    userManager: UserManager
) : BaseViewModel(repository, authRepository, userRepository, userManager) {

    fun updateUseAvatarColor(useAvatarColor: Boolean) {
        viewModelScope.launch {
            repository.updateUseAvatarColor(useAvatarColor)
        }
    }

    fun updateDarkMode(darkMode: Int) {
        viewModelScope.launch {
            repository.updateDarkMode(darkMode)
        }
    }


    fun updateUserAge(age: Int) {
        viewModelScope.launch {
            repository.updateUserAge(age)
        }
    }

    fun updateLoginStatus(isLoggedIn: Boolean) {
        viewModelScope.launch {
            repository.updateLoginStatus(isLoggedIn)
        }
    }

    fun updateGender(gender: String) {
        viewModelScope.launch {
            repository.updateGender(gender)
        }
    }

    fun clearData() {
        viewModelScope.launch {
            repository.clearAllData()
        }
    }
}
