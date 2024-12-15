package ir.the_code.guardify

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.provider.Telephony
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.core.util.Consumer
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ir.the_code.guardify.data.preferences.SettingsPreferences
import ir.the_code.guardify.data.preferences.UserPreferences
import ir.the_code.guardify.data.recivers.SmsReceiver
import ir.the_code.guardify.data.states.app_state.LocalAppState
import ir.the_code.guardify.data.states.app_state.WithLocalAppState
import ir.the_code.guardify.features.main.MainNavigation
import ir.the_code.guardify.ui.theme.GuardifyTheme
import ir.the_code.guardify.utils.AppPages
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import org.koin.android.ext.android.inject

class MainActivity : FragmentActivity() {
    private val settingsPreferences: SettingsPreferences by inject()
    private val userPreferences: UserPreferences by inject()
    private var isAuthenticated by mutableStateOf(false)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val currentPage = getIntentPage(intent)
        if (isDefaultBrowser().not()) {
            openDefaultAppSettings()
        }


        val currentToken = runBlocking {
            userPreferences.token.firstOrNull()
        }
        setContent {
            val isSystemInDark = isSystemInDarkTheme()
            val isDark by settingsPreferences.isDarkTheme.collectAsStateWithLifecycle(isSystemInDark)
            WithLocalAppState {
                GuardifyTheme(isDark ?: isSystemInDark) {
                    val appState = LocalAppState.current
                    DisposableEffect(Unit) {
                        val onNewIntent = Consumer<Intent> {
                            appState.navController.navigate(getIntentPage(it))
                        }
                        addOnNewIntentListener(onNewIntent)
                        onDispose {
                            removeOnNewIntentListener(onNewIntent)
                        }
                    }
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background,
                        contentColor = MaterialTheme.colorScheme.onBackground
                    ) {
                        MainNavigation(
                            if (currentToken == null) AppPages.Register else currentPage,
                            isAuthenticated = isAuthenticated,
                            onRequestAuthenticate = ::requestAuthenticate
                        )
                    }
                }
            }
        }
    }

    private fun requestAuthenticate() {
        if (isBiometricAvailable().not()) {
            isAuthenticated = true
            return
        }
        val executor = ContextCompat.getMainExecutor(this)
        val biometricPrompt =
            BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    Log.d("dsfsdsdfsf", "onAuthenticationError: success")
                    isAuthenticated = true
                }

                override fun onAuthenticationFailed() {
                    isAuthenticated = false
                    Log.d("dsfsdsdfsf", "onAuthenticationError: fail")
                    super.onAuthenticationFailed()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    isAuthenticated = false
                    Log.d("dsfsdsdfsf", "onAuthenticationError: $errString")
                    super.onAuthenticationError(errorCode, errString)
                }
            })

        val promptInfo = BiometricPrompt.PromptInfo.Builder().setTitle("Biometric Authentication")
            .setSubtitle("Authenticate using your fingerprint").setAllowedAuthenticators(
                BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL
            ).build()

        biometricPrompt.authenticate(promptInfo)
    }


    private fun isBiometricAvailable(): Boolean {
        val biometricManager = BiometricManager.from(this)
        return when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> true
            else -> false
        }
    }

    private fun openDefaultAppSettings() {
        val intent = Intent(android.provider.Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS)
        startActivity(intent)
    }

    private fun getIntentPage(intent: Intent): AppPages {
        val action = intent.action
        val data: Uri? = intent.data

        return if (action == Intent.ACTION_VIEW && data != null) {
            AppPages.ValidationLink(
                url = data.toString()
            )
        } else {
            AppPages.Messages
        }
    }

    private fun isDefaultBrowser(): Boolean {
        val packageManager = packageManager
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("http://example.com")
        }

        val resolveInfoList =
            packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
        for (resolveInfo in resolveInfoList) {
            val packageName = resolveInfo.activityInfo.packageName
            if (packageName == this.packageName) {
                return true
            }
        }
        return false
    }
}