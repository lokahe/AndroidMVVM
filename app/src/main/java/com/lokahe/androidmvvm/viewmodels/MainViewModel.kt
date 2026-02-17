package com.lokahe.androidmvvm.viewmodels

//import com.google.gson.Gson
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.lokahe.androidmvvm.AppDialog
import com.lokahe.androidmvvm.PAGE_SIZE
import com.lokahe.androidmvvm.R
import com.lokahe.androidmvvm.clear
import com.lokahe.androidmvvm.data.auth.GoogleAuther
import com.lokahe.androidmvvm.data.local.UserManager
import com.lokahe.androidmvvm.data.models.Person
import com.lokahe.androidmvvm.data.models.Post
import com.lokahe.androidmvvm.data.models.auth.GoogleAuth
import com.lokahe.androidmvvm.data.models.network.User
import com.lokahe.androidmvvm.data.repository.DataBaseRepository
import com.lokahe.androidmvvm.data.repository.HttpRepository
import com.lokahe.androidmvvm.data.repository.PreferencesRepository
import com.lokahe.androidmvvm.set
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
import kotlinx.coroutines.launch

@HiltViewModel
class MainViewModel @Inject constructor(
    private val prefRepository: PreferencesRepository,
    private val dbRepository: DataBaseRepository,
    private val httpRepository: HttpRepository,
    private val userManager: UserManager,
    private val googleAuther: GoogleAuther
) : BaseViewModel(prefRepository, userManager) {

    init {
        // 2. Trigger the check immediately when ViewModel is created (App Start)
        checkAutoLogin()
    }

    private val _processing = mutableStateOf(false)
    val processing: State<Boolean> = _processing
    private fun onProcessing() {
        _processing.value = true
        showDialog(AppDialog.Loading)
    }

    private fun unProcessing() {
        _processing.value = false
        dismissDialog(AppDialog.Loading)
    }

    private fun checkAutoLogin() {
        viewModelScope.launch {
            // Get the saved token
            // Note: We use .firstOrNull() to get the current value from the Flow once
            val token = userManager.userTokenFlow.firstOrNull()
            if (!token.isNullOrEmpty()) {
                onProcessing()
                // Verify with server
                httpRepository.varifyToken(token).onSuccess {
                    save(token, it)
                }
                unProcessing()
            }
        }
    }

    // Expose the user as State for Compose
    val currentUser = userManager.userFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Check if logged in based on if user data exists
    val isSignedIn = currentUser.map { it != null }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun logout() {
        viewModelScope.launch {
            val token = userManager.userTokenFlow.firstOrNull()
            token?.ifEmpty { null }?.let { httpRepository.signOut(it) }
            userManager.clearUser()
        }
    }

    fun loginWithTwitter(
        context: Context,
    ) {
        viewModelScope.launch {
            val result = httpRepository.xOauth()
            result.onSuccess { oauth ->
                Log.d("loginWithTwitter", "oauth: $oauth")
            }.onFailure { error ->
                toast(error.message ?: R.string.unkown_error.toString())
            }
        }

//        val customTabsIntent = CustomTabsIntent.Builder().build()
//        customTabsIntent.launchUrl(context, Uri.parse(Api.TWITTER_AUTH_URL))

//        httpRepository.loginWithTwitter()
    }

    fun signWithGoogle(context: Context) {
        viewModelScope.launch {
            googleAuther.gOauth(context) { idToken ->
                httpRepository.gAuth(body = GoogleAuth(idToken = idToken, nonce = null)).onSuccess {
                    save(it.accessToken)
                }.onFailure {
                    toast(it.message ?: R.string.unkown_error.toString())
                }
            }
        }
    }

    private fun save(
        token: String?,
        user: com.lokahe.androidmvvm.data.models.supabase.User? = null
    ) =
        viewModelScope.launch {
            userManager.saveToken(token)
            user ?: token?.let { tk ->
                httpRepository.varifyToken(tk).onSuccess { userManager.saveUser(it) }
            }
        }

    fun sign(email: String) {
        viewModelScope.launch {
            val result = httpRepository.sign(email)
            result.onSuccess { message ->
                toast(message)
                _verifyEmail.value = email
            }.onFailure { error ->
                toast(error.message ?: R.string.unkown_error.toString())
            }
        }
    }

    fun verifyEmail(email: String, token: String) {
        viewModelScope.launch {
            val result = httpRepository.verifyEmail(email, token)
            result.onSuccess { it ->
                save(it.accessToken)
            }.onFailure { error ->
                toast(error.message ?: R.string.unkown_error.toString())
            }
        }
    }

    fun updateAvatar(url: String) {
        viewModelScope.launch {
//            val objectId = userManager.userFlow.firstOrNull()?.objectId ?: ""
//            val token = userManager.userTokenFlow.firstOrNull()
//            if (objectId.isEmpty() || token.isNullOrEmpty()) return@launch
//            val result = httpRepository.updateProperty(
//                objectId = objectId,
//                token = token,
//                request = UpdateAvatarRequest(url)
//            )
//            result.onSuccess { message ->
//                toast(message)
//            }.onFailure { error ->
//                toast(error.message ?: R.string.unkown_error.toString())
//            }
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
            val userId = userManager.userFlow.firstOrNull()?.id ?: ""
            val token = userManager.userTokenFlow.firstOrNull()
            if (userId.isEmpty() || token.isNullOrEmpty()) return@launch

            val updateMap = mapOf(
                "phone" to phone,
                "address" to address,
                "birthDate" to birthDate,
                "description" to description,
                "gender" to gender
            )

//            val result = httpRepository.updateProperty(
//                objectId = objectId,
//                token = token,
//                request = updateMap
//            )
//            result.onSuccess { message ->
//                toast(message)
//                userManager.saveUser(
//                    currentUser.value!!.copy(
//                        phone = phone,
//                        address = address,
//                        birthDate = birthDate,
//                        description = description,
//                        gender = gender
//                    )
//                )
//            }.onFailure { error ->
//                toast(error.message ?: R.string.unkown_error.toString())
//            }
        }
    }

    // State for the check is signed up
    private val _verifyEmail = mutableStateOf("")
    val verifyEmail: State<String> = _verifyEmail
    fun resetVerifyEmail() {
        _verifyEmail.value = ""
    }

    // State for the currently visible dialog
    private val _activeDialog = mutableIntStateOf(AppDialog.None.index)
    val activeDialog: State<Int> = _activeDialog
    fun showDialog(dialog: AppDialog) {
        _activeDialog.intValue = _activeDialog.intValue.set(dialog.index)
    }

    fun dismissDialog(dialog: AppDialog? = null) {
        _activeDialog.intValue =
            dialog?.let { _activeDialog.intValue.clear(dialog.index) } ?: AppDialog.None.index
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
//            val result = httpRepository.getUsers(pageSize, offset)
//            result.onSuccess { users ->
//                _users.update { currentList ->
//                    if (offset == 0) users
//                    else currentList + users
//                }
//            }
//            result.onFailure { error ->
//                toast(error.message ?: R.string.unkown_error.toString())
//            }
        }
    }

    // State for the list of posts
    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    private val _myPosts = MutableStateFlow<List<Post>>(emptyList())
    val posts = _posts.asStateFlow()
    val myPosts = _myPosts.asStateFlow()

    init {
        fetchPosts(PAGE_SIZE, 0)
    }

    fun fetchPosts(pageSize: Int, offset: Int, ownerId: String = "") {
        viewModelScope.launch {
//            val whereClause = if (ownerId.isNotEmpty()) "ownerId='$ownerId'" else ""
//            val result = httpRepository.getPosts(pageSize, offset, whereClause)
//            result.onSuccess { posts ->
//                if (ownerId.isNotEmpty())
//                    _myPosts.update { currentList -> if (offset == 0) posts else currentList + posts }
//                else
//                    _posts.update { currentList -> if (offset == 0) posts else currentList + posts }
//                for (post in posts)
//                    dbRepository.insertPost(post)
//            }
//            result.onFailure { error ->
//                dbRepository.getAllPosts(pageSize, offset, if (ownerId.isEmpty()) null else ownerId)
//                    .let { posts ->
//                        if (ownerId.isNotEmpty())
//                            _myPosts.update { currentList -> if (offset == 0) posts else currentList + posts }
//                        else
//                            _posts.update { currentList -> if (offset == 0) posts else currentList + posts }
//                    }
//            }
        }
    }

    fun sendPost(content: String, images: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
//            val token = userManager.userTokenFlow.firstOrNull()
//            val user = userManager.userFlow.firstOrNull()
//            val date = System.currentTimeMillis()
//            if (!token.isNullOrEmpty() && user != null) {
//                val post = Post(
//                    objectId = "",
//                    content = content,
//                    images = images,
//                    parentId = "",
//                    ownerId = user.objectId,
//                    author = user.name,
//                    avatar = user.avatar ?: "",
//                    created = date,
//                    updated = date,
//                    message = null,
//                    code = null
//                )
//                val result = httpRepository.sendPost(token, post)
//                result.onSuccess { message ->
//                    toast(message)
//                    onSuccess()
//                }.onFailure { error ->
//                    toast(error.message ?: R.string.unkown_error.toString())
//                }
//            }
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

    fun handleMagicLink(uri: Uri) {
        // 1. Get the part after the '#'
        uri.fragment?.let { fragment ->
            // 2. Convert "key=value&key2=value2" into a Map
            val params = fragment.split("&").associate {
                val (key, value) = it.split("=")
                key to value
            }
            val accessToken = params["access_token"]
            val refreshToken = params["refresh_token"]
            accessToken?.ifEmpty { null }?.let {
                viewModelScope.launch { save(it) }
            } ?: params["error_description"]?.ifEmpty { null }?.let { toast(it) }
        }
    }
}
