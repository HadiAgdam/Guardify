package ir.the_code.guardify.utils.operators

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import ir.the_code.guardify.components.bottom_navigation.bottomNavigationHeight

@Composable
operator fun PaddingValues.plus(
    other: PaddingValues,
) = LocalLayoutDirection.current.let { layoutDirection ->
    PaddingValues(
        calculateStartPadding(layoutDirection) + other.calculateStartPadding(layoutDirection),
        calculateTopPadding() + other.calculateTopPadding(),
        calculateEndPadding(layoutDirection) + other.calculateEndPadding(layoutDirection),
        calculateBottomPadding() + other.calculateBottomPadding()
    )
}


@Composable
fun getDefaultPaddingValuesWithBottomNavigation() =
    PaddingValues(horizontal = 16.dp) + WindowInsets.systemBars.asPaddingValues() + PaddingValues(
        top = 16.dp,
        bottom = 32.dp + bottomNavigationHeight
    )

@Composable
fun getDefaultPaddingValue() =
    PaddingValues(horizontal = 12.dp) + PaddingValues(
        vertical = 16.dp,
    )

@Composable
fun getDefaultPaddingValueWithTopAppbar() = getDefaultPaddingValue() + PaddingValues(top = 64.dp)

@Composable
fun getDefaultPaddingValueWithTopAppbarAndBottomNavigation() =
    getDefaultPaddingValue() + PaddingValues(top = 64.dp) + PaddingValues(
        bottom = 32.dp + bottomNavigationHeight
    )

@Composable
fun getDefaultPaddingValueWithTopAppbarAndBottomNavigationVertical() =
    WindowInsets.systemBars.asPaddingValues() + PaddingValues(
        vertical = 16.dp,
    ) + PaddingValues(top = 64.dp) + PaddingValues(
        bottom = 32.dp + bottomNavigationHeight
    )