package ir.the_code.guardify.features.main

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.toRoute
import ir.the_code.guardify.R
import ir.the_code.guardify.components.bottom_navigation.BottomNavigationBox
import ir.the_code.guardify.data.states.app_state.LocalAppState
import ir.the_code.guardify.data.states.app_state.appbar.AppbarState
import ir.the_code.guardify.features.login.LoginScreen
import ir.the_code.guardify.features.messages.MessagesScreen
import ir.the_code.guardify.features.messages_inbox.MessagesInboxScreen
import ir.the_code.guardify.features.notifications.NotificationsScreen
import ir.the_code.guardify.features.register.RegisterScreen
import ir.the_code.guardify.features.settings.SettingsScreen
import ir.the_code.guardify.features.validation_link.ValidationLinkScreen
import ir.the_code.guardify.features.web_browser.WebBrowserScreen
import ir.the_code.guardify.utils.AppPages
import ir.the_code.guardify.utils.navigateWithSaveState

@SuppressLint("UnrememberedMutableState")
@Composable
fun MainNavigation(
    currentPage: AppPages,
    isAuthenticated: Boolean,
    defaultVisibleBottomNavigation: Boolean = false,
    onRequestAuthenticate: () -> Unit
) {
    val appState = LocalAppState.current
    val navController = appState.navController
    var currentTopAppbarState by remember { mutableStateOf<AppbarState?>(null) }
    val currentDestination by navController.currentBackStackEntryAsState()
    BottomNavigationBox(
        appbarState = currentTopAppbarState,
        visible = currentDestination?.isVisibleBottomNavigation() ?: defaultVisibleBottomNavigation,
        isSelectedPage = { page ->
            currentDestination?.destination?.hierarchy?.any { it.hasRoute(page::class) } ?: false
        }, onClick = { navController.navigateWithSaveState(it) }) {
        NavHost(
            navController = navController,
            startDestination = currentPage,
            modifier = Modifier.fillMaxSize()
        ) {
            composable<AppPages.Messages> {
                MessagesScreen(isAuthenticated, onAppbarState = { currentTopAppbarState = it })
            }
            composable<AppPages.WebBrowser> {
                val link = remember { it.savedStateHandle.get<String>("initial") }
                if (link == null) {
                    SideEffect {
                        it.savedStateHandle.remove<String>("initial")
                    }
                }
                WebBrowserScreen(
                    onAppbarState = { currentTopAppbarState = it },
                    initialLink = link
                )
            }
            composable<AppPages.MessagesInbox> {
                MessagesInboxScreen(
                    onAppbarState = { currentTopAppbarState = it },
                    detail = it.toRoute(),
                    onRequestAuthenticate = onRequestAuthenticate,
                    isAuthenticated = isAuthenticated
                )
            }
            composable<AppPages.ValidationLink> {
                LaunchedEffect(Unit) {
                    currentTopAppbarState = AppbarState(
                        title = R.string.validate_link
                    )
                }
                ValidationLinkScreen(it.toRoute())
            }
            composable<AppPages.Register> {
                RegisterScreen()
            }
            composable<AppPages.Register> {
                RegisterScreen()
            }
            composable<AppPages.Login> {
                LoginScreen()
            }
            composable<AppPages.Settings> {
                SettingsScreen(
                    onAppbarState = { currentTopAppbarState = it }
                )
            }
            composable<AppPages.Notifications> {
                NotificationsScreen(
                    onAppbarState = { currentTopAppbarState = it }
                )
            }
        }
    }
}

fun NavBackStackEntry.isVisibleBottomNavigation(): Boolean {
    return AppPages.bottomNavigationPages.any { destination.hasRoute(it.page::class) } || listOf(
        AppPages.MessagesInbox::class, AppPages.ValidationLink::class
    ).any { destination.hasRoute(it) }
}