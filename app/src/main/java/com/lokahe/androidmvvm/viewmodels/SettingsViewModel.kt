package com.lokahe.androidmvvm.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lokahe.androidmvvm.models.UserPreferences
import com.lokahe.androidmvvm.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: UserPreferencesRepository
) : ViewModel() {

    // Expose preferences as StateFlow for UI observation
    val userPreferences: StateFlow<UserPreferences> = repository.observeUserPreferences()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = runBlocking {
                repository.observeUserPreferences().first()
            }
        )

    fun updateUserName(name: String) {
        viewModelScope.launch {
            repository.updateUserName(name)
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
