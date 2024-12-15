package ir.the_code.guardify.viewmodels.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.the_code.guardify.data.models.notification.Notification
import ir.the_code.guardify.data.network.response.NetworkErrors
import ir.the_code.guardify.data.network.response.Response
import ir.the_code.guardify.data.network.services.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MyNotificationsViewModel(private val apiService: ApiService) : ViewModel() {
    private val _response =
        MutableStateFlow<Response<List<Notification>, NetworkErrors>>(Response.Idle)
    val response = _response.asStateFlow()


    init {
        fetchMyNotifications()
    }

    fun fetchMyNotifications() = viewModelScope.launch(Dispatchers.IO) {
        _response.update { Response.Loading }
        val newResponse = apiService.getMyNotifications()
        _response.update { newResponse }
    }
}