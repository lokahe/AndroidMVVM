package com.lokahe.androidmvvm.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.lokahe.androidmvvm.LocalViewModel
import com.lokahe.androidmvvm.R
import com.lokahe.androidmvvm.size
import com.lokahe.androidmvvm.ui.activites.MainScaffold
import com.lokahe.androidmvvm.ui.widget.personItem
import com.lokahe.androidmvvm.ui.widget.text
import com.lokahe.androidmvvm.viewmodels.MainViewModel

@Composable
fun PersonsScreen() {
    val viewModel: MainViewModel = LocalViewModel.current as MainViewModel
    MainScaffold() { contentPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = contentPadding
        ) {
            val persons by viewModel.persons
            item {
                Text(
                    text = stringResource(R.string.persons),
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
            repeat(persons.size()) { index ->
                personItem(index, persons[index])
            }
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .clickable(onClick = { viewModel.addRandomPerson() })
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Icon(
                            modifier = Modifier.padding(16.dp),
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                        )
                        Text(text = stringResource(R.string.addRandom))
                    }
                }
            }
        }
    }
}