//package com.lokahe.androidmvvm.ui.vectors
//
//import androidx.compose.foundation.Canvas
//import androidx.compose.foundation.layout.size
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.vector.PathParser
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//
//@Preview(showBackground = true)
//@Composable
//fun MaterialPathPreview() {
//    val myPathData = "M12,2L4.5,20.29L5.21,21L12,18L18.79,21L19.5,20.29L12,2Z" // ここにパスを貼る
//
//    Canvas(modifier = Modifier.size(100.dp)) {
//        // パス文字列をPathオブジェクトに変換して描画
//        val path = PathParser().parsePathString(myPathData).toPath()
//        drawPath(path, color = Color.Black)
//    }
//}