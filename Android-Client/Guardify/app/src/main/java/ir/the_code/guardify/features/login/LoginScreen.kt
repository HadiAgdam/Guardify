package ir.the_code.guardify.features.login

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ir.the_code.guardify.R
import ir.the_code.guardify.components.field.CustomField
import ir.the_code.guardify.data.network.response.getSuitableMessage
import ir.the_code.guardify.data.network.response.onError
import ir.the_code.guardify.data.network.response.onLoading
import ir.the_code.guardify.data.network.response.onSuccess
import ir.the_code.guardify.data.states.app_state.LocalAppState
import ir.the_code.guardify.utils.AppPages
import ir.the_code.guardify.viewmodels.login.LoginViewModel
import ir.the_code.guardify.viewmodels.register.RegisterViewModel
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: LoginViewModel = koinViewModel()
) {
    val appState = LocalAppState.current
    val response by viewModel.response.collectAsStateWithLifecycle()
    val context = LocalContext.current
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            CenterAlignedTopAppBar(title = {
                Text(stringResource(R.string.login))
            })
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(top = 36.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.logo),
                        modifier = Modifier
                            .padding(bottom = 12.dp)
                            .size(200.dp)
                            .align(Alignment.CenterHorizontally),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    CustomField(
                        value = viewModel.mobile,
                        onValueChange = {
                            viewModel.mobile = it
                        },
                        placeholder = stringResource(R.string.mobile)
                    )
                    CustomField(
                        value = viewModel.password,
                        onValueChange = {
                            viewModel.password = it
                        },
                        placeholder = stringResource(R.string.password)
                    )
                    Button(onClick = viewModel::login, modifier = Modifier.fillMaxWidth()) {
                        Text(stringResource(R.string.register))
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(stringResource(R.string.have_not_account))
                        TextButton(onClick = {
                            appState.navController.navigate(AppPages.Register) {
                                popUpTo<AppPages.Login> {
                                    inclusive = true
                                }
                            }
                        }) {
                            Text(stringResource(R.string.login))
                        }
                    }
                }
            }
        }
        response.onError {
            SideEffect {
                Toast.makeText(
                    context,
                    it.getSuitableMessage(context),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }.onLoading { CircularProgressIndicator() }.onSuccess {
            LaunchedEffect(Unit) {
                Toast.makeText(
                    context,
                    context.getString(R.string.success),
                    Toast.LENGTH_SHORT
                ).show()
                viewModel.saveToken(it.user.name, it.token)
                appState.navController.navigate(
                    AppPages.Messages
                ) {
                    popUpTo<AppPages.Login> {
                        inclusive = true
                    }
                }
            }
        }
    }
}