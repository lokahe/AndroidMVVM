package com.lokahe.androidmvvm.viewmodels

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lokahe.androidmvvm.AppDialog
import com.lokahe.androidmvvm.data.local.UserManager
import com.lokahe.androidmvvm.data.models.UserPreferences
import com.lokahe.androidmvvm.data.repository.PreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.runBlocking

private val _activeDialogs = mutableStateListOf<AppDialog>()

open class BaseViewModel(
    private val repository: PreferencesRepository,
    private val userManager: UserManager
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

    val colorSeed = userManager.colorSeedFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val activeDialogs: List<AppDialog> get() = _activeDialogs

    // State for the currently visible dialog
    fun showDialog(dialog: AppDialog) {
        _activeDialogs.add(dialog)
    }

    fun dismissDialog(vararg dialogs: AppDialog = emptyArray()) {
        if (dialogs.isEmpty()) _activeDialogs.clear()
        else _activeDialogs.removeAll(dialogs.toSet())
    }
}