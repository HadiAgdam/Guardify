package ir.the_code.guardify.data.states.app_state.appbar

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable

@Immutable
data class AppbarState(
    val title: Int,
    val navigationIcon: @Composable () -> Unit = {},
    val actions: @Composable RowScope.() -> Unit = {},
    val aboveAppbarContent: (@Composable () -> Unit)? = null,
    val appbarContent: (@Composable () -> Unit)? = null,
    val navigationBarContent: (@Composable () -> Unit)? = null,
)