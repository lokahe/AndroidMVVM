package com.lokahe.androidmvvm.viewmodels

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lokahe.androidmvvm.R
import com.lokahe.androidmvvm.models.Person
import com.lokahe.androidmvvm.network.UserManager
import com.lokahe.androidmvvm.repository.DataBaseRepository
import com.lokahe.androidmvvm.repository.HttpRepository
import com.lokahe.androidmvvm.repository.PreferencesRepository
import com.lokahe.androidmvvm.toast
import com.lokahe.androidmvvm.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
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
    private val dataBaseRepository: DataBaseRepository,
    private val httpRepository: HttpRepository,
    private val userManager: UserManager
) : ViewModel() {

    init {
        // 2. Trigger the check immediately when ViewModel is created (App Start)
        checkAutoLogin()
    }

    private fun checkAutoLogin() {
        viewModelScope.launch {
//            _isLoading.value = true
            // Get the saved token
            // Note: We use .firstOrNull() to get the current value from the Flow once
            val token = userManager.userTokenFlow.firstOrNull()

            if (!token.isNullOrEmpty()) {
                // Verify with server
                val isValid = httpRepository.verifyToken(token)

                if (isValid) {
                    // Token is good, user stays logged in (state is likely already observing userManager)
                    Log.d("AutoLogin", "Token verified successfully")
                } else {
                    // Token expired or invalid -> Logout locally
                    Log.d("AutoLogin", "Token invalid, clearing user")
                    userManager.clearUser()
                }
            }
//            _isLoading.value = false
        }
    }

    // Expose the user as State for Compose
    val currentUser = userManager.userFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Check if logged in based on if user data exists
    val isLoggedIn = currentUser.map { it != null }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun logout() {
        viewModelScope.launch {
            val token = userManager.userTokenFlow.firstOrNull()
            if (!token.isNullOrEmpty()) {
                httpRepository.logout(token)
            }
            userManager.clearUser()
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            val result = httpRepository.login(email, password)
            result.onSuccess { message ->
                // Handle success (e.g., show toast, navigate to login)
                toast(message)
                dismissDialog()
            }.onFailure { error ->
                toast(error.message ?: R.string.unkown_error.toString())
            }
        }
    }

    fun signUp(email: String, password: String, name: String) {
        viewModelScope.launch {
            val result = httpRepository.register(email, password, name)
            result.onSuccess { message ->
                // Handle success (e.g., show toast, navigate to login)
                toast(message)
                dismissDialog()
                Log.d("Register", "Success: $message")
            }.onFailure { error ->
                // Handle error (e.g., show error dialog)
                toast(error.message ?: R.string.unkown_error.toString())
                Log.e("Register", "Error: ${error.message}")
            }
        }
    }

    // State for the signed up
    private val _isNewAccount = mutableStateOf(false)
    val isNewAccount: State<Boolean> = _isNewAccount
    fun isNewAccount(email: String) {
        viewModelScope.launch {
            _isNewAccount.value = !httpRepository.isUserRegistered(email)
        }
    }

    fun resetNewAccountCheck() {
        _isNewAccount.value = false
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
        addPerson(Utils.randomPerson())
    }
}
