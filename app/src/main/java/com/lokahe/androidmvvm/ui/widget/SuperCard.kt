package com.lokahe.androidmvvm.ui.widget

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.combinedClickable
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SuperCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    onLongClick: () -> Unit = {},
    editMode: Boolean = false,
    selected: Boolean = false,
    content: @Composable () -> Unit
) {
    Card(
        modifier.combinedClickable(
            onClick = onClick,
            onLongClick = onLongClick
        ),
        border = if (editMode) BorderStroke(
            2.dp,
            if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
        ) else null
    ) {
        content()
    }
}