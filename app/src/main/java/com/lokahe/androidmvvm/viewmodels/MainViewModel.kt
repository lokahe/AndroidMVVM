package com.lokahe.androidmvvm.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lokahe.androidmvvm.models.Person
import com.lokahe.androidmvvm.repository.DataBaseRepository
import com.lokahe.androidmvvm.repository.PreferencesRepository
import com.lokahe.androidmvvm.utils.RandomUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch

sealed class AppDialog {
    data object None : AppDialog()
    data object Logout : AppDialog()
    data object Login : AppDialog()
    // Add more later easily: data object DeleteAccount : AppDialog()
}

@HiltViewModel
class MainViewModel @Inject constructor(
    private val preferencesRepository: PreferencesRepository,
    private val dataBaseRepository: DataBaseRepository
) : ViewModel() {

    // State for the log
    private val _isLoggedIn = mutableStateOf(false)
    val isLoggedIn: State<Boolean> = _isLoggedIn

    fun logout() {
        _isLoggedIn.value = false
    }

    fun login(email: String, password: String) {

    }

    fun isSignedUp(email: String): Boolean {
        return false
    }

    // State for the currently visible dialog
    private val _activeDialog = mutableStateOf<AppDialog>(AppDialog.None)
    val activeDialog: State<AppDialog> = _activeDialog

    fun showDialog(dialog: AppDialog) {
        _activeDialog.value = dialog
    }

    fun dismissDialog() {
        _activeDialog.value = AppDialog.None
    }

    // State for the list of persons
    private val _persons = mutableStateOf<List<Person>>(emptyList())
    val persons: State<List<Person>> = _persons

    init {
        fetchPersons()
    }

    private fun fetchPersons() {
        viewModelScope.launch {
            _persons.value = dataBaseRepository.getAllPersons()
        }
    }

    fun addPerson(person: Person) {
        viewModelScope.launch {
            dataBaseRepository.insertPerson(person)
            fetchPersons()
        }
    }

    fun addRandomPerson() {
        addPerson(RandomUtils.randomPerson())
    }
}
