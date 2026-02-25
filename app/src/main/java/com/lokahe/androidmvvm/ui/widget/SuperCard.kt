package com.lokahe.androidmvvm.ui.widget

import android.graphics.BlurMaskFilter
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.combinedClickable
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
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
    val primaryColor = MaterialTheme.colorScheme.primary
    Card(
        modifier.combinedClickable(
            onClick = onClick,
            onLongClick = onLongClick
        ).drawBehind {
            if (editMode && selected) {
                val paint = Paint().asFrameworkPaint().apply {
                    // ぼかし効果を設定
                    maskFilter = BlurMaskFilter(8.dp.toPx(), BlurMaskFilter.Blur.OUTER)
                    color = primaryColor.toArgb()
                }
                // コンテンツの背後に光を描画
                drawIntoCanvas { canvas ->
                    canvas.nativeCanvas.drawRoundRect(
                        0f, 0f, size.width, size.height,
                        12.dp.toPx(), 12.dp.toPx(), // 角丸の半径
                        paint
                    )
                }
            }
        },
        border = if (editMode) BorderStroke(
            2.dp,
            if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
        ) else null
    ) {
        content()
    }
}