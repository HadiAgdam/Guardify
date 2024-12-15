package ir.the_code.guardify.viewmodels.register

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.the_code.guardify.data.models.register.LoginResponse
import ir.the_code.guardify.data.models.register.RegisterBody
import ir.the_code.guardify.data.network.response.NetworkErrors
import ir.the_code.guardify.data.network.response.Response
import ir.the_code.guardify.data.network.services.ApiService
import ir.the_code.guardify.data.network.services.ApiServiceImpl
import ir.the_code.guardify.data.preferences.UserPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val apiService: ApiService,
    private val userPreferences: UserPreferences
) : ViewModel() {
    private val _response = MutableStateFlow<Response<LoginResponse, NetworkErrors>>(Response.Idle)
    val response = _response.asStateFlow()
    var username by mutableStateOf("")
    var mobile by mutableStateOf("")
    var password by mutableStateOf("")

    fun login() = viewModelScope.launch(Dispatchers.IO) {
        _response.update { Response.Loading }
        val newResponse = apiService.register(
            RegisterBody(
                mobile = mobile,
                name = username,
                password = password
            )
        )
        _response.update { newResponse }
    }

    fun saveToken(token: String) = viewModelScope.launch(Dispatchers.IO) {
        userPreferences.saveUserinfo(username, token)
    }
}