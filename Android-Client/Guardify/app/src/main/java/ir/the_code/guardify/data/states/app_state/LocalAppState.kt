package ir.the_code.guardify.data.states.app_state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.CoroutineScope

val LocalAppState = staticCompositionLocalOf<AppState> { error("not exists") }

@Immutable
data class AppState(
    val navController: NavHostController,
    val scope: CoroutineScope
)

@Composable
fun rememberAppState(): AppState {
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()
    return remember {
        AppState(
            navController = navController,
            scope = scope
        )
    }
}


@Composable
fun WithLocalAppState(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalAppState provides rememberAppState(), content = content)
}