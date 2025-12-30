package com.lokahe.androidmvvm.utils

import android.annotation.SuppressLint
import android.graphics.drawable.BitmapDrawable
import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.palette.graphics.Palette
import coil.ImageLoader
import coil.request.ImageRequest
import com.google.android.material.color.utilities.DynamicScheme
import com.google.android.material.color.utilities.Hct
import com.google.android.material.color.utilities.TonalPalette
import com.google.android.material.color.utilities.Variant
import com.lokahe.androidmvvm.MyApplication.Companion.application
import com.lokahe.androidmvvm.R
import com.lokahe.androidmvvm.argb
import com.lokahe.androidmvvm.models.Person
import com.lokahe.androidmvvm.s
import com.lokahe.androidmvvm.toColorScheme
import com.lokahe.androidmvvm.ui.theme.ColorSeed

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
            val gd = gender ?: listOf(s(R.string.female), s(R.string.male)).random()
            return Person(
                name = if (gd == s(R.string.female)) names0.random() else names1.random(),
                description = s(R.string.desc_random_person),
                age = (18..65).random(),
                gender = gd,
                image = "https://fastly.picsum.photos/id/336/200/200.jpg?hmac=VZ7MzNM30jINYNf5Oj_8zqPLTDAyKDk6eXWTGnNb4bU"
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
    }
}