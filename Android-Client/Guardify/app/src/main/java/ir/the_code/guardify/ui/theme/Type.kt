package ir.the_code.guardify.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.sp
import ir.the_code.guardify.R

val SchoolFontFamily = FontFamily(
    Font(R.font.vazir_regular),
    Font(R.font.vazir_bold, weight = FontWeight.Bold),
    Font(R.font.vazir_black, weight = FontWeight.Black),
    Font(R.font.vazir_medium, weight = FontWeight.Medium),
)

// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
).copyFont(SchoolFontFamily)


fun Typography.copyFont(
    fontFamily: FontFamily,
    textDirection: TextDirection = TextDirection.ContentOrLtr
) = copy(
    displayLarge.copy(fontFamily = fontFamily, textDirection = textDirection),
    displayMedium.copy(fontFamily = fontFamily, textDirection = textDirection),
    displaySmall.copy(fontFamily = fontFamily, textDirection = textDirection),
    headlineLarge.copy(fontFamily = fontFamily, textDirection = textDirection),
    headlineMedium.copy(fontFamily = fontFamily, textDirection = textDirection),
    headlineSmall.copy(fontFamily = fontFamily, textDirection = textDirection),
    titleLarge.copy(fontFamily = fontFamily, textDirection = textDirection),
    titleMedium.copy(fontFamily = fontFamily, textDirection = textDirection),
    titleSmall.copy(fontFamily = fontFamily, textDirection = textDirection),
    bodyLarge.copy(fontFamily = fontFamily, textDirection = textDirection),
    bodyMedium.copy(fontFamily = fontFamily, textDirection = textDirection),
    bodySmall.copy(fontFamily = fontFamily, textDirection = textDirection),
    labelLarge.copy(fontFamily = fontFamily, textDirection = textDirection),
    labelMedium.copy(fontFamily = fontFamily, textDirection = textDirection),
    labelSmall.copy(fontFamily = fontFamily, textDirection = textDirection)
)