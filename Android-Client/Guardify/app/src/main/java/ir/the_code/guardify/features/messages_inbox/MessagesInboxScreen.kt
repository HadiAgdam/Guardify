package ir.the_code.guardify.features.messages_inbox

import android.telephony.SmsManager
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowLeft
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ir.the_code.guardify.utils.operators.getDefaultPaddingValue
import ir.the_code.guardify.R
import ir.the_code.guardify.data.models.SmsMessage
import ir.the_code.guardify.data.models.hasPin
import ir.the_code.guardify.data.states.app_state.LocalAppState
import ir.the_code.guardify.data.states.app_state.appbar.AppbarState
import ir.the_code.guardify.utils.AppPages
import ir.the_code.guardify.utils.formatters.date.formatMonthAndYear
import ir.the_code.guardify.utils.formatters.date.formatTime
import ir.the_code.guardify.utils.modifier.clickableWithAnimateScale
import ir.the_code.guardify.viewmodels.messages_inbox.MessagesInboxViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun MessagesInboxScreen(
    onAppbarState: (AppbarState) -> Unit,
    detail: AppPages.MessagesInbox,
    isAuthenticated: Boolean,
    onRequestAuthenticate: () -> Unit,
    viewModel: MessagesInboxViewModel = koinViewModel(
        parameters = { parametersOf(detail.phoneNumber) }
    ),
) {
    val isLoading by viewModel.loading.collectAsStateWithLifecycle()
    val appState = LocalAppState.current
    val navController = appState.navController
    val messages by viewModel.messages.collectAsStateWithLifecycle()
    val lazyState = rememberLazyListState()
    val context = LocalContext.current
    val smsManager = remember {
        context.getSystemService(SmsManager::class.java)
    }
    LaunchedEffect(messages) {
        if (messages.isNotEmpty()) {
            lazyState.animateScrollToItem(lazyState.layoutInfo.totalItemsCount)
        }
    }
    LaunchedEffect(Unit) {
        onAppbarState.invoke(
            AppbarState(
                title = R.string.messages,
                navigationBarContent = if (smsManager != null) ({
                    var currentMessage by remember { mutableStateOf("") }
                    Row(
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BasicTextField(
                            value = currentMessage,
                            onValueChange = { currentMessage = it },
                            modifier = Modifier
                                .weight(1f),
                            keyboardActions = KeyboardActions(onSend = {
                                viewModel.sendMessage(smsManager, currentMessage) {
                                    Toast
                                        .makeText(context, it, Toast.LENGTH_SHORT)
                                        .show()
                                }
                                currentMessage = ""
                            }),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                            maxLines = 1,
                            singleLine = true,
                            decorationBox = { content ->
                                Row(
                                    Modifier
                                        .clip(MaterialTheme.shapes.medium)
                                        .background(MaterialTheme.colorScheme.background)
                                        .height(48.dp)
                                        .padding(horizontal = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box(Modifier.weight(1f)) {
                                        content()
                                        if (currentMessage.isEmpty()) {
                                            Text(
                                                stringResource(R.string.send_message),
                                                modifier = Modifier.alpha(.7f)
                                            )
                                        }
                                    }
                                }
                            },
                            textStyle = LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.onBackground)
                        )
                        Box(
                            Modifier
                                .clip(CircleShape)
                                .size(48.dp)
                                .background(MaterialTheme.colorScheme.primary)
                                .clickableWithAnimateScale {
                                    viewModel.sendMessage(smsManager, currentMessage) {
                                        Toast
                                            .makeText(context, it, Toast.LENGTH_SHORT)
                                            .show()
                                    }
                                    currentMessage = ""
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_send),
                                contentDescription = "send",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }) else null,
                appbarContent = {
                    CenterAlignedTopAppBar(
                        title = {
                            Text(
                                detail.name
                            )
                        },
                        navigationIcon = {
                            IconButton(navController::navigateUp) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowLeft,
                                    contentDescription = "back"
                                )
                            }
                        },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(Color.Transparent)
                    )
                }
            )
        )
    }
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        val groupedByMonth = remember(messages) {
            messages.groupBy { it.messageDate.formatMonthAndYear() }
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentPadding = getDefaultPaddingValue(),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally,
            state = lazyState,
        ) {
            groupedByMonth.forEach { (date, groupMessages) ->
                stickyHeader {
                    Box(
                        Modifier
                            .padding(top = 8.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(.6f))
                            .padding(4.dp)
                            .padding(horizontal = 4.dp)
                    ) {
                        Text(date, color = Color.White, fontSize = 12.sp)
                    }
                }
                items(groupMessages, key = { it.id }) { message ->
                    MessageItem(message, isAuthenticated, onRequestAuthenticate)
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

@Composable
private fun MessageItem(
    message: SmsMessage,
    isAuthenticated: Boolean,
    onRequestAuthenticate: () -> Unit
) {
    val shape = if (message.sentByMe) {
        MaterialTheme.shapes.medium.copy(bottomEnd = CornerSize(0f))
    } else MaterialTheme.shapes.medium.copy(bottomStart = CornerSize(0f))
    val alignment = if (message.sentByMe) Alignment.CenterEnd else Alignment.CenterStart
    val backgroundColor =
        if (message.sentByMe) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
    val contentColor =
        if (message.sentByMe) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
    val hasPin = remember(message, isAuthenticated) { message.hasPin() && isAuthenticated.not() }
    BoxWithConstraints(
        Modifier
            .fillMaxWidth()
            .padding(top = 12.dp), contentAlignment = alignment
    ) {
        Surface(
            shape = shape,
            color = backgroundColor,
            modifier = Modifier.widthIn(max = maxWidth * .7f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Column(
                    modifier = Modifier
                        .then(if (hasPin) Modifier.blur(16.dp) else Modifier)
                        .padding(12.dp)
                ) {
                    if (hasPin) {
                        Text(
                            message.messageBody,
                            style = LocalTextStyle.current.copy(
                                textDirection = TextDirection.ContentOrRtl,
                                color = contentColor
                            ),
                        )
                    } else {
                        SelectionContainer {
                            Text(
                                message.messageBody,
                                style = LocalTextStyle.current.copy(
                                    textDirection = TextDirection.ContentOrRtl,
                                    color = contentColor
                                ),
                            )
                        }
                    }
                    Text(
                        message.messageDate.formatTime(),
                        modifier = Modifier.alpha(.7f),
                        color = contentColor
                    )
                }
                if (hasPin) {
                    IconButton(onRequestAuthenticate) {
                        Icon(
                            painter = painterResource(R.drawable.ic_eye_slash),
                            contentDescription = null,
                        )
                    }
                }
            }
        }
    }
}