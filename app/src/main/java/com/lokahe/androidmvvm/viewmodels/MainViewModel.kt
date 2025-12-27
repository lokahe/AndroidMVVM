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

@HiltViewModel
class MainViewModel @Inject constructor(
    private val preferencesRepository: PreferencesRepository,
    private val dataBaseRepository: DataBaseRepository
) : ViewModel() {

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
