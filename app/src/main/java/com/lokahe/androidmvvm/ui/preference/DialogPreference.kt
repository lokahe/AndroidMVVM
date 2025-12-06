package com.lokahe.androidmvvm.ui.preference

import android.graphics.Bitmap
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastForEachIndexed
import com.lokahe.androidmvvm.R
import kotlin.math.max

@Composable
fun ConfirmDialogPreference(
    modifier: Modifier = Modifier,
    title: String,
    dialogTitle: String = title,
    summary: String? = null,
    enabled: @Composable () -> Boolean = { true },
    @DrawableRes titleIcon: Int? = null,
    titleBitmap: Bitmap? = null,
    onPositiveButtonClick: (() -> Unit)? = null,
) {
    var openDialog by rememberSaveable { mutableStateOf(false) }
    if (openDialog) {
        Dialog(
            title = dialogTitle,
            titleIcon = titleIcon,
            titleBitmap = titleBitmap,
            onDismissRequest = { openDialog = false },
            onPositiveButtonClick = {
                onPositiveButtonClick?.invoke()
                openDialog = false
            }
        )
    }
    Preference(
        title = title,
        modifier = modifier,
        enabled = enabled,
        summary = summary
    ) {
        openDialog = true
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Dialog(
    modifier: Modifier = Modifier,
    title: String,
    summary: String? = null,
    @DrawableRes titleIcon: Int? = null,
    titleBitmap: Bitmap? = null,
    onDismissRequest: () -> Unit,
    positiveButtonText: String = stringResource(R.string.ok),
    onPositiveButtonClick: (() -> Unit)? = null,
    positiveBtnEnabledState: State<Boolean>? = null,
    negativeButtonText: String = stringResource(R.string.cancel),
    negativeBtnEnabledState: State<Boolean>? = null,
    onResetButtonClick: (() -> Unit)? = null,
    resetButtonText: String = stringResource(R.string.reset),
    contentPaddingValues: PaddingValues = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
    content: @Composable () -> Unit = {}
) {
    BasicAlertDialog(onDismissRequest = onDismissRequest, modifier = modifier) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = AlertDialogDefaults.shape,
            color = AlertDialogDefaults.containerColor,
            tonalElevation = AlertDialogDefaults.TonalElevation,
        ) {
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)) {
                ProvideContentColorTextStyle(
                    contentColor = AlertDialogDefaults.titleContentColor,
                    textStyle = MaterialTheme.typography.headlineSmall,
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        titleIcon?.let {
                            Image(
                                painter = painterResource(id = it),
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                        }
                        titleBitmap?.let {
                            Image(
                                bitmap = it.asImageBitmap(),
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                        }
                        Text(text = title, style = MaterialTheme.typography.titleLarge)
                    }
                    summary?.let {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(contentPaddingValues)
                        .weight(1f, fill = false)
                ) { content() }
                ProvideContentColorTextStyle(
                    contentColor = MaterialTheme.colorScheme.primary,
                    textStyle = MaterialTheme.typography.labelLarge
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 2.dp)
                    ) {
                        onResetButtonClick?.let {
                            Box(
                                modifier = Modifier.wrapContentWidth(),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides Dp.Unspecified) {
                                    TextButton(onClick = {
                                        it()
                                        onDismissRequest()
                                    }) {
                                        Text(text = resetButtonText)
                                    }
                                }
                            }
                        }
                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            AlertDialogFlowRow(mainAxisSpacing = 8.dp, crossAxisSpacing = 12.dp) {
                                CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides Dp.Unspecified) {
                                    TextButton(
                                        onClick = onDismissRequest,
                                        enabled = negativeBtnEnabledState?.value ?: true
                                    ) {
                                        Text(text = negativeButtonText)
                                    }
                                    onPositiveButtonClick?.let {
                                        TextButton(
                                            onClick = {
                                                it()
                                                onDismissRequest()
                                            },
                                            enabled = positiveBtnEnabledState?.value ?: true
                                        ) {
                                            Text(text = positiveButtonText)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// Copied from androidx.compose.material3.internal.ProvideContentColorTextStyle .
@Composable
private fun ProvideContentColorTextStyle(
    contentColor: Color,
    textStyle: TextStyle,
    content: @Composable () -> Unit,
) {
    val mergedStyle = LocalTextStyle.current.merge(textStyle)
    CompositionLocalProvider(
        LocalContentColor provides contentColor,
        LocalTextStyle provides mergedStyle,
        content = content,
    )
}


// Copied from androidx.compose.material3.AlertDialogFlowRow .
@Composable
private fun AlertDialogFlowRow(
    mainAxisSpacing: Dp,
    crossAxisSpacing: Dp,
    content: @Composable () -> Unit,
) {
    Layout(content) { measurables, constraints ->
        val sequences = mutableListOf<List<Placeable>>()
        val crossAxisSizes = mutableListOf<Int>()
        val crossAxisPositions = mutableListOf<Int>()

        var mainAxisSpace = 0
        var crossAxisSpace = 0

        val currentSequence = mutableListOf<Placeable>()
        var currentMainAxisSize = 0
        var currentCrossAxisSize = 0

        // Return whether the placeable can be added to the current sequence.
        fun canAddToCurrentSequence(placeable: Placeable) =
            currentSequence.isEmpty() ||
                    currentMainAxisSize + mainAxisSpacing.roundToPx() + placeable.width <=
                    constraints.maxWidth

        // Store current sequence information and start a new sequence.
        fun startNewSequence() {
            if (sequences.isNotEmpty()) {
                crossAxisSpace += crossAxisSpacing.roundToPx()
            }
            // Ensures that confirming actions appear above dismissive actions.
            @Suppress("ListIterator") sequences.add(0, currentSequence.toList())
            crossAxisSizes += currentCrossAxisSize
            crossAxisPositions += crossAxisSpace

            crossAxisSpace += currentCrossAxisSize
            mainAxisSpace = max(mainAxisSpace, currentMainAxisSize)

            currentSequence.clear()
            currentMainAxisSize = 0
            currentCrossAxisSize = 0
        }

        measurables.fastForEach { measurable ->
            // Ask the child for its preferred size.
            val placeable = measurable.measure(constraints)

            // Start a new sequence if there is not enough space.
            if (!canAddToCurrentSequence(placeable)) startNewSequence()

            // Add the child to the current sequence.
            if (currentSequence.isNotEmpty()) {
                currentMainAxisSize += mainAxisSpacing.roundToPx()
            }
            currentSequence.add(placeable)
            currentMainAxisSize += placeable.width
            currentCrossAxisSize = max(currentCrossAxisSize, placeable.height)
        }

        if (currentSequence.isNotEmpty()) startNewSequence()

        val mainAxisLayoutSize = max(mainAxisSpace, constraints.minWidth)

        val crossAxisLayoutSize = max(crossAxisSpace, constraints.minHeight)

        val layoutWidth = mainAxisLayoutSize

        val layoutHeight = crossAxisLayoutSize

        layout(layoutWidth, layoutHeight) {
            sequences.fastForEachIndexed { i, placeables ->
                val childrenMainAxisSizes =
                    IntArray(placeables.size) { j ->
                        placeables[j].width +
                                if (j < placeables.lastIndex) mainAxisSpacing.roundToPx() else 0
                    }
                val arrangement = Arrangement.End
                val mainAxisPositions = IntArray(childrenMainAxisSizes.size) { 0 }
                with(arrangement) {
                    arrange(
                        mainAxisLayoutSize,
                        childrenMainAxisSizes,
                        layoutDirection,
                        mainAxisPositions,
                    )
                }
                placeables.fastForEachIndexed { j, placeable ->
                    placeable.place(x = mainAxisPositions[j], y = crossAxisPositions[i])
                }
            }
        }
    }
}
