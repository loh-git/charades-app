package com.zoomi.charades.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.zoomi.charades.data.AppTheme

// Unchanged from before theming was introduced — Default must stay pixel-identical.
private val DefaultColorScheme = darkColorScheme(
    primary = OrangeAccent,
    onPrimary = TextPrimary,
    secondary = PinkAccent,
    onSecondary = TextPrimary,
    tertiary = CorrectGreen,
    background = BackgroundDark,
    onBackground = TextPrimary,
    surface = SurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = TextSecondary,
    outline = OutlineDark,
)

private val LightColorScheme = lightColorScheme(
    primary = OrangeAccent,
    onPrimary = OnPrimaryLight,
    secondary = PinkAccent,
    onSecondary = TextPrimaryLight,
    tertiary = CorrectGreen,
    background = BackgroundLight,
    onBackground = TextPrimaryLight,
    surface = SurfaceLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = TextSecondaryLight,
    outline = OutlineLight,
)

private val PopArtColorScheme = lightColorScheme(
    primary = PopArtRed,
    onPrimary = Color.White,
    secondary = PopArtBlue,
    onSecondary = Color.White,
    tertiary = PopArtYellow,
    background = BackgroundPopArt,
    onBackground = TextPrimaryPopArt,
    surface = SurfacePopArt,
    onSurface = TextPrimaryPopArt,
    surfaceVariant = SurfaceVariantPopArt,
    onSurfaceVariant = TextSecondaryPopArt,
    outline = OutlinePopArt,
)

private val TestColorScheme = lightColorScheme(
    primary = TestBlue,
    onPrimary = Color.White,
    secondary = TestPurple,
    onSecondary = Color.White,
    tertiary = TestGreen,
    background = BackgroundTest,
    onBackground = TextPrimaryTest,
    surface = SurfaceTest,
    onSurface = TextPrimaryTest,
    surfaceVariant = SurfaceVariantTest,
    onSurfaceVariant = TextSecondaryTest,
    outline = OutlineTest,
)

private fun colorSchemeFor(theme: AppTheme) = when (theme) {
    AppTheme.DEFAULT -> DefaultColorScheme
    AppTheme.LIGHT -> LightColorScheme
    AppTheme.POP_ART -> PopArtColorScheme
    AppTheme.TEST -> TestColorScheme
}

@Composable
fun CharadesTheme(theme: AppTheme = AppTheme.DEFAULT, content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalExtendedColors provides extendedColorsFor(theme)) {
        MaterialTheme(
            colorScheme = colorSchemeFor(theme),
            content = content,
        )
    }
}
