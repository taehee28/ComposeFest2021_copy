package com.codelab.theming.ui.start.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * 사용하고 싶은 색들을 Material Theme에서 요구하는 Color 객체로 만듬
 */
private val LightColors = lightColors(
    primary = Red700,
    primaryVariant = Red900,
    onPrimary = Color.White,
    secondary = Red700,
    secondaryVariant = Red900,
    onSecondary = Color.White,
    error = Red800
)

private val DarkColors = darkColors(
    primary = Red300,
    primaryVariant = Red700,
    onPrimary = Color.Black,
    secondary = Red300,
    onSecondary = Color.Black,
    error = Red200
)

/**
 * 테마 커스텀을 위해서 기본 Material Theme을 감싸고 설정하는 컴포저블
 */
@Composable
fun JetnewsTheme(isDarkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    MaterialTheme(
        colors = if (isDarkTheme) DarkColors else LightColors,   // 새로 만든 컬러 팔레트 적용
        typography = JetnewsTypography,     // 새로 정의한 타이포그래피 적용
        shapes = JetnewsShapes,     // 새로 정의한 모양 적용
        content = content
    )
}