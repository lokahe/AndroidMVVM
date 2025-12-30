package com.lokahe.androidmvvm.ui.preference

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import com.lokahe.androidmvvm.LocalTheme

inline fun LazyListScope.switchpreference(
    modifier: Modifier = Modifier,
    value: Boolean,
    crossinline title: @Composable () -> String,
    summary: String? = null,
    noinline enabled: @Composable () -> Boolean = { true },
    noinline icon: @Composable () -> Unit = {},
    noinline onValueChange: (Boolean) -> Unit
) {
    item(contentType = "SwitchPreference") {
        SwitchPreference(
            enabled = enabled,
            title = title(),
            summary = summary,
            icon = icon,
            modifier = modifier,
            value = value,
            onValueChange = onValueChange
        )
    }
}

@Composable
fun SwitchPreference(
    modifier: Modifier = Modifier,
    title: String,
    value: Boolean,
    summary: String? = null,
    enabled: @Composable () -> Boolean = { true },
    icon: @Composable () -> Unit = {},
    onValueChange: (Boolean) -> Unit
) {
    Preference(
        title = title,
        summary = summary,
        icon = icon,
        enabled = enabled,
        modifier = modifier.toggleable(
            value = value,
            enabled = enabled(),
            role = Role.Switch,
            onValueChange = onValueChange
        ),
        widgetContainer = {
            val theme = LocalTheme.current
            Switch(
                checked = value,
                onCheckedChange = onValueChange,
                modifier = Modifier.padding(horizontal = theme.horizontalSpacing),
                enabled = enabled(),
                thumbContent = {
                    Icon(
                        imageVector = if (value) Icons.Filled.Check else Icons.Filled.Close,
                        contentDescription = null,
                        modifier = Modifier.size(SwitchDefaults.IconSize),
                        tint = if (enabled()) if (value) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.surface
                        else if (value) MaterialTheme.colorScheme.onBackground.copy(alpha = theme.disabledOpacity)
                        else MaterialTheme.colorScheme.surface.copy(alpha = theme.disabledOpacity)
                    )
                }
            )
        }
    )
}