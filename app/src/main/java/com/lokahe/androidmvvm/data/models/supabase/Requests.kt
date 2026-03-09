package com.lokahe.androidmvvm.data.models.supabase

import android.content.Context
import android.net.Uri
import com.google.gson.annotations.SerializedName
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okio.BufferedSink
import java.io.File
import java.io.FileInputStream
import java.io.IOException

data class SignRequest(
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String?,
    @SerializedName("options")
    val options: SignUpOptions?
)

data class SignUpOptions(
    @SerializedName("data")
    val data: Map<String, Any> // Use a Map or a specific Metadata class
)

data class OtpRequest(
    @SerializedName("email")
    val email: String,
    @SerializedName("create_user")
    val createUser: Boolean = true
)

data class SetPasswordRequest(
    @SerializedName("password")
    val password: String
)

data class VerifyRequest(
    @SerializedName("type")
    val type: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("token")
    val token: String
)

data class RefreshTokenRequest(
    @SerializedName("refresh_token")
    val refreshToken: String
)

data class CodeExchangeRequest(
    @SerializedName("auth_code")
    val authCode: String,
    @SerializedName("code_verifier")
    val codeVerifier: String
)

data class FollowRequest(
    @SerializedName("follower_id")
    val followerId: String,
    @SerializedName("target_id")
    val targetId: String
)

data class LikeRequest(
    @SerializedName("post_id")
    val postId: String,
    @SerializedName("user_id")
    val userId: String
)

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