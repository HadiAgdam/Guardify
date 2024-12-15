package ir.the_code.guardify.utils

import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController

fun NavHostController.navigateWithSaveState(page: Any) {
    navigate(page) {
        getPopupPage()?.let {
            popUpTo(it) {
                inclusive = true
                saveState = true
            }
        }
        launchSingleTop = true
        restoreState = true
    }
}


fun NavHostController.getPopupPage() = currentDestination?.hierarchy?.let { hierarchy ->
    AppPages.bottomNavigationPages.firstOrNull { info -> hierarchy.any { it.hasRoute(info.page::class) } }?.page
}