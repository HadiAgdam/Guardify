package ir.the_code.guardify.features.intro

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier

@Composable
fun IntroScreen(modifier: Modifier = Modifier) {

}

@Immutable
data class IntroItem(
    val text: String,
    val image: Int,
)