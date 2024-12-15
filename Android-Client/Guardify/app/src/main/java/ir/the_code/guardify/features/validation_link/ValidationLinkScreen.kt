package ir.the_code.guardify.features.validation_link

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import ir.the_code.guardify.R
import ir.the_code.guardify.data.network.response.getSuitableMessage
import ir.the_code.guardify.data.network.response.onError
import ir.the_code.guardify.data.network.response.onLoading
import ir.the_code.guardify.data.network.response.onSuccess
import ir.the_code.guardify.data.states.app_state.LocalAppState
import ir.the_code.guardify.ui.theme.Green
import ir.the_code.guardify.ui.theme.Red
import ir.the_code.guardify.utils.AppPages
import ir.the_code.guardify.viewmodels.validation_link.ValidationLinkViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun ValidationLinkScreen(
    detail: AppPages.ValidationLink,
    viewModel: ValidationLinkViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val appState = LocalAppState.current
    val response by viewModel.response.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        Log.d("dsfdsfsfd", "ValidationLinkScreen: ${detail.url}")
        viewModel.checkUrlIsValid(detail.url)
    }
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        response.onError {
            SideEffect {
                Toast.makeText(context, it.getSuitableMessage(context), Toast.LENGTH_SHORT).show()
            }
            Button(onClick = {
                viewModel.checkUrlIsValid(detail.url)
            }) {
                Text(stringResource(R.string.try_again))
            }
        }.onLoading { CircularProgressIndicator(strokeCap = StrokeCap.Round) }
            .onSuccess { content ->
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .padding(16.dp)
                        .padding(top = 36.dp)
                        .fillMaxSize()
                        .fillMaxWidth(),
                ) {
                    if (content.isBlock) {
                        val composition by rememberLottieComposition(
                            LottieCompositionSpec.RawRes(
                                R.raw.anim_unsafe
                            )
                        )
                        val progress by animateLottieCompositionAsState(composition)
                        LottieAnimation(
                            composition = composition,
                            progress = { progress },
                            modifier = Modifier.size(120.dp)
                        )
                        Text(
                            stringResource(R.string.link_is_not_valid),
                            color = Red,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            stringResource(R.string.open) + " ${detail.url} " + stringResource(R.string.with),
                        )
                    } else {
                        val composition by rememberLottieComposition(
                            LottieCompositionSpec.RawRes(
                                R.raw.anim_safe
                            )
                        )
                        val progress by animateLottieCompositionAsState(composition)
                        LottieAnimation(
                            composition = composition,
                            progress = { progress },
                            modifier = Modifier.size(120.dp)
                        )
                        Text(
                            stringResource(R.string.link_is_valid),
                            color = Green,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            stringResource(R.string.open) + " ${detail.url} " + stringResource(R.string.with),
                        )
                    }
                    Button(onClick = {
                        context.openInChrome(detail.url)
                    }, modifier = Modifier.fillMaxWidth()) {
                        Text(stringResource(R.string.open_with_other_browsers))
                    }
                    Button(onClick = {
                        appState.navController.navigate(AppPages.WebBrowser)
                        runCatching {
                            appState.navController.getBackStackEntry<AppPages.WebBrowser>().savedStateHandle["initial"] =
                                detail.url
                        }
                    }, modifier = Modifier.fillMaxWidth()) {
                        Text(stringResource(R.string.open_with_safe_browser))
                    }
                }
            }
    }
}

private fun Context.openInChrome(url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    intent.component = ComponentName("com.android.chrome", "com.google.android.apps.chrome.Main")
    try {
        startActivity(intent)
    } catch (e: Exception) {
        openInExternalBrowser(url)
    }
}

private fun Context.openInExternalBrowser(url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    val chooser = Intent.createChooser(intent, getString(R.string.open_with))
    startActivity(chooser)
}