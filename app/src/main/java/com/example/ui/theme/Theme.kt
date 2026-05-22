package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFD43DFF), // Glowing Neon Purple
    secondary = Color(0xFFFF007F), // Glowing Neon Pink
    tertiary = Color(0xFF00ADB5), // Cyan Accent
    background = Color(0xFF080512), // Rich midnight dark canvas
    surface = Color(0xFF130F26), // Dark indigo-tinted surface
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFFF1EAFF),
    onSurface = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40,
    background = BackgroundLight,
    surface = CardBackgroundLight,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = TextDark,
    onSurface = TextDark
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+ (disable by default to guarantee custom mockup colors match theme exactly)
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// Custom extension for glowing neon pink and purple accent lines in midnight dark theme
fun Modifier.glowingNeonBorder(
    isDark: Boolean,
    shape: androidx.compose.ui.graphics.Shape = RoundedCornerShape(20.dp)
): Modifier {
    return if (isDark) {
        this.border(
            width = (1.5).dp,
            brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                colors = listOf(Color(0xFFD43DFF), Color(0xFFFF007F))
            ),
            shape = shape
        )
    } else {
        this
    }
}
