package ir.the_code.guardify.components.tabs

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ir.the_code.guardify.utils.modifier.clickableWithAnimateScale
import kotlin.math.roundToInt

@Composable
fun Tabs(
    items: List<Pair<String, Color>>,
    currentPage: Int,
    modifier: Modifier = Modifier,
    onClick: (page: Int) -> Unit
) {
    val eachWidth = 110.dp
    val eachSpace = 12.dp
    val scrollState = rememberScrollState()
    val density = LocalDensity.current
    LaunchedEffect(currentPage) {
        scrollState.animateScrollTo(
            with(density) { ((eachWidth * currentPage) + (eachSpace * currentPage)).toPx() }.roundToInt()
        )
    }
    Box(
        modifier
            .fillMaxWidth()
            .height(40.dp)
    ) {
        val currentOffset by animateDpAsState(
            eachWidth * currentPage + eachSpace * currentPage,
            label = ""
        )
        val currentColor by animateColorAsState(
            items[currentPage].second.copy(.3f),
            label = ""
        )
        Box(
            Modifier
                .offset(x = currentOffset - with(LocalDensity.current) { scrollState.value.toDp() })
                .clip(MaterialTheme.shapes.medium)
                .width(eachWidth)
                .background(currentColor)
                .fillMaxHeight()
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(eachSpace),
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState)
        ) {
            items.forEachIndexed { index, item ->
                Box(
                    Modifier
                        .width(eachWidth)
                        .fillMaxHeight()
                        .clickableWithAnimateScale { onClick.invoke(index) },
                    contentAlignment = Alignment.Center
                ) {
                    val color by animateColorAsState(
                        if (currentPage == index) items[index].second else LocalContentColor.current,
                        label = ""
                    )
                    Text(
                        item.first,
                        modifier = Modifier.padding(horizontal = 8.dp),
                        textAlign = TextAlign.Center,
                        color = color
                    )
                }
            }
        }
    }
}