package ir.the_code.guardify.utils

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import ir.the_code.guardify.R
import ir.the_code.guardify.ui.theme.DarkBlue
import ir.the_code.guardify.ui.theme.Orange
import kotlinx.serialization.Serializable


sealed class AppPages {
    @Serializable
    data object Messages : AppPages()

    @Serializable
    data object WebBrowser : AppPages()

    @Serializable
    data class MessagesInbox(
        val phoneNumber: String,
        val name: String,
    ) : AppPages()

    @Serializable
    data class ValidationLink(
        val url: String
    ) : AppPages()


    @Serializable
    data object Register : AppPages()

    @Serializable
    data object Login : AppPages()

    @Serializable
    data object Settings : AppPages()


    @Serializable
    data object Notifications : AppPages()

    companion object {
        val bottomNavigationPages: List<BottomNavigationItem>
            get() = listOf(
                BottomNavigationItem(
                    iconRes = R.drawable.ic_message_outline,
                    filledIcon = R.drawable.ic_message_filled,
                    titleId = R.string.messages,
                    page = Messages,
                ),
                BottomNavigationItem(
                    iconRes = R.drawable.ic_notification_outlined,
                    filledIcon = R.drawable.ic_notifications_filled,
                    titleId = R.string.notifications,
                    page = Notifications,
                ),
                BottomNavigationItem(
                    iconRes = R.drawable.ic_search_outlined,
                    filledIcon = R.drawable.ic_search_filled,
                    titleId = R.string.browser,
                    page = WebBrowser,
                ),
                BottomNavigationItem(
                    iconRes = R.drawable.ic_settings_outlined,
                    filledIcon = R.drawable.ic_settings_filled,
                    titleId = R.string.settings,
                    page = Settings,
                ),
            )
    }
}

@Immutable
data class BottomNavigationItem(
    @DrawableRes val iconRes: Int,
    @DrawableRes val filledIcon: Int,
    @StringRes val titleId: Int,
    val page: AppPages,
)