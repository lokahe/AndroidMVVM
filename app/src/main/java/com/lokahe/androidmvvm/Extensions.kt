package com.lokahe.androidmvvm

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.TextUnit
import androidx.core.graphics.ColorUtils
import com.google.android.material.color.utilities.DynamicScheme
import com.lokahe.androidmvvm.MyApplication.Companion.application

fun s(@StringRes id: Int): String = application.getString(id)
fun Int.max(max: Int): Int = this.coerceAtMost(max)
fun Int.min(min: Int): Int = this.coerceAtLeast(min)
fun Int.limit(min: Int, max: Int): Int = this.coerceAtLeast(min).coerceAtMost(max)
fun Int.argb(): Int {
    return 0xFF shl 24 or this
}

fun Int.has(flag: Int): Boolean = this and flag != 0
fun Int.set(flag: Int): Int = this or flag
fun Int.clear(flag: Int): Int = this and flag.inv()

fun Int.toHexColor(): String {
    return String.format("#%08X", this)
}

fun toast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(application, message, duration).show()
}

@SuppressLint("RestrictedApi")
fun DynamicScheme.toColorScheme(): ColorScheme {
    return ColorScheme(
        primary = Color(primary),
        onPrimary = Color(onPrimary),
        primaryContainer = Color(primaryContainer),
        onPrimaryContainer = Color(onPrimaryContainer),
        inversePrimary = Color(inversePrimary),
        secondary = Color(secondary),
        onSecondary = Color(onSecondary),
        secondaryContainer = Color(secondaryContainer),
        onSecondaryContainer = Color(onSecondaryContainer),
        tertiary = Color(tertiary),
        onTertiary = Color(onTertiary),
        tertiaryContainer = Color(tertiaryContainer),
        onTertiaryContainer = Color(onTertiaryContainer),
        background = Color(background),
        onBackground = Color(onBackground),
        surface = Color(surface),
        onSurface = Color(onSurface),
        surfaceVariant = Color(surfaceVariant),
        onSurfaceVariant = Color(onSurfaceVariant),
        surfaceTint = Color(primary),
        inverseSurface = Color(inverseSurface),
        inverseOnSurface = Color(inverseOnSurface),
        error = Color(error),
        onError = Color(onError),
        errorContainer = Color(errorContainer),
        onErrorContainer = Color(onErrorContainer),
        outline = Color(outline),
        outlineVariant = Color(outlineVariant),
        scrim = Color(scrim),
        // Add default values for new M3 colors if needed, but the above covers the core
        surfaceBright = Color(surfaceBright),
        surfaceDim = Color(surfaceDim),
        surfaceContainer = Color(surfaceContainer),
        surfaceContainerHigh = Color(surfaceContainerHigh),
        surfaceContainerHighest = Color(surfaceContainerHighest),
        surfaceContainerLow = Color(surfaceContainerLow),
        surfaceContainerLowest = Color(surfaceContainerLowest),
    )
}

fun Color.isDark(): Boolean {
    // Convert Compose Color to Android ColorInt
    val colorInt = android.graphics.Color.argb(
        (alpha * 255).toInt(),
        (red * 255).toInt(),
        (green * 255).toInt(),
        (blue * 255).toInt()
    )
    // Luminance threshold is typically 0.5
    return ColorUtils.calculateLuminance(colorInt) < 0.5
}

@Composable
fun TextUnit.toDp(): Dp = with(LocalDensity.current) { toPx().toDp() }

fun <T> Set<T>.deal(any: Any, enable: Boolean) = if (enable) this.plus(any) else this.minus(any)
fun <T> Set<T>.newSize(any: Any, enable: Boolean) = deal(any, enable).size

fun <T> MutableSet<T>.set(value: Set<T>) {
    clear()
    addAll(value)
}

fun <T> MutableState<Set<T>>.set(value: Set<T>) {
    this.value = value
}


fun Any.toAny(defaultValue: Any): Any {
    when (defaultValue) {
        is Int -> return this.toInt()
        is Float -> return this.toFloat()
        is String -> return this.toString()
        else -> return defaultValue
    }
}

fun Any.toInt(): Int {
    when (this) {
        is Int -> return this
        is Float -> return this.toInt()
        is String -> return this.toIntOrNull() ?: 0
        else -> return 0
    }
}

fun Any.toFloat(): Float {
    when (this) {
        is Int -> return this.toFloat()
        is Float -> return this
        is String -> return this.toFloatOrNull() ?: 0f
        else -> return 0f
    }
}

fun Any.toLong(): Long {
    when (this) {
        is Long -> return this
        is Int -> return this.toLong()
        is Float -> return this.toLong()
        is String -> return this.toLongOrNull() ?: 0L
        else -> return 0L
    }
}

fun Any.toBoolean(): Boolean {
    when (this) {
        is Boolean -> return this
        is String -> return this.toBooleanStrictOrNull() ?: false
        else -> return false
    }
}

@Suppress("UNCHECKED_CAST")
fun Any.addOrRemove(any: Any): Set<Any> =
    when (this) {
        is Set<*> -> {
            if (this.contains(any)) {
                this.minus(any)
            } else {
                this.plus(any)
            }
        }

        else -> setOf(any)
    } as Set<Any>

