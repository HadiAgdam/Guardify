package ir.the_code.guardify.features.messages

import android.Manifest
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import ir.the_code.guardify.utils.operators.getDefaultPaddingValue
import ir.the_code.guardify.R
import ir.the_code.guardify.components.tabs.Tabs
import ir.the_code.guardify.data.MessageReason
import ir.the_code.guardify.data.models.MessageInfo
import ir.the_code.guardify.data.models.getHiddenMessage
import ir.the_code.guardify.data.models.hasPin
import ir.the_code.guardify.data.states.app_state.LocalAppState
import ir.the_code.guardify.data.states.app_state.appbar.AppbarState
import ir.the_code.guardify.ui.theme.Red
import ir.the_code.guardify.ui.theme.Yellow
import ir.the_code.guardify.utils.AppPages
import ir.the_code.guardify.utils.formatters.date.formatDateTime
import ir.the_code.guardify.utils.hasPermissions
import ir.the_code.guardify.utils.modifier.clickableWithAnimateScale
import ir.the_code.guardify.viewmodels.messages.MessagesViewModel
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun MessagesScreen(
    isAuthenticated: Boolean,
    onAppbarState: (AppbarState) -> Unit,
    viewModel: MessagesViewModel = koinViewModel()
) {
    val appState = LocalAppState.current
    val navController = appState.navController
    val messages by viewModel.messages.collectAsStateWithLifecycle()
    val blockedUsersReason by viewModel.blockedUsersReason.collectAsStateWithLifecycle()
    val isLoading by viewModel.loading.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var hasPermission by remember {
        mutableStateOf(
            context.hasPermissions(
                viewModel.getPermissionsList()
            )
        )
    }
    LaunchedEffect(Unit) {
        viewModel.fetchAllBlockedUsersFromApi()
    }
    val pagerState = rememberPagerState { MessageReason.entries.size + 1 }
    val onBackgroundColor = MaterialTheme.colorScheme.onBackground
    val tabs = remember {
//        listOf(
//            R.string.all to onBackgroundColor,
//            R.string.phishing to Red,
//            R.string.advertisement to Yellow,
//        ).map { context.getString(it.first) to it.second }

        listOf(context.getString(R.string.all) to onBackgroundColor) +
                MessageReason.entries.map { it.name.lowercase().capitalize() to it.color }
    }
    val scope = appState.scope
    LaunchedEffect(Unit) {
        onAppbarState.invoke(
            AppbarState(
                title = R.string.messages,
                aboveAppbarContent = {
                    Tabs(
                        tabs,
                        currentPage = pagerState.currentPage,
                        onClick = {
                            scope.launch { pagerState.animateScrollToPage(it) }
                        },
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .padding(bottom = 12.dp)
                    )
                }
            )
        )
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        Log.d("dsfsddsf", "MessagesScreen: ${it.values}")
        if (it.values.all { permission -> permission }) {
            viewModel.fetchAllMessages()
            hasPermission = true
        }
    }
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (!hasPermission) {
            Button(onClick = {
                permissionLauncher.launch(
                    viewModel.getPermissionsList().toTypedArray()
                )
            }) {
                Text(stringResource(R.string.grant_permissions))
            }
        } else {
            HorizontalPager(pagerState) {
                val tabName = if (it == 0) "ALL" else MessageReason.entries[it - 1].name
                val filteredPhones = remember(blockedUsersReason, messages) {
                    when (tabName) {
                        "ALL" -> messages
                        "SAFE" -> {
                            messages.filter { info ->
                                val reasons = blockedUsersReason[info.phoneNumber]
                                reasons == null
                            }
                        }

                        else -> messages.filter { info ->
                            val reasons = blockedUsersReason[info.phoneNumber]
                            reasons != null && reasons.any { reasonName -> reasonName == tabName }
                        }
                    }
                }
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentPadding = getDefaultPaddingValue(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredPhones, key = { message -> message.phoneNumber }) { message ->
                            val reasons = remember(blockedUsersReason, message) {
                                blockedUsersReason[message.phoneNumber]?.mapNotNull { item -> MessageReason.entries.firstOrNull { item == it.name } }
                                    ?: emptyList()
                            }
                            MessagesItem(
                                message,
                                isAuthenticated,
                                blockMobileReasons = reasons,
                                onClick = {
                                    navController.navigate(
                                        AppPages.MessagesInbox(
                                            message.phoneNumber,
                                            message.phoneNumber,
                                        )
                                    )
                                })
                        }
                    }
                    if (filteredPhones.isEmpty() && isLoading.not()) {
                        Text(
                            stringResource(R.string.everything_looks_fine)
                        )
                    }
                }
            }
            if (isLoading) {
                CircularProgressIndicator(
                    strokeCap = StrokeCap.Round
                )
            }
        }
    }
}


@Composable
fun MessagesItem(
    message: MessageInfo,
    isAuthenticated: Boolean,
    blockMobileReasons: List<MessageReason>,
    onClick: () -> Unit
) {
    val hasPin =
        remember(message, isAuthenticated) { message.hasPin() && isAuthenticated.not() }
    val lastMessage =
        remember(hasPin) {
            (if (hasPin) message.getHiddenMessage() else message.lastMessage).replace(
                "\n",
                " "
            )
        }
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.large,
        modifier = Modifier.clickableWithAnimateScale { onClick.invoke() }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (message.photoUri != null) {
                AsyncImage(
                    model = message.photoUri,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .size(48.dp),
                    contentScale = ContentScale.Crop,
                    contentDescription = message.phoneNumber
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        message.phoneNumber,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        remember(message) { message.messageDate.formatDateTime() },
                        modifier = Modifier.alpha(.7f)
                    )
                }
                Text(
                    lastMessage,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .fillMaxWidth()
                )
                if (blockMobileReasons.isNotEmpty()) {
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(blockMobileReasons) { reason ->
                            Box(
                                Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(reason.color)
                            ) {
                                Text(
                                    remember(reason) { reason.name.lowercase().capitalize() },
                                    color = MaterialTheme.colorScheme.surface,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}