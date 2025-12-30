package com.lokahe.androidmvvm.ui.preference

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.lokahe.androidmvvm.LocalTheme
import com.lokahe.androidmvvm.copy

fun LazyListScope.preference(
    title: String,
    summary: String? = null,
    enabled: @Composable () -> Boolean = { true },
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit = {},
    widgetContainer: @Composable () -> Unit = {},
    onClick: () -> Unit
) {
    item(contentType = "Preference") {
        Preference(
            title = title,
            summary = summary,
            enabled = enabled,
            onClick = onClick,
            icon = icon,
            widgetContainer = widgetContainer,
            modifier = modifier
        )
    }
}

@Composable
fun Preference(
    modifier: Modifier = Modifier,
    title: String,
    summary: String? = null,
    enabled: @Composable () -> Boolean = { true },
    icon: @Composable () -> Unit = {},
    widgetContainer: @Composable () -> Unit = {},
    onClick: (() -> Unit)? = null
) {
    val theme = LocalTheme.current
    val textColor =
        if (enabled()) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(
            alpha = theme.disabledOpacity
        )
    Row(
        modifier = onClick?.let { modifier.then(Modifier.clickable(enabled(), onClick = it)) }
            ?: modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        icon()
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(theme.padding.copy())
        ) {
            Text(text = title, style = MaterialTheme.typography.titleMedium, color = textColor)
            summary?.let {
                if (it.isNotEmpty()) {
                    Text(
                        text = summary,
                        style = MaterialTheme.typography.bodyMedium,
                        color = textColor
                    )
                }
            }
        }
        widgetContainer()
    }
}