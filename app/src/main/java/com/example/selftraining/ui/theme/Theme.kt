package com.example.selftraining.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ダイエット・筋トレアプリらしいエネルギッシュな配色
private val PrimaryColor = Color(0xFF2E7D32)      // 深緑（健康・自然）
private val PrimaryContainer = Color(0xFFA5D6A7)
private val SecondaryColor = Color(0xFF1565C0)    // 青（爽快感）
private val TertiaryColor = Color(0xFFE65100)     // オレンジ（活力）

private val LightColorScheme = lightColorScheme(
    primary = PrimaryColor,
    primaryContainer = PrimaryContainer,
    secondary = SecondaryColor,
    tertiary = TertiaryColor,
    background = Color(0xFFF5F5F5),
    surface = Color(0xFFFFFFFF)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF81C784),
    primaryContainer = Color(0xFF1B5E20),
    secondary = Color(0xFF64B5F6),
    tertiary = Color(0xFFFF8A65),
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E)
)

/**
 * アプリのテーマ定義
 */
@Composable
fun SelfTrainingTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
