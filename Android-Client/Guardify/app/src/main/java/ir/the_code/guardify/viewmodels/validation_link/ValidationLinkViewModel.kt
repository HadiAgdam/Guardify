package ir.the_code.guardify.viewmodels.validation_link

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.the_code.guardify.data.models.url.IsUrlBlocked
import ir.the_code.guardify.data.network.response.NetworkErrors
import ir.the_code.guardify.data.network.response.Response
import ir.the_code.guardify.data.network.response.onSuccess
import ir.the_code.guardify.data.network.services.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ValidationLinkViewModel(private val apiService: ApiService) :
    ViewModel() {
    private val _response = MutableStateFlow<Response<IsUrlBlocked, NetworkErrors>>(Response.Idle)
    val response = _response.asStateFlow()

    fun checkUrlIsValid(link:String) = viewModelScope.launch(Dispatchers.IO) {
        _response.update { Response.Loading }
        val newResponse = apiService.isLinkBlocked(link)
        _response.update { newResponse }
    }
}