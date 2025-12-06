package com.lokahe.androidmvvm.ui.preference

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Stable
class PreferenceTheme(
    val categoryPadding: PaddingValues,
    val categoryColor: Color,
    val categoryTextStyle: TextStyle,
    val padding: PaddingValues,
    val horizontalSpacing: Dp,
    val verticalSpacing: Dp,
    val disabledOpacity: Float,
    val iconContainerMinWidth: Dp,
    val iconColor: Color,
    val titleColor: Color,
    val titleTextStyle: TextStyle,
    val summaryColor: Color,
    val summaryTextStyle: TextStyle,
    val dividerHeight: Dp,
    val headItemPadding: PaddingValues,
    val middleItemPadding: PaddingValues,
    val endItemPadding: PaddingValues,
    val grayOutAlpha: Float
)

@Composable
fun preferenceTheme(
    categoryPadding: PaddingValues =
        PaddingValues(start = 16.dp, top = 24.dp, end = 16.dp, bottom = 8.dp),
    categoryColor: Color = MaterialTheme.colorScheme.secondary,
    categoryTextStyle: TextStyle = MaterialTheme.typography.labelLarge,
    padding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
    horizontalSpacing: Dp = 16.dp,
    verticalSpacing: Dp = 16.dp,
    disabledOpacity: Float = 0.38f,
    iconContainerMinWidth: Dp = 56.dp,
    iconColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    titleColor: Color = MaterialTheme.colorScheme.onBackground,
    titleTextStyle: TextStyle = MaterialTheme.typography.titleLarge,
    summaryColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    summaryTextStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    dividerHeight: Dp = 32.dp,
    headItemPadding: PaddingValues = PaddingValues(
        start = 16.dp,
        top = 16.dp,
        end = 16.dp,
        bottom = 8.dp
    ),
    middleItemPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
    endItemPadding: PaddingValues = PaddingValues(
        start = 16.dp,
        top = 8.dp,
        end = 16.dp,
        bottom = 16.dp
    ),
    grayOutAlpha: Float = 0.38f
) =
    PreferenceTheme(
        categoryPadding = categoryPadding,
        categoryColor = categoryColor,
        categoryTextStyle = categoryTextStyle,
        padding = padding,
        horizontalSpacing = horizontalSpacing,
        verticalSpacing = verticalSpacing,
        disabledOpacity = disabledOpacity,
        iconContainerMinWidth = iconContainerMinWidth,
        iconColor = iconColor,
        titleColor = titleColor,
        titleTextStyle = titleTextStyle,
        summaryColor = summaryColor,
        summaryTextStyle = summaryTextStyle,
        dividerHeight = dividerHeight,
        headItemPadding = headItemPadding,
        middleItemPadding = middleItemPadding,
        endItemPadding = endItemPadding,
        grayOutAlpha = grayOutAlpha
    )