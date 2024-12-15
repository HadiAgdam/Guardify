package ir.the_code.guardify.features.notifications

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import ir.the_code.guardify.R
import ir.the_code.guardify.components.tabs.Tabs
import ir.the_code.guardify.data.MessageReason
import ir.the_code.guardify.data.NotificationReason
import ir.the_code.guardify.data.models.MessageInfo
import ir.the_code.guardify.data.models.getHiddenMessage
import ir.the_code.guardify.data.models.hasPin
import ir.the_code.guardify.data.models.notification.Notification
import ir.the_code.guardify.data.network.response.onError
import ir.the_code.guardify.data.network.response.onLoading
import ir.the_code.guardify.data.network.response.onSuccess
import ir.the_code.guardify.data.states.app_state.LocalAppState
import ir.the_code.guardify.data.states.app_state.appbar.AppbarState
import ir.the_code.guardify.features.messages.MessagesItem
import ir.the_code.guardify.utils.AppPages
import ir.the_code.guardify.utils.formatters.date.formatDateTime
import ir.the_code.guardify.utils.modifier.clickableWithAnimateScale
import ir.the_code.guardify.utils.operators.getDefaultPaddingValue
import ir.the_code.guardify.viewmodels.notifications.MyNotificationsViewModel
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import java.time.Instant

@Composable
fun NotificationsScreen(
    onAppbarState: (AppbarState) -> Unit, viewModel: MyNotificationsViewModel = koinViewModel()
) {
    val appState = LocalAppState.current
    val context = LocalContext.current
    val scope = appState.scope
    val onBackgroundColor = MaterialTheme.colorScheme.onBackground
    val tabs = remember {
        listOf(context.getString(R.string.all) to onBackgroundColor) + NotificationReason.entries.map {
            it.name.lowercase().capitalize() to it.color
        }
    }
    val pagerState = rememberPagerState { tabs.size }
    val response by viewModel.response.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        onAppbarState.invoke(
            AppbarState(title = R.string.unsafe_notifications,
                aboveAppbarContent = {
                    Tabs(
                        tabs, currentPage = pagerState.currentPage, onClick = {
                            scope.launch { pagerState.animateScrollToPage(it) }
                        }, modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .padding(bottom = 12.dp)
                    )
                })
        )
    }
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (!context.isNotificationServiceEnabled()) {
            Button(onClick = {
                val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
                context.startActivity(intent)
            }) {
                Text(stringResource(R.string.grant_permissions))
            }
        } else {
            response.onSuccess { notifications ->
                HorizontalPager(pagerState) {
                    val filteredNotifications = remember(notifications) {
                        when (it) {
                            0 -> notifications
                            else -> notifications.filter { info -> info.status == NotificationReason.entries[it - 1].name }
                        }
                    }
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = getDefaultPaddingValue(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(filteredNotifications,
                                key = { notification -> notification.id }) { notification ->
                                NotificationItem(notification)
                            }
                        }
                        if (notifications.isEmpty()) {
                            Text(
                                stringResource(R.string.everything_looks_fine)
                            )
                        }
                    }
                }
            }.onLoading {
                CircularProgressIndicator(strokeCap = StrokeCap.Round)
            }.onError {
                Button(onClick = viewModel::fetchMyNotifications) {
                    Text(stringResource(R.string.try_again))
                }
            }
        }
    }
}


fun Context.isNotificationServiceEnabled(): Boolean {
    val enabledListeners =
        Settings.Secure.getString(contentResolver, "enabled_notification_listeners")
    return enabledListeners != null && enabledListeners.contains(packageName)
}


@SuppressLint("NewApi")
@Composable
fun NotificationItem(
    notification: Notification,
) {
    Log.d("dfsdfdfsd", "NotificationItem: ${notification.status}")
    val reason = remember(notification) {
        NotificationReason.entries.firstOrNull { it.name == notification.status }
            ?: NotificationReason.entries.first()
    }
    val text = remember {
        notification.text.replace(
            "\n", " "
        )
    }
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.large,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(12.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    notification.numberFrom, modifier = Modifier.weight(1f)
                )
                Text(
                    remember(notification) {
                        Instant.parse(notification.createdAt).toEpochMilli().formatDateTime()
                    }, modifier = Modifier.alpha(.7f)
                )
            }
            Text(
                text,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )
            Box(
                Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(reason.color)
            ) {
                Text(
                    remember { reason.name.lowercase().capitalize() },
                    color = MaterialTheme.colorScheme.surface,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                )
            }
        }
    }
}