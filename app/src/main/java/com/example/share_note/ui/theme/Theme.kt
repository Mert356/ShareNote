package com.example.share_note.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

// 🔥 Dark Mode Renk Teması
private val DarkColorPalette = darkColorScheme(
    primary = Green,           // Ana tema rengi
    secondary = Yellow,        // Yardımcı renk
    tertiary = LightGreen,     // Ekstra vurgu rengi

    background = Color.Black,  // Derin koyu gri (göz yormayan)
    surface = Color(0xFF1E1E1E),     // Hafif açık gri ton
    onPrimary = Color(0xFF121212),         // Ana renk üstündeki metin rengi
    onSecondary = Color.Black,       // Secondary üstü metin
    onBackground = Color.White,      // Arka plandaki metin rengi (okunaklı beyaz)
    onSurface = Color.White          // Yüzey üzerindeki yazı rengi
)


// 🌞 Light Mode Renk Teması
private val LightColorPalette = lightColorScheme(
    primary = Green,          // Ana renk
    secondary = Yellow,       // Yardımcı renk
    tertiary = LightGreen,    // Vurgu rengi
    background = Color(0xFFF5F5F5), // Hafif gri arka plan (tam beyaz yerine daha yumuşak)
    surface = Color.White,         // Kartlar veya yüzeyler için temiz beyaz
    onPrimary = Color(0xFFFAF9F6),       // Ana rengin üstündeki metin (okunaklı beyaz)
    onSecondary = Color.Black,     // Secondary rengi üstüne siyah metin
    onBackground = Color.Black,    // Arka planın üstüne siyah yazı
    onSurface = Color.Black        // Yüzeylerin üstüne siyah yazı
)


@Composable
fun MyAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = {
            CompositionLocalProvider(
                LocalContentColor provides contentColorFor(colorScheme.background)
            ) {
                content()
            }
        }
    )
}




