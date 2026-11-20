package com.lokahe.androidmvvm.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lokahe.androidmvvm.R
import com.lokahe.androidmvvm.UserHeaderOption
import com.lokahe.androidmvvm.ui.widget.MainScaffold
import com.lokahe.androidmvvm.ui.widget.UserHeader
import com.lokahe.androidmvvm.viewmodels.AccountViewModel
import com.lokahe.domain.model.SessionInfo
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun AccountScreen(id: String? = null) {
    val viewModel = viewModel<AccountViewModel>()
    val user by viewModel.currentUser.collectAsState()
    val identities by viewModel.identities.collectAsState()
    val sessions by viewModel.sessions.collectAsState()
    LaunchedEffect(Unit) { viewModel.refresh() }
    MainScaffold(
        title = stringResource(R.string.account)
    ) { contentPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = contentPadding
        ) {
            item { UserHeader(user, UserHeaderOption.Sign) }
            item {
                SectionCard(title = stringResource(R.string.section_account)) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        IconButton(onClick = { viewModel.refresh() }) {
                            Icon(
                                imageVector = Icons.Filled.Refresh,
                                contentDescription = stringResource(R.string.refresh)
                            )
                        }
                    }
                    ProfileRow(
                        label = stringResource(R.string.email),
                        value = user?.email
                    )
                    ProfileRow(
                        label = stringResource(R.string.email_verified),
                        value = stringResource(
                            if (user?.profile?.emailConfirmedAt?.isNotBlank() == true)
                                R.string.yes else R.string.no
                        )
                    )
                    ProfileRow(
                        label = stringResource(R.string.registered_at),
                        value = user?.profile?.createdAt?.formatDateTime()
                    )
                }
            }
            item {
                SectionCard(title = stringResource(R.string.section_identities)) {
                    if (identities.isEmpty()) {
                        Text(
                            modifier = Modifier.padding(16.dp),
                            text = stringResource(R.string.no_identities),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    } else {
                        identities.forEach { identity ->
                            Row(
                                modifier = Modifier.fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 4.dp),
                                verticalAlignment = CenterVertically
                            ) {
                                Icon(
                                    imageVector = platformIcon(identity.provider),
                                    contentDescription = platformName(identity.provider),
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                                Column {
                                    Text(
                                        text = platformName(identity.provider),
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(
                                        text = listOfNotNull(
                                            identity.email,
                                            identity.createdAt?.formatDateTime()
                                        ).joinToString(" · "),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
            item {
                SectionCard(title = stringResource(R.string.section_devices)) {
                    if (sessions.isEmpty()) {
                        Text(
                            modifier = Modifier.padding(16.dp),
                            text = stringResource(R.string.no_sessions),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    } else {
                        sessions.forEach { session ->
                            SessionRow(session) { viewModel.kick(session.id) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionCard(title: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column {
            Text(
                modifier = Modifier.padding(start = 16.dp, top = 12.dp),
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            content()
        }
    }
}

@Composable
private fun ProfileRow(label: String, value: String?) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
        Text(
            text = value ?: "-",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun SessionRow(session: SessionInfo, onKick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
        verticalAlignment = CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.Smartphone,
            contentDescription = stringResource(R.string.section_devices),
            modifier = Modifier.padding(end = 8.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = session.deviceLabel ?: session.userAgent
                ?: stringResource(R.string.unknown_device),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = listOfNotNull(
                    session.createdAt?.formatDateTime(),
                    session.ip
                ).joinToString(" · "),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        TextButton(onClick = onKick) {
            Text(text = stringResource(R.string.kick_device))
        }
    }
}

private fun platformName(provider: String): String = when (provider) {
    "github" -> "GitHub"
    "magic" -> "Magic Link"
    else -> provider
}

private fun platformIcon(provider: String): ImageVector = when (provider) {
    "github" -> Icons.Filled.Key
    else -> Icons.Filled.Link
}

private fun String.formatDateTime(): String? = runCatching {
    OffsetDateTime.parse(this).format(
        DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
    )
}.getOrNull()
