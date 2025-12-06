package com.lokahe.androidmvvm.ui.preference

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.lokahe.androidmvvm.toAny
import com.lokahe.androidmvvm.toFloat

@Composable
inline fun SliderDialogPreference(
    modifier: Modifier = Modifier,
    title: String,
    dialogTitle: String = title,
    value: Any,
    defaultValue: Any,
    summary: (Any) -> String = { "" },
    valueRange: ClosedFloatingPointRange<Float>,
    valueSteps: Int = (valueRange.endInclusive - valueRange.start).toInt(),
    crossinline enabled: @Composable () -> Boolean = { true },
    showConfirmButton: Boolean = true,
    showResetButton: Boolean = true,
    crossinline value2Slider: (Any) -> Float = { it.toFloat() },
    crossinline slider2Value: (Float) -> Any = { it.toAny(defaultValue = defaultValue) },
    crossinline onValueChange: (Any) -> Unit
) {
    var openSelector by rememberSaveable { mutableStateOf(false) }
    if (openSelector) {
        var valueTemp by rememberSaveable { mutableStateOf(value) }
        val onPositiveButtonClick = {
            onValueChange(valueTemp)
            openSelector = false
        }
        val onResetButtonClick = {
            onValueChange(defaultValue)
            openSelector = false
        }
        Dialog(
            onDismissRequest = { openSelector = false },
            title = dialogTitle,
            summary = summary(valueTemp),
            onPositiveButtonClick = if (showConfirmButton) onPositiveButtonClick else null,
            onResetButtonClick = if (showResetButton) onResetButtonClick else null
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Slider(
                    value = value2Slider(valueTemp),
                    onValueChange = { valueTemp = slider2Value(it) },
                    modifier = Modifier.weight(1f),
                    enabled = enabled(),
                    valueRange = valueRange,
                    steps = valueSteps
                )
            }
        }
    }
    Preference(
        title = title,
        modifier = modifier,
        enabled = { enabled() },
        summary = summary(value)
    ) {
        openSelector = true
    }
}