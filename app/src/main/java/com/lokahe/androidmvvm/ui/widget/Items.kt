package com.lokahe.androidmvvm.ui.widget

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale.Companion.Crop
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.lokahe.androidmvvm.R
import com.lokahe.androidmvvm.models.Person
import com.lokahe.androidmvvm.models.network.Post
import com.lokahe.androidmvvm.models.network.User
import com.lokahe.androidmvvm.utils.Utils.Companion.genderLogo

@Composable
fun AvatarIcon(
    modifier: Modifier = Modifier,
    url: String
) {
    if (url.isNotEmpty()) {
        AsyncImage(
            model = url,
            contentDescription = stringResource(R.string.avatar),
            modifier = modifier.clip(RoundedCornerShape(20)),
            contentScale = Crop
        )
    } else {
        Icon(
            modifier = modifier,
            imageVector = Icons.Filled.Person,
            contentDescription = ""
        )
    }
}

@Composable
fun PostItem(
    index: Int,
    post: Post
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row {
                AvatarIcon(
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .size(36.dp),
                    url = post.avatar
                )
                Text(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    text = genderLogo(post.author, post.authorGender),
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
                text = post.content,
                style = MaterialTheme.typography.bodyMedium
            )
            if (post.images.isNotEmpty()) {
                Row() {
                    post.images.split(",").forEach {
                        AsyncImage(
                            model = it,
                            contentDescription = stringResource(R.string.image),
                            modifier = Modifier
                                .size(36.dp)
                                .padding(end = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun UserItem(
    index: Int,
    user: User
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row {
                AvatarIcon(
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .size(36.dp),
                    url = user.avatar ?: ""
                )
                Text(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    text = genderLogo(user.name, user.gender),
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
                text = user.description ?: "",
                style = MaterialTheme.typography.bodyMedium
            )
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
                    AvatarIcon(
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .size(36.dp),
                        url = person.image
                    )
                    Text(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        text = genderLogo(person.name, person.gender),
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
