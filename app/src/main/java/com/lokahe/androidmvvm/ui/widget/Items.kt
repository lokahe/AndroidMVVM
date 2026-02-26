package com.lokahe.androidmvvm.ui.widget

import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale.Companion.Crop
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.lokahe.androidmvvm.R
import com.lokahe.androidmvvm.data.models.Person
import com.lokahe.androidmvvm.data.models.supabase.Post
import com.lokahe.androidmvvm.data.models.supabase.User
import com.lokahe.androidmvvm.emptyNull
import com.lokahe.androidmvvm.utils.Utils.Companion.genderLogo
import com.lokahe.androidmvvm.utils.Utils.Companion.postTitle
import com.lokahe.androidmvvm.utils.Utils.Companion.userTitle

@Composable
fun AvatarIcon(
    modifier: Modifier = Modifier,
    url: String?
) {
    url?.emptyNull()?.let {
        AsyncImage(
            model = url,
            contentDescription = stringResource(R.string.avatar),
            modifier = modifier.clip(RoundedCornerShape(20)),
            contentScale = Crop
        )
    } ?: Icon(
        modifier = modifier,
        imageVector = Icons.Filled.Person,
        contentDescription = ""
    )
}

@Composable
fun PostItem(
    index: Int,
    post: Post,
    liked: Boolean = false,
    editMode: Boolean,
    selected: Boolean = false,
    onLongClick: () -> Unit = {},
    onAuthorClick: (String) -> Unit = {},
    onLikeClick: (Int) -> Unit = {},
    onClick: (Int) -> Unit = {}
) {
    SuperCard(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        onClick = { onClick(index) },
        onLongClick = onLongClick,
        editMode = editMode,
        selected = selected
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row(verticalAlignment = CenterVertically) {
                AvatarIcon(
                    modifier = Modifier.padding(end = 16.dp).size(48.dp).clickable {
                        onAuthorClick(post.authorId)
                    },
                    url = post.profiles.avatar
                )
                Text(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    text = postTitle(post),
                    style = MaterialTheme.typography.titleMedium
                )
                Icon(
                    imageVector = Icons.Filled.Share,
                    contentDescription = stringResource(R.string.share),
                    modifier = Modifier.padding(end = 30.dp).size(20.dp),
                )
                Row(Modifier.padding(end = 30.dp)) {
                    Icon(
                        painter = painterResource(
                            if (liked) R.drawable.ic_heart_filled
                            else R.drawable.ic_heart_stroke
                        ),
                        contentDescription = stringResource(R.string.like),
                        modifier = Modifier.size(20.dp).padding(end = 4.dp)
                            .clickable { onLikeClick(index) },
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = post.likes[0].count.toString(),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = stringResource(R.string.favor),
                    modifier = Modifier.padding(end = 8.dp).size(20.dp),
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = post.content,
                style = MaterialTheme.typography.bodyMedium
            )
            if (post.imageUrls.isNotEmpty()) {
                Row {
                    post.imageUrls.split(",").forEach {
                        AsyncImage(
                            model = it,
                            contentDescription = stringResource(R.string.image),
                            modifier = Modifier.size(36.dp).padding(end = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun UserItem(index: Int, user: User) {
    Card(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row {
                AvatarIcon(
                    modifier = Modifier.padding(end = 16.dp).size(50.dp),
                    url = user.userMetadata?.avatarUrl
                )
                Text(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    text = userTitle(user),
                    style = MaterialTheme.typography.titleMedium
                )
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = "Star",
                    modifier = Modifier.padding(end = 10.dp).size(20.dp),
                )
            }
        }
    }
}

fun LazyListScope.personItem(index: Int, person: Person) {
    item {
        Card(
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row {
                    AvatarIcon(
                        modifier = Modifier.padding(end = 16.dp).size(36.dp),
                        url = person.image
                    )
                    Text(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        text = genderLogo(person.name, person.gender),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "Star",
                        modifier = Modifier.padding(end = 16.dp)
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
