package com.zoomi.charades.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Density
import com.zoomi.charades.data.AppTheme

// "Sunset Arcade" (AppTheme.DEFAULT) — the app's first-launch theme. Originally locked
// pixel-identical to the pre-theming app; deliberately updated per an explicit redesign request,
// now matched to the designer's exact spec (Tailwind tokens — see Color.kt). No longer treat this
// scheme as frozen without checking. onPrimary is intentionally dark (BackgroundDark, not
// TextPrimary) — the reference calls for dark text on the amber primary buttons/badges.
private val DefaultColorScheme = darkColorScheme(
    primary = OrangeAccent,
    onPrimary = BackgroundDark,
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

// "Matrix Terminal" — a pure-black CRT-terminal theme with neon phosphor green, per an explicit
// design spec. onPrimary/onSecondary are black (dark text on solid green buttons/pills, per the
// spec's "Inverted Button Text: text-black"). tertiary keeps the universal success green used by
// every theme, since it isn't part of this theme's own brand palette.
private val MatrixColorScheme = darkColorScheme(
    primary = MatrixPrimaryGreen,
    onPrimary = Color.Black,
    secondary = MatrixBrightGreen,
    onSecondary = Color.Black,
    tertiary = CorrectGreen,
    background = MatrixBackground,
    onBackground = MatrixBrightGreen,
    surface = MatrixBackground,
    onSurface = MatrixBrightGreen,
    surfaceVariant = MatrixContainerGreen,
    onSurfaceVariant = MatrixMutedGreen,
    outline = MatrixBorderGreen,
    // Material3's HorizontalDivider() defaults its color to outlineVariant when no explicit
    // color is passed (e.g. the deck card's own divider) — left unset, it silently falls back
    // to M3's baseline dark-theme token (a gray), which would be the only non-black/non-green
    // color in the whole theme. Matched to the deck card's own border color per an explicit ask.
    outlineVariant = MatrixBorderGreen,
    error = Color(0xFFF87171),
    onError = Color.Black,
    errorContainer = Color(0xFF450A0A),
    onErrorContainer = Color(0xFFF87171),
)

// Every TextStyle monospaced, per the spec's "Font: Monospace everywhere" — this cascades to
// virtually all plain Text() calls app-wide via MaterialTheme's own ProvideTextStyle(bodyLarge),
// not just call sites that explicitly reference MaterialTheme.typography.
private val MatrixTypography = Typography().let { base ->
    Typography(
        displayLarge = base.displayLarge.copy(fontFamily = FontFamily.Monospace),
        displayMedium = base.displayMedium.copy(fontFamily = FontFamily.Monospace),
        displaySmall = base.displaySmall.copy(fontFamily = FontFamily.Monospace),
        headlineLarge = base.headlineLarge.copy(fontFamily = FontFamily.Monospace),
        headlineMedium = base.headlineMedium.copy(fontFamily = FontFamily.Monospace),
        headlineSmall = base.headlineSmall.copy(fontFamily = FontFamily.Monospace),
        titleLarge = base.titleLarge.copy(fontFamily = FontFamily.Monospace),
        titleMedium = base.titleMedium.copy(fontFamily = FontFamily.Monospace),
        titleSmall = base.titleSmall.copy(fontFamily = FontFamily.Monospace),
        bodyLarge = base.bodyLarge.copy(fontFamily = FontFamily.Monospace),
        bodyMedium = base.bodyMedium.copy(fontFamily = FontFamily.Monospace),
        bodySmall = base.bodySmall.copy(fontFamily = FontFamily.Monospace),
        labelLarge = base.labelLarge.copy(fontFamily = FontFamily.Monospace),
        labelMedium = base.labelMedium.copy(fontFamily = FontFamily.Monospace),
        labelSmall = base.labelSmall.copy(fontFamily = FontFamily.Monospace),
    )
}
private val DefaultTypography = Typography()

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
    AppTheme.MATRIX_TERMINAL -> MatrixColorScheme
    AppTheme.POP_ART -> PopArtColorScheme
    AppTheme.TEST -> TestColorScheme
    AppTheme.STUDIO_MINIMALIST -> LightColorScheme
}

private fun typographyFor(theme: AppTheme) = when (theme) {
    AppTheme.MATRIX_TERMINAL -> MatrixTypography
    else -> DefaultTypography
}

// Matrix Terminal's monospace font renders visibly larger than Sunset Arcade's default font at
// the same nominal sp size — this scales every sp-sized value (both Typography-driven text and
// the many ad hoc `fontSize = Xsp` literals scattered across screens, none of which reference
// Typography directly) down by a fixed proportion, without touching dp sizing.
private const val MatrixFontScale = 0.9f

@Composable
fun CharadesTheme(theme: AppTheme = AppTheme.DEFAULT, content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalExtendedColors provides extendedColorsFor(theme)) {
        MaterialTheme(
            colorScheme = colorSchemeFor(theme),
            typography = typographyFor(theme),
        ) {
            if (theme == AppTheme.MATRIX_TERMINAL) {
                val density = LocalDensity.current
                CompositionLocalProvider(
                    LocalDensity provides Density(density.density, density.fontScale * MatrixFontScale),
                    content = content,
                )
            } else {
                content()
            }
        }
    }
}
