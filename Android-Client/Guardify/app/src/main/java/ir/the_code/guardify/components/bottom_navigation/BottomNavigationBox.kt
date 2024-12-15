package ir.the_code.guardify.components.bottom_navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild
import dev.chrisbanes.haze.materials.CupertinoMaterials
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import ir.the_code.guardify.R
import ir.the_code.guardify.data.states.app_state.appbar.AppbarState
import ir.the_code.guardify.utils.AppPages
import ir.the_code.guardify.utils.modifier.clickableWithAnimateScale

val bottomNavigationHeight = 80.dp

@OptIn(ExperimentalHazeMaterialsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun BottomNavigationBox(
    appbarState: AppbarState? = null,
    visible: Boolean,
    onClick: (AppPages) -> Unit,
    isSelectedPage: (Any) -> Boolean,
    content: @Composable () -> Unit
) {
    val pages = remember { AppPages.bottomNavigationPages }
    val horizontalPadding by animateDpAsState(if (visible) 8.dp else 0.dp, label = "")
    val corners by animateDpAsState(if (visible) 24.dp else 0.dp, label = "")
    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        val primaryAlpha by animateFloatAsState(
            if (isSelectedPage(AppPages.WebBrowser) || visible.not()) 0f else if (isSystemInDarkTheme()) .8f else .5f,
            label = ""
        )
        AnimatedVisibility(visible) {
            if (appbarState != null) {
                AnimatedContent(appbarState.appbarContent, label = "") { appbarContent ->
                    if (appbarContent == null) {
                        Column {
                            CenterAlignedTopAppBar(
                                title = {
                                    Text(stringResource(appbarState.title))
                                },
                                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(Color.Transparent),
                                navigationIcon = appbarState.navigationIcon,
                                actions = appbarState.actions
                            )
                            Column(
                                Modifier
                                    .fillMaxWidth()
                                    .animateContentSize()
                            ) {
                                if (appbarState.aboveAppbarContent != null) {
                                    Spacer(Modifier.height(8.dp))
                                    appbarState.aboveAppbarContent.invoke()
                                }
                            }
                        }
                    } else {
                        appbarContent.invoke()
                    }
                }
            }
        }
        Box(
            modifier = Modifier.weight(1f)
        ) {
            val primaryColor = MaterialTheme.colorScheme.primary.copy(primaryAlpha)
            val brush = Brush.verticalGradient(
                listOf(
                    primaryColor,
                    Color.Transparent,
                    Color.Transparent,
                    Color.Transparent,
                    Color.Transparent,
                    Color.Transparent,
                    Color.Transparent,
                    primaryColor,
                )
            )
            Canvas(
                Modifier
                    .fillMaxHeight()
                    .padding(horizontal = horizontalPadding)
                    .fillMaxWidth()
                    .blur(8.dp, edgeTreatment = BlurredEdgeTreatment.Unbounded)
            ) {
                drawRoundRect(
                    brush, cornerRadius = CornerRadius(corners.toPx())
                )
            }
            Box(
                Modifier
                    .padding(horizontal = horizontalPadding)
                    .border(
                        1.dp, brush, RoundedCornerShape(corners)
                    )
                    .clip(RoundedCornerShape(corners))
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                content.invoke()
            }
        }
        AnimatedVisibility(
            visible = visible,
            enter = expandVertically(expandFrom = Alignment.Top),
            exit = shrinkVertically(shrinkTowards = Alignment.Top)
        ) {
            AnimatedContent(appbarState?.navigationBarContent, label = "") { navigationBarContent ->
                Box(
                    Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .height(bottomNavigationHeight)
                        .padding(horizontalPadding)
                ) {
                    if (navigationBarContent != null) {
                        navigationBarContent()
                    } else {
                        Row(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            pages.forEach { page ->
                                val isSelected = isSelectedPage(page.page)
                                val transition = updateTransition(isSelected, label = "")
                                val scale by transition.animateFloat(label = "") {
                                    if (it) 1f else .8f
                                }
                                val alpha by transition.animateFloat(label = "") {
                                    if (it) 1f else .7f
                                }
                                Box(
                                    Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .clickableWithAnimateScale { onClick.invoke(page.page) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Crossfade(
                                        isSelected,
                                        label = "",
                                        modifier = Modifier
                                            .scale(scale)
                                            .alpha(alpha)
                                    ) {
                                        Icon(
                                            painter = painterResource(if (it) page.filledIcon else page.iconRes),
                                            contentDescription = null,
                                            modifier = Modifier.size(28.dp),
                                            tint = MaterialTheme.colorScheme.onBackground
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}