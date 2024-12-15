package ir.the_code.guardify.features.settings

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ir.the_code.guardify.R
import ir.the_code.guardify.data.states.app_state.LocalAppState
import ir.the_code.guardify.data.states.app_state.WithLocalAppState
import ir.the_code.guardify.data.states.app_state.appbar.AppbarState
import ir.the_code.guardify.features.main.MainNavigation
import ir.the_code.guardify.ui.theme.GuardifyTheme
import ir.the_code.guardify.utils.AppPages
import ir.the_code.guardify.utils.operators.getDefaultPaddingValue
import ir.the_code.guardify.viewmodels.settings.SettingsViewModel
import kotlinx.coroutines.delay
import org.koin.compose.viewmodel.koinViewModel
import soup.compose.material.motion.circularReveal

@Composable
fun SettingsScreen(
    onAppbarState: (AppbarState) -> Unit,
    viewModel: SettingsViewModel = koinViewModel()
) {
    var isChangingTheme by remember { mutableStateOf(false) }
    var changingThemOffset by remember { mutableStateOf(Offset.Zero) }
    val isSystemInDark = isSystemInDarkTheme()
    val isDarkTheme by viewModel.isDarkTheme.collectAsStateWithLifecycle(isSystemInDark)
    var requiredIsDarkTheme by remember(isDarkTheme, isSystemInDark) {
        mutableStateOf(
            isDarkTheme ?: isSystemInDark
        )
    }
    val density = LocalDensity.current
    val statusBarTop = WindowInsets.statusBars.getTop(density)
    LaunchedEffect(requiredIsDarkTheme) {
        onAppbarState.invoke(
            AppbarState(
                title = R.string.settings,
                actions = {
                    IconButton(onClick = {
                        if (isChangingTheme) return@IconButton
                        isChangingTheme = true
                    }, modifier = Modifier.onGloballyPositioned {
                        changingThemOffset =
                            it.boundsInWindow().center - Offset(0f, statusBarTop.toFloat())
                    }) {
                        Crossfade(requiredIsDarkTheme, label = "") { darkTheme ->
                            Icon(
                                painter = painterResource(if (darkTheme) R.drawable.sun_icon else R.drawable.moon_icon),
                                contentDescription = "change theme",
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            )
        )
    }
    Box {
        GuardifyTheme(requiredIsDarkTheme) {
            SettingsContent("Amirreza", false)
        }
        if (isChangingTheme) {
            var isVisible by remember {
                mutableStateOf(false)
            }
            val frontTheme = remember { requiredIsDarkTheme.not() }
            LaunchedEffect(Unit) {
                isVisible = true
                delay(1000)
                viewModel.changeTheme(requiredIsDarkTheme.not())
                delay(100)
                isChangingTheme = false
            }
            Popup(
                onDismissRequest = {},
                properties = PopupProperties(
                    usePlatformDefaultWidth = false,
                    clippingEnabled = true
                )
            ) {
                val window = LocalView.current.parent as? DialogWindowProvider
                SideEffect {
                    window?.let {
                        it.window.setDimAmount(0f)
                        it.window.setWindowAnimations(-1)
                        it.window.statusBarColor = Color.Transparent.toArgb()
                        it.window.navigationBarColor = Color.Transparent.toArgb()
                    }
                }
                Box(
                    Modifier
                        .circularReveal(
                            isVisible,
                            transitionSpec = { tween(1000) },
                            center = { changingThemOffset }
                        )
                        .fillMaxSize()
                ) {
                    WithLocalAppState {
                        GuardifyTheme(frontTheme) {
                            Surface(
                                modifier = Modifier.fillMaxSize(),
                                color = MaterialTheme.colorScheme.background,
                                contentColor = MaterialTheme.colorScheme.onBackground
                            ) {
                                MainNavigation(
                                    AppPages.Settings,
                                    isAuthenticated = false,
                                    onRequestAuthenticate = {}
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsContent(name: String, isPersian: Boolean) {
    LazyColumn(
        contentPadding = getDefaultPaddingValue(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item("change_language") {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            ) {
                ChangeLanguageSwitch(isPersian) { }
            }
        }
    }
}


@Composable
fun ChangeLanguageSwitch(value: Boolean, onChangeLanguage: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onChangeLanguage.invoke()
            }
            .padding(12.dp)
    ) {
        Text(
            stringResource(R.string.language),
            modifier = Modifier.weight(1f)
        )
        Text(if (value) "FA" else "EN", color = MaterialTheme.colorScheme.primary)
        Switch(
            value,
            null
        )
    }
}