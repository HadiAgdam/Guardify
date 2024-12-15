package ir.the_code.guardify.utils.modifier

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale


@Composable
fun Modifier.clickableWithAnimateScale(onClick: () -> Unit): Modifier {
    val ins = remember { MutableInteractionSource() }
    val isPressed by ins.collectIsPressedAsState()
    val scale by animateFloatAsState(
        if (isPressed) .95f else 1f, animationSpec = tween(100),
        label = ""
    )
    return scale(scale).clickable(ins, null, onClick = onClick)
}