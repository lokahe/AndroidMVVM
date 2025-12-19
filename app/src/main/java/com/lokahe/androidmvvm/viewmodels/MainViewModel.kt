package com.lokahe.androidmvvm.viewmodels

import androidx.lifecycle.ViewModel
import com.lokahe.androidmvvm.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

}