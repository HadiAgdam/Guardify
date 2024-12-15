package ir.the_code.guardify.viewmodels.messages_inbox

import android.telephony.SmsManager
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.the_code.guardify.data.models.MessageInfo
import ir.the_code.guardify.data.models.SmsMessage
import ir.the_code.guardify.data.repositories.messages.MessagesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MessagesInboxViewModel(
    private val messagesRepository: MessagesRepository,
    val phoneNumber: String
) : ViewModel() {
    private val _messages = MutableStateFlow<List<SmsMessage>>(emptyList())
    val messages = _messages.asStateFlow()

    private var _loading = MutableStateFlow(true)
    val loading = _loading.asStateFlow()


    init {
        fetchAllMessages()
    }

    private fun fetchAllMessages() = viewModelScope.launch(Dispatchers.IO) {
        _loading.update { true }
        messagesRepository.getMessagesByPhoneNumberFlow(phoneNumber).collectLatest { newMessages ->
            _loading.update { false }
            _messages.update { newMessages.sortedBy { it.messageDate } }
        }
    }

    fun sendMessage(smsManager: SmsManager, message: String, onError: (String) -> Unit) =
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                smsManager.sendTextMessage(phoneNumber, null, message, null, null)
            }.onFailure {
                withContext(Dispatchers.Main) {
                    onError.invoke(it.message ?: "Error")
                }
            }
        }
}