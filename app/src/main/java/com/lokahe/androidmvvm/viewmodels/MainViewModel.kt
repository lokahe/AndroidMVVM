package com.lokahe.androidmvvm.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.lokahe.androidmvvm.AppDialog
import com.lokahe.androidmvvm.PAGE_SIZE
import com.lokahe.androidmvvm.R
import com.lokahe.androidmvvm.models.Person
import com.lokahe.androidmvvm.models.network.Post
import com.lokahe.androidmvvm.models.network.UpdateAvatarRequest
import com.lokahe.androidmvvm.models.network.User
import com.lokahe.androidmvvm.network.UserManager
import com.lokahe.androidmvvm.repository.DataBaseRepository
import com.lokahe.androidmvvm.repository.HttpRepository
import com.lokahe.androidmvvm.repository.PreferencesRepository
import com.lokahe.androidmvvm.toast
import com.lokahe.androidmvvm.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class MainViewModel @Inject constructor(
    private val prefRepository: PreferencesRepository,
    private val dbRepository: DataBaseRepository,
    private val httpRepository: HttpRepository,
    private val userManager: UserManager
) : BaseViewModel(prefRepository, userManager) {

    init {
        // 2. Trigger the check immediately when ViewModel is created (App Start)
        checkAutoLogin()
    }

    private fun checkAutoLogin() {
        viewModelScope.launch {
            // Get the saved token
            // Note: We use .firstOrNull() to get the current value from the Flow once
            val token = userManager.userTokenFlow.firstOrNull()
            if (!token.isNullOrEmpty()) {
                showDialog(AppDialog.Loading)
                // Verify with server
                if (httpRepository.verifyToken(token)) {
                    // Token is good, user stays logged in (state is likely already observing userManager)
                } else {
                    // Token expired or invalid -> Logout locally
                    userManager.clearUser()
                }
            }
            showDialog(AppDialog.None)
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
                toast(message)
                dismissDialog()
            }.onFailure { error ->
                toast(error.message ?: R.string.unkown_error.toString())
            }
        }
    }

    fun updateAvatar(url: String) {
        viewModelScope.launch {
            val objectId = userManager.userFlow.firstOrNull()?.objectId ?: ""
            val token = userManager.userTokenFlow.firstOrNull()
            if (objectId.isEmpty() || token.isNullOrEmpty()) return@launch
            val result = httpRepository.updateProperty(
                objectId = objectId,
                token = token,
                request = UpdateAvatarRequest(url)
            )
            result.onSuccess { message ->
                toast(message)
            }.onFailure { error ->
                toast(error.message ?: R.string.unkown_error.toString())
            }
        }
    }

    fun updateUserProfile(
        phone: String,
        address: String,
        birthDate: String,
        description: String,
        gender: String
    ) {
        viewModelScope.launch {
            val objectId = userManager.userFlow.firstOrNull()?.objectId ?: ""
            val token = userManager.userTokenFlow.firstOrNull()
            if (objectId.isEmpty() || token.isNullOrEmpty()) return@launch

            val updateMap = mapOf(
                "phone" to phone,
                "address" to address,
                "birthDate" to birthDate,
                "description" to description,
                "gender" to gender
            )

            val result = httpRepository.updateProperty(
                objectId = objectId,
                token = token,
                request = updateMap
            )
            result.onSuccess { message ->
                toast(message)
                userManager.saveUser(
                    currentUser.value!!.copy(
                        phone = phone,
                        address = address,
                        birthDate = birthDate,
                        description = description,
                        gender = gender
                    )
                )
            }.onFailure { error ->
                toast(error.message ?: R.string.unkown_error.toString())
            }
        }
    }

    // State for the check is signed up
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

    // state for home tab index state
    private var _homeTabIndex = mutableIntStateOf(0)
    val homeTabIndex: State<Int> = _homeTabIndex
    fun setHomeTabIndex(index: Int) {
        _homeTabIndex.intValue = index
    }

    // State for the list of users
    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users = _users.asStateFlow()

    init {
        fetchUsers(PAGE_SIZE, 0)
    }

    fun fetchUsers(pageSize: Int, offset: Int) {
        viewModelScope.launch {
            val result = httpRepository.getUsers(pageSize, offset)
            result.onSuccess { users ->
                _users.update { currentList ->
                    if (offset == 0) users
                    else currentList + users
                }
            }
            result.onFailure { error ->
                toast(error.message ?: R.string.unkown_error.toString())
            }
        }
    }

    // State for the list of posts
    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts = _posts.asStateFlow()

    init {
        fetchPosts(PAGE_SIZE, 0)
    }

    fun fetchPosts(pageSize: Int, offset: Int) {
        viewModelScope.launch {
            val result = httpRepository.getPosts(pageSize, offset)
            result.onSuccess { posts ->
                _posts.update { currentList ->
                    if (offset == 0) posts
                    else currentList + posts
                }
            }
            result.onFailure { error ->
                toast(error.message ?: R.string.unkown_error.toString())
            }
        }
    }

    fun sendPost(content: String, images: String) {
        viewModelScope.launch {
            val token = userManager.userTokenFlow.firstOrNull()
            val user = userManager.userFlow.firstOrNull()
            val date = System.currentTimeMillis()
            if (!token.isNullOrEmpty() && user != null) {
                val post = Post(
                    content = content,
                    images = images,
                    parentId = "",
                    ownerId = user.objectId,
                    author = user.name,
                    authorGender = user.gender ?: "",
                    avatar = user.avatar ?: "",
                    created = date,
                    updated = date,
                    message = null,
                    code = null
                )
                val result = httpRepository.sendPost(token, post)
                result.onSuccess { message ->
                    toast(message)
                }.onFailure { error ->
                    toast(error.message ?: R.string.unkown_error.toString())
                }
            }
        }
    }

    // State for the list of persons (Database)
    private val _persons = mutableStateOf<List<Person>>(emptyList())
    val persons: State<List<Person>> = _persons

    init {
        fetchPersons()
    }

    private fun fetchPersons() {
        viewModelScope.launch {
            _persons.value = dbRepository.getAllPersons()
        }
    }

    fun addPerson(person: Person) {
        viewModelScope.launch {
            dbRepository.insertPerson(person)
            fetchPersons()
        }
    }

    fun addRandomPerson() {
        addPerson(Utils.randomPerson())
    }
}
