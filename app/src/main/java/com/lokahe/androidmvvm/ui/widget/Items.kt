package com.lokahe.androidmvvm.ui.widget

import android.text.TextUtils
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.lokahe.androidmvvm.R
import com.lokahe.androidmvvm.models.Person
import com.lokahe.androidmvvm.models.Post
import com.lokahe.androidmvvm.s

fun LazyListScope.postItem(
    index: Int,
    post: Post? = null
) {
    item {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = "Profile",
                        modifier = Modifier.padding(
                            end = 16.dp
                        )
                    )
                    Text(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        text = "Post ${index + 1}",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "Star",
                        modifier = Modifier.padding(
                            end = 16.dp
                        )
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "This is sample content for post ${index + 1}. In a real app, this would show dynamic content.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

fun LazyListScope.personItem(
    index: Int,
    person: Person
) {
    item {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row {
                    if (TextUtils.isEmpty(person.image)) {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = "Profile",
                            modifier = Modifier.padding(
                                end = 16.dp
                            )
                        )
                    } else {
                        AsyncImage(
                            model = person.image,
                            contentDescription = "Profile",
                            modifier = Modifier
                                .padding(end = 16.dp)
                                .size(40.dp)
                                .clip(CircleShape)
                        )
                    }
                    Text(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        text = androidx.compose.ui.text.buildAnnotatedString {
                            append(person.name + " ")
                            val isMale = person.gender == s(R.string.male)
                            withStyle(
                                style = androidx.compose.ui.text.SpanStyle(
                                    color = if (isMale) androidx.compose.ui.graphics.Color.Blue
                                    else androidx.compose.ui.graphics.Color.Magenta
                                )
                            ) {
                                append(if (isMale) "\u2642" else "\u2640")
                            }
                        },
                        style = MaterialTheme.typography.titleMedium
                    )
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "Star",
                        modifier = Modifier.padding(
                            end = 16.dp
                        )
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = person.description,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