fun Any.expextSize(any: Any): Int =
    when (this) {
        is Set<*> -> if (this.contains(any)) this.size - 1 else this.size + 1
        else -> 0
    }

fun Any.size(): Int =
    when (this) {
        is String -> this.length
        is Set<*> -> this.size
        is List<*> -> this.size
        is Map<*, *> -> this.size
        is Array<*> -> this.size
        is CharSequence -> this.length
        is IntArray -> this.size
        is LongArray -> this.size
        is FloatArray -> this.size
        is DoubleArray -> this.size
        is BooleanArray -> this.size
        is CharArray -> this.size
        is ShortArray -> this.size
        else -> 0
    }

fun Int.between(min: Int, max: Int) = Math.max(Math.min(this, max), min)

@Composable
internal fun PaddingValues.copy(
    horizontal: Dp = Dp.Unspecified,
    vertical: Dp = Dp.Unspecified,
): PaddingValues = copy(start = horizontal, top = vertical, end = horizontal, bottom = vertical)

@Composable
internal fun PaddingValues.copy(
    start: Dp = Dp.Unspecified,
    top: Dp = Dp.Unspecified,
    end: Dp = Dp.Unspecified,
    bottom: Dp = Dp.Unspecified,
): PaddingValues = CopiedPaddingValues(start, top, end, bottom, this)

@Stable
private class CopiedPaddingValues(
    private val start: Dp,
    private val top: Dp,
    private val end: Dp,
    private val bottom: Dp,
    private val paddingValues: PaddingValues,
) : PaddingValues {
    override fun calculateLeftPadding(layoutDirection: LayoutDirection): Dp =
        (if (layoutDirection == LayoutDirection.Ltr) start else end).takeIf { it != Dp.Unspecified }
            ?: paddingValues.calculateLeftPadding(layoutDirection)

    override fun calculateTopPadding(): Dp =
        top.takeIf { it != Dp.Unspecified } ?: paddingValues.calculateTopPadding()

    override fun calculateRightPadding(layoutDirection: LayoutDirection): Dp =
        (if (layoutDirection == LayoutDirection.Ltr) end else start).takeIf { it != Dp.Unspecified }
            ?: paddingValues.calculateRightPadding(layoutDirection)

    override fun calculateBottomPadding(): Dp =
        bottom.takeIf { it != Dp.Unspecified } ?: paddingValues.calculateBottomPadding()

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other !is CopiedPaddingValues) {
            return false
        }
        return start == other.start &&
                top == other.top &&
                end == other.end &&
                bottom == other.bottom &&
                paddingValues == other.paddingValues
    }

    override fun hashCode(): Int {
        var result = start.hashCode()
        result = 31 * result + top.hashCode()
        result = 31 * result + end.hashCode()
        result = 31 * result + bottom.hashCode()
        result = 31 * result + paddingValues.hashCode()
        return result
    }

    override fun toString(): String {
        return "Copied($start, $top, $end, $bottom, $paddingValues)"
    }
}

//@Composable
//internal fun PaddingValues.offset(all: Dp = 0.dp): PaddingValues = offset(all, all, all, all)
//
//@Composable
//internal fun PaddingValues.offset(horizontal: Dp = 0.dp, vertical: Dp = 0.dp): PaddingValues =
//    offset(horizontal, vertical, horizontal, vertical)
//
//@Composable
//internal fun PaddingValues.offset(
//    start: Dp = 0.dp,
//    top: Dp = 0.dp,
//    end: Dp = 0.dp,
//    bottom: Dp = 0.dp,
//): PaddingValues = OffsetPaddingValues(start, top, end, bottom, this)
//
//@Stable
//private class OffsetPaddingValues(
//    private val start: Dp,
//    private val top: Dp,
//    private val end: Dp,
//    private val bottom: Dp,
//    private val paddingValues: PaddingValues,
//) : PaddingValues {
//    override fun calculateLeftPadding(layoutDirection: LayoutDirection): Dp =
//        paddingValues.calculateLeftPadding(layoutDirection) +
//                (if (layoutDirection == LayoutDirection.Ltr) start else end)
//
//    override fun calculateTopPadding(): Dp = paddingValues.calculateTopPadding() + top
//
//    override fun calculateRightPadding(layoutDirection: LayoutDirection): Dp =
//        paddingValues.calculateRightPadding(layoutDirection) +
//                (if (layoutDirection == LayoutDirection.Ltr) end else start)
//
//    override fun calculateBottomPadding(): Dp = paddingValues.calculateBottomPadding() + bottom
//
//    override fun equals(other: Any?): Boolean {
//        if (this === other) {
//            return true
//        }
//        if (other !is OffsetPaddingValues) {
//            return false
//        }
//        return start == other.start &&
//                top == other.top &&
//                end == other.end &&
//                bottom == other.bottom &&
//                paddingValues == other.paddingValues
//    }
//
//    override fun hashCode(): Int {
//        var result = start.hashCode()
//        result = 31 * result + top.hashCode()
//        result = 31 * result + end.hashCode()
//        result = 31 * result + bottom.hashCode()
//        result = 31 * result + paddingValues.hashCode()
//        return result
//    }
//
//    override fun toString(): String {
//        return "Offset($start, $top, $end, $bottom, $paddingValues)"
//    }
//}