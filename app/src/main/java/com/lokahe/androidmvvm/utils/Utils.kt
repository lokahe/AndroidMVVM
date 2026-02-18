package com.lokahe.androidmvvm.utils

import android.annotation.SuppressLint
import android.graphics.drawable.BitmapDrawable
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.palette.graphics.Palette
import coil.ImageLoader
import coil.request.ImageRequest
import com.google.android.material.color.utilities.DynamicScheme
import com.google.android.material.color.utilities.Hct
import com.google.android.material.color.utilities.TonalPalette
import com.google.android.material.color.utilities.Variant
import com.lokahe.androidmvvm.AVATARS
import com.lokahe.androidmvvm.GENDERS
import com.lokahe.androidmvvm.MyApplication.Companion.application
import com.lokahe.androidmvvm.R
import com.lokahe.androidmvvm.argb
import com.lokahe.androidmvvm.data.models.Person
import com.lokahe.androidmvvm.data.models.Post
import com.lokahe.androidmvvm.data.models.supabase.User
import com.lokahe.androidmvvm.s
import com.lokahe.androidmvvm.toColorScheme
import com.lokahe.androidmvvm.ui.theme.ColorSeed
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

class Utils {
    companion object {
        val names0 by lazy {
            application.applicationContext.applicationContext.resources.getStringArray(R.array.female_names)
                .toList()
        }

        val names1 by lazy {
            application.applicationContext.applicationContext.resources.getStringArray(R.array.male_names)
                .toList()
        }

        fun randomPerson(gender: String? = null): Person {
            val gd = gender ?: GENDERS.random().first
            return Person(
                name = when (gd) {
                    GENDERS[0].first -> names0.random()
                    GENDERS[1].first -> names1.random()
                    else -> (names0 + names1).random()
                },
                description = s(R.string.desc_random_person),
                age = (18..65).random(),
                gender = gd,
                image = AVATARS.random()
                //"https://picsum.photos/200"
            )
        }

        fun md5(str: String): String {
            val digest = java.security.MessageDigest.getInstance("MD5")
            val bytes = digest.digest(str.toByteArray())
            return bytes.joinToString("") { "%02x".format(it) }
        }

        suspend fun calculateMainColor(url: String): ColorSeed? {
            if (url.isNotEmpty()) {
                val context = application
                val loader = ImageLoader(context)
                val request = ImageRequest.Builder(context)
                    .data(url)
                    .allowHardware(false) // important for Palette
                    .build()
                val result = loader.execute(request)
                (result.drawable as? BitmapDrawable)?.bitmap?.let { bitmap ->
                    val palette = Palette.from(bitmap).generate()
                    return ColorSeed(
                        palette.mutedSwatch?.rgb?.argb() ?: palette.vibrantSwatch?.rgb?.argb()
                        ?: palette.lightMutedSwatch?.rgb?.argb()
                        ?: palette.lightVibrantSwatch?.rgb?.argb()
                        ?: palette.darkMutedSwatch?.rgb?.argb()
                        ?: palette.darkVibrantSwatch?.rgb?.argb()
                        ?: palette.dominantSwatch?.rgb?.argb() ?: Color.Unspecified.toArgb()
                    )
                }
            }
            return null
        }

        @SuppressLint("RestrictedApi")
        fun createColorScheme(seedColor: ColorSeed, dark: Boolean): ColorScheme {
            val hct = Hct.fromInt(/*if (dark) seedColor.seedDark else */seedColor.seed)

            // Build tonal palettes based on Material 3 defaults
//            val primary = TonalPalette.fromHueAndChroma(hct.hue, 24.0)
//            val secondary = TonalPalette.fromHueAndChroma(hct.hue, 16.0)
//            val tertiary = TonalPalette.fromHueAndChroma(hct.hue + 60.0, 24.0)
//            val neutral = TonalPalette.fromHueAndChroma(hct.hue, 4.0)
//            val neutralVariant = TonalPalette.fromHueAndChroma(hct.hue, 8.0)
            val primary = TonalPalette.fromHueAndChroma(hct.hue, hct.chroma)
            val secondary = TonalPalette.fromHueAndChroma(hct.hue, hct.chroma * 0.66)
            val tertiary = TonalPalette.fromHueAndChroma(hct.hue + 60.0, hct.chroma)
            val neutral = TonalPalette.fromHueAndChroma(hct.hue, hct.chroma / 6)
            val neutralVariant = TonalPalette.fromHueAndChroma(hct.hue, hct.chroma / 3)

            return DynamicScheme(
                hct, Variant.TONAL_SPOT, // same as Material You
                dark, 0.0, primary, secondary,
                tertiary, neutral, neutralVariant
            ).toColorScheme()
        }

        fun genderLogo(name: String = "", gender: String?): AnnotatedString =
            buildAnnotatedString {
                append(name + if (name.isNotEmpty()) " " else "")
                gender?.let { gender ->
                    GENDERS.first { it.first == gender }.let {
                        withStyle(
                            style = SpanStyle(color = Color(it.third))
                        ) { append(it.second) }
                    }
                }
            }

        @Composable
        fun postTitle(post: Post): AnnotatedString =
            buildAnnotatedString {
                append(post.author + "\n")
                withStyle(
                    style = SpanStyle(
                        color = MaterialTheme.colorScheme.secondary,
                        fontSize = MaterialTheme.typography.bodySmall.fontSize
                    )
                ) {
                    append(
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                            .withZone(ZoneId.systemDefault())
                            .format(Instant.ofEpochMilli(post.created))
                    )
                }
            }

        @Composable
        fun userTitle(user: User): AnnotatedString =
            buildAnnotatedString {
                append(user.userMetadata.fullName + "\n")
                withStyle(
                    style = SpanStyle(
                        color = MaterialTheme.colorScheme.secondary,
                        fontSize = MaterialTheme.typography.bodySmall.fontSize
                    )
                ) {
//                    append(user.userMetadata.)
                }
            }


        /**
         * Step A: Percent-Encoding Utility
         * Standard URLEncoder doesn't strictly follow RFC 3986 (it encodes spaces as + instead of %20). Use this helper:
         */
        fun percentEncode(value: String): String {
            return java.net.URLEncoder.encode(value, "UTF-8")
                .replace("+", "%20")
                .replace("*", "%2A")
                .replace("%7E", "~")
        }

//        /**
//         * Step B: Build the Signature Base String (SBS)
//         * Collect all parameters (oauth and query params).
//         * Percent-encode every key and value.
//         * Sort them alphabetically by key.
//         * Join them with = and & to create a parameter string.
//         */
//        fun createSignatureBaseString(
//            method: String,
//            url: String,
//            params: Map<String, String>
//        ): String {
//            val sortedParams = params.entries
//                .sortedBy { it.key }
//                .joinToString("&") { "${percentEncode(it.key)}=${percentEncode(it.value)}" }
//
//            return "${method.uppercase()}&${percentEncode(url)}&${percentEncode(sortedParams)}"
//        }
//
//        /**
//         * Step C: Sign the SBS with HMAC-SHA1
//         * The signing key is your Consumer Secret and Token Secret (if any), joined by an &. For the initial "Request Token" step, the Token Secret is empty, so use CONSUMER_SECRET&.
//         */
//        fun generateSignature(
//            baseString: String,
//            consumerSecret: String,
//            tokenSecret: String = ""
//        ): String {
//            val keyString = "${percentEncode(consumerSecret)}&${percentEncode(tokenSecret)}"
//            val mac = Mac.getInstance("HmacSHA1")
//            val secretKey = SecretKeySpec(keyString.toByteArray(), "HmacSHA1")
//            mac.init(secretKey)
//
//            val hashBytes = mac.doFinal(baseString.toByteArray())
//            return Base64.encodeToString(hashBytes, Base64.NO_WRAP)
//        }

        fun generateOAuthSignature(
            method: String, url: String, params: Map<String, String>,
            consumerSecret: String, tokenSecret: String = ""
        ): String {
            // 1. Sort & encode params
            val paramStr = params.toSortedMap().entries.joinToString("&") {
                "${percentEncode(it.key)}=${
                    percentEncode(it.value)
                }"
            }

            // 2. Signature base string
            val baseUrl = url.split("?")[0].replace("https://", "https%3A%2F%2F")
            val baseString = "$method&$baseUrl&${percentEncode(paramStr)}"

            // 3. Signing key
            val key = "${percentEncode(consumerSecret)}&${percentEncode(tokenSecret)}"

            // 4. HMAC-SHA1
            val mac = Mac.getInstance("HmacSHA1")
            mac.init(SecretKeySpec(key.toByteArray(), "HmacSHA1"))
            val sigBytes = mac.doFinal(baseString.toByteArray())

            return java.util.Base64.getEncoder().encodeToString(sigBytes).trimEnd()
        }
    }
}