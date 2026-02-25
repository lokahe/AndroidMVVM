package com.lokahe.androidmvvm.ui.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * A 6-digit OTP input component with:
 * - Auto-advance to next field on digit input
 * - Auto-back to previous field on delete when empty
 * - Paste support (pastes up to 6 digits at once)
 * - Custom styling without OutlinedTextField limitations
 *
 * @param otpLength Number of OTP digits (default 6)
 * @param onOtpComplete Called when all digits are filled
 * @param modifier Modifier for the row container
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun OtpInputField(
    modifier: Modifier = Modifier,
    otpLength: Int = 6,
    onOtpComplete: (String) -> Unit = {}
) {
    // One TextFieldValue per cell
    val cellValues = remember {
        mutableStateListOf(*Array(otpLength) { TextFieldValue("") })
    }
    val focusRequesters = remember { List(otpLength) { FocusRequester() } }
    val clipboardManager: ClipboardManager = LocalClipboardManager.current

    // Helper: move focus safely
    fun focusCell(index: Int): Boolean =
        focusRequesters[index.coerceIn(0, otpLength - 1)].requestFocus()

    // Helper: handle paste from clipboard
    fun tryPaste(startIndex: Int) {
        val text = clipboardManager.getText()?.text ?: return
        val digits = text.filter { it.isDigit() }.take(otpLength - startIndex)
        if (digits.isEmpty()) return
        digits.forEachIndexed { i, ch ->
            val cellIndex = startIndex + i
            if (cellIndex < otpLength) {
                cellValues[cellIndex] = TextFieldValue(ch.toString(), TextRange(1))
            }
        }
        val nextFocus = (startIndex + digits.length).coerceAtMost(otpLength - 1)
        focusCell(nextFocus)

        val otp = cellValues.joinToString("") { it.text }
        if (otp.length == otpLength) onOtpComplete(otp)
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(otpLength) { index ->
            var isFocused by remember { mutableStateOf(false) }

            BasicTextField(
                modifier = Modifier.size(48.dp).weight(1f).clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.background)
//                    .background(if (isFocused) Color(0xFFEEF2FF) else Color(0xFFF8FAFC))
                    .border(
                        width = if (isFocused) 2.dp else 1.dp,
                        color = when {
                            isFocused -> MaterialTheme.colorScheme.primary// Color(0xFF6366F1)
                            cellValues[index].text.isNotEmpty() -> MaterialTheme.colorScheme.onPrimary // Color(0xFFA5B4FC)
                            else -> MaterialTheme.colorScheme.background // Color(0xFFE2E8F0)
                        },
                        shape = RoundedCornerShape(12.dp),
                    )
                    .focusRequester(focusRequesters[index])
                    .onFocusChanged { isFocused = it.isFocused }
                    // Handle backspace on an already-empty cell → go to previous
                    .onKeyEvent { event ->
                        if (event.key == Key.Backspace && cellValues[index].text.isEmpty() &&
                            index > 0
                        ) focusCell(index - 1) else false

                    },
                value = cellValues[index],
                onValueChange = { newValue ->
                    val newText = newValue.text

                    when {
                        // Paste: if more than 1 char was entered at once, distribute across cells
                        newText.length > 1 -> {
                            val digits = newText.filter { it.isDigit() }
                            if (digits.isEmpty()) return@BasicTextField
                            digits.take(otpLength - index).forEachIndexed { i, ch ->
                                val cellIndex = index + i
                                if (cellIndex < otpLength) {
                                    cellValues[cellIndex] =
                                        TextFieldValue(ch.toString(), TextRange(1))
                                }
                            }
                            val nextFocus = (index + digits.length).coerceAtMost(otpLength - 1)
                            focusCell(nextFocus)
                            val otp = cellValues.joinToString("") { it.text }
                            if (otp.length == otpLength && otp.none { it == ' ' }) onOtpComplete(otp)
                        }

                        // Normal single digit input
                        newText.length == 1 && newText.first().isDigit() -> {
                            cellValues[index] = TextFieldValue(newText, TextRange(1))
                            if (index < otpLength - 1) focusCell(index + 1)
                            val otp = cellValues.joinToString("") { it.text }
                            if (otp.length == otpLength && otp.none { it == ' ' }) onOtpComplete(otp)
                        }

                        // Cleared (delete/backspace)
                        newText.isEmpty() -> {
                            cellValues[index] = TextFieldValue("")
                            // If already empty, move to previous
                            if (cellValues[index].text.isEmpty() && index > 0) {
                                focusCell(index - 1)
                            }
                        }

                        // Non-digit: ignore
                        else -> return@BasicTextField
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = if (index == otpLength - 1) ImeAction.Done else ImeAction.Next,
                ),
                singleLine = true,
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary), // Color(0xFF6366F1)
                textStyle = TextStyle(
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground // Color(0xFF1E1B4B),
                ),
                decorationBox = { innerTextField ->
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        innerTextField()
                    }
                },
            )
        }
    }
}