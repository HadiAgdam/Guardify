package ir.the_code.guardify.features.web_browser

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.view.ViewCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.web.WebContent
import com.google.accompanist.web.WebView
import com.google.accompanist.web.WebViewNavigator
import com.google.accompanist.web.WebViewState
import com.google.accompanist.web.rememberWebViewState
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild
import ir.the_code.guardify.R
import ir.the_code.guardify.data.network.response.getSuitableMessage
import ir.the_code.guardify.data.network.response.onError
import ir.the_code.guardify.data.network.response.onLoading
import ir.the_code.guardify.data.network.response.onSuccess
import ir.the_code.guardify.data.states.app_state.appbar.AppbarState
import ir.the_code.guardify.viewmodels.web_browser.WebBrowserViewModel
import kotlinx.coroutines.flow.collectIndexed
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun WebBrowserScreen(
    initialLink: String? = null,
    onAppbarState: (AppbarState) -> Unit,
    viewModel: WebBrowserViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val response by viewModel.response.collectAsStateWithLifecycle()
    val state = remember(initialLink) {
        WebViewState(
            WebContent.Url(
                url = initialLink ?: viewModel.currentUrl,
            )
        )
    }
    LaunchedEffect(Unit) {
        onAppbarState.invoke(
            AppbarState(
                title = R.string.messages,
                appbarContent = {
                    WebBrowserToolbar(
                        favicon = viewModel.favicon,
                        url = viewModel.currentUrl,
                        navigator = viewModel.navigator,
                        onSearch = viewModel::loadUrl,
                        onUrlChanged = {
                            viewModel.currentUrl = it
                        },
                        state = state
                    )
                }
            )
        )
    }
    Surface(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            AnimatedVisibility(viewModel.progress != 1f) {
                LinearProgressIndicator(
                    { viewModel.progress },
                    strokeCap = StrokeCap.Round,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            WebView(state = state,
                navigator = viewModel.navigator,
                client = viewModel.client,
                chromeClient = viewModel.chromeClient,
                modifier = Modifier
                    .clipToBounds()
                    .fillMaxSize(),
                onCreated = {
                    it.settings.apply {
                        allowFileAccess = false
                        allowContentAccess = false
                        javaScriptEnabled = true
                        domStorageEnabled = true
                    }
                })
        }
    }
    response.onError {
        SideEffect {
            Toast.makeText(context, it.getSuitableMessage(context), Toast.LENGTH_SHORT).show()
        }
    }.onLoading {
        Dialog(
            onDismissRequest = {},
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(.4f)
                    .aspectRatio(1f)
            ) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CircularProgressIndicator(strokeCap = StrokeCap.Round)
                        Text(stringResource(R.string.validating))
                    }
                }
            }
        }
    }.onSuccess {
        SideEffect {
            if (it.isBlock) {
                Toast.makeText(
                    context,
                    context.getString(R.string.link_is_not_valid),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    context,
                    context.getString(R.string.link_is_valid),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun WebBrowserToolbar(
    url: String,
    state: WebViewState,
    favicon: ImageBitmap? = null,
    navigator: WebViewNavigator,
    onUrlChanged: (String) -> Unit,
    onSearch: (String) -> Unit,
) {
    val sharedTransitionKey = remember { "text_field" }
    var isFocused by remember { mutableStateOf(false) }
    SharedTransitionLayout {
        AnimatedContent(
            isFocused, label = "", modifier = Modifier
                .padding(horizontal = 16.dp)
        ) { focused ->
            if (focused) {
                val ins = remember { MutableInteractionSource() }
                val focusRequester = remember { FocusRequester() }
                LaunchedEffect(Unit) {
                    focusRequester.requestFocus()
                    ins.interactions.collectIndexed { index, interaction ->
                        if (interaction is FocusInteraction.Unfocus && index != 0) {
                            kotlin.runCatching {
                                onUrlChanged.invoke(
                                    (state.content as? WebContent.Url)?.url ?: return@collectIndexed
                                )
                            }
                            isFocused = false
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .statusBarsPadding(),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(onClick = {
                            navigator.navigateBack()
                        }, enabled = navigator.canGoBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowLeft,
                                contentDescription = null
                            )
                        }
                        IconButton(onClick = {
                            navigator.navigateForward()
                        }, enabled = navigator.canGoForward) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                                contentDescription = null
                            )
                        }
                        Box(Modifier.weight(1f))
                        IconButton(onClick = {
                            onSearch(url)
                        }) {
                            Icon(
                                painter = painterResource(R.drawable.ic_refresh),
                                contentDescription = "search"
                            )
                        }
                    }

                    BasicTextField(
                        value = url,
                        onValueChange = onUrlChanged,
                        modifier = Modifier
                            .sharedBounds(
                                rememberSharedContentState(sharedTransitionKey),
                                animatedVisibilityScope = this@AnimatedContent,
                                resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds
                            )
                            .renderInSharedTransitionScopeOverlay()
                            .fillMaxWidth()
                            .focusRequester(focusRequester),
                        keyboardActions = KeyboardActions(onSearch = {
                            onSearch(url)
                            isFocused = false
                        }),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
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
                                favicon?.let { icon ->
                                    Crossfade(icon, label = "icon") { newIcon ->
                                        Image(
                                            bitmap = newIcon,
                                            contentDescription = null,
                                            modifier = Modifier
                                                .clip(CircleShape)
                                                .size(24.dp)
                                        )
                                    }
                                }
                                Box(Modifier.weight(1f)) {
                                    content()
                                }
                                IconButton(onClick = { onUrlChanged("") }) {
                                    Icon(
                                        imageVector = Icons.Rounded.Close,
                                        contentDescription = "clear"
                                    )
                                }
                            }
                        },
                        interactionSource = ins,
                        textStyle = LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.onBackground)
                    )
                }
            } else {
                val ins = remember { MutableInteractionSource() }
                val focusedIns by ins.collectIsFocusedAsState()
                LaunchedEffect(focusedIns) {
                    if (focusedIns) {
                        isFocused = true
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .statusBarsPadding(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(onClick = {
                        navigator.navigateBack()
                    }, enabled = navigator.canGoBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowLeft,
                            contentDescription = null
                        )
                    }
                    IconButton(onClick = {
                        navigator.navigateForward()
                    }, enabled = navigator.canGoForward) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                            contentDescription = null
                        )
                    }
                    BasicTextField(
                        value = url,
                        onValueChange = onUrlChanged,
                        modifier = Modifier
                            .sharedBounds(
                                rememberSharedContentState(sharedTransitionKey),
                                animatedVisibilityScope = this@AnimatedContent,
                                resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds
                            )
                            .weight(1f)
                            .renderInSharedTransitionScopeOverlay(),
                        keyboardActions = KeyboardActions(onSearch = { onSearch(url) }),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
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
                                favicon?.let { icon ->
                                    Crossfade(icon, label = "icon") { newIcon ->
                                        Image(
                                            bitmap = newIcon,
                                            contentDescription = null,
                                            modifier = Modifier
                                                .clip(CircleShape)
                                                .size(24.dp)
                                        )
                                    }
                                }
                                content()
                            }
                        },
                        interactionSource = ins,
                        textStyle = LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.onBackground)
                    )
                    IconButton(onClick = {
                        onSearch(url)
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.ic_refresh),
                            contentDescription = "search"
                        )
                    }
                }
            }
        }
    }
}