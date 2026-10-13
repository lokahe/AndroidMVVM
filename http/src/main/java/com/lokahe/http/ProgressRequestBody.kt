package com.lokahe.http

import android.content.Context
import android.net.Uri
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okio.BufferedSink
import java.io.File
import java.io.FileInputStream
import java.io.IOException

class ProgressRequestBody(
    private val file: File,
    private val contentType: String,
    private val onProgress: (percent: Int) -> Unit
) : RequestBody() {

    override fun contentType(): MediaType? = contentType.toMediaTypeOrNull()

    override fun contentLength(): Long = file.length()

    override fun writeTo(sink: BufferedSink) {
        val fileLength = file.length()
        val buffer = ByteArray(2048)
        val inputStream = FileInputStream(file)
        var uploaded = 0L

        inputStream.use { input ->
            var read: Int
            while (input.read(buffer).also { read = it } != -1) {
                // 進捗を計算して通知
                uploaded += read.toLong()
                sink.write(buffer, 0, read)
                onProgress(((uploaded * 100) / fileLength).toInt())
            }
        }
    }
}

class ProgressUriRequestBody(
    private val context: Context,
    private val uri: Uri,
    private val contentType: String,
    private val onProgress: (percent: Int) -> Unit
) : RequestBody() {

    private val contentResolver = context.contentResolver

    override fun contentType(): MediaType? = contentType.toMediaTypeOrNull()

    // ストリームを開いてサイズを取得
    override fun contentLength(): Long {
        return contentResolver.openAssetFileDescriptor(uri, "r")?.use { it.length } ?: -1L
    }

    override fun writeTo(sink: BufferedSink) {
        val fileLength = contentLength()
        val buffer = ByteArray(2048)

        // contentResolverから直接ストリームを開く
        val inputStream = contentResolver.openInputStream(uri)
            ?: throw IOException("Uriを開けませんでした")

        inputStream.use { input ->
            var uploaded = 0L
            var read: Int
            while (input.read(buffer).also { read = it } != -1) {
                uploaded += read.toLong()
                sink.write(buffer, 0, read)
                if (fileLength > 0) {
                    onProgress(((uploaded * 100) / fileLength).toInt())
                }
            }
        }
    }
}