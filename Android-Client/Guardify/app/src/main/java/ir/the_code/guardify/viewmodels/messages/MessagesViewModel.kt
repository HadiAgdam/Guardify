package ir.the_code.guardify.viewmodels.messages

import android.Manifest
import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.the_code.guardify.data.database.BlockedPhoneDao
import ir.the_code.guardify.data.models.MessageInfo
import ir.the_code.guardify.data.network.response.onSuccess
import ir.the_code.guardify.data.network.services.ApiService
import ir.the_code.guardify.data.repositories.messages.MessagesRepository
import ir.the_code.guardify.data.services.MySmsListenerService
import ir.the_code.guardify.utils.hasPermissions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@SuppressLint("StaticFieldLeak")
class MessagesViewModel(
    private val messagesRepository: MessagesRepository,
    private val apiService: ApiService,
    private val blockedPhonesDao: BlockedPhoneDao,
    private val context: Context
) : ViewModel() {
    private val _messages = MutableStateFlow<List<MessageInfo>>(emptyList())
    val messages = _messages.asStateFlow()

    private val _blockedUsersReason = MutableStateFlow<Map<String, List<String>>>(emptyMap())
    val blockedUsersReason = _blockedUsersReason.asStateFlow()

    private var _loading = MutableStateFlow(true)
    val loading = _loading.asStateFlow()


    fun getPermissionsList() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) listOf(
        Manifest.permission.READ_SMS,
        Manifest.permission.SEND_SMS,
        Manifest.permission.READ_CONTACTS,
        Manifest.permission.POST_NOTIFICATIONS,
        Manifest.permission.RECEIVE_SMS,
    ) else listOf(
        Manifest.permission.READ_SMS,
        Manifest.permission.SEND_SMS,
        Manifest.permission.READ_CONTACTS,
        Manifest.permission.RECEIVE_SMS,
    )

    init {
        if (context.hasPermissions(
                getPermissionsList()
            )
        ) {
            fetchAllMessages()
        }

        fetchAllBlockedUsersFromDatabase()
        fetchAllBlockedUsersFromApi()
    }

    fun fetchAllBlockedUsersFromApi() = viewModelScope.launch(Dispatchers.IO) {
        apiService.getAllBlockedMobiles().onSuccess {
            blockedPhonesDao.upsertAll(it)
        }
    }

    private fun fetchAllBlockedUsersFromDatabase() = viewModelScope.launch(Dispatchers.IO) {
        blockedPhonesDao.getAll().collectLatest { newBlockedUsers ->
            Log.d("DSfsdfdd", "fetchAllBlockedUsersFromDatabase: $newBlockedUsers")
            _blockedUsersReason.update {
                newBlockedUsers.associateBy({ it.number },
                    { it.reasons })
            }
        }
    }

    fun fetchAllMessages() = viewModelScope.launch(Dispatchers.IO) {
        startService()
        _loading.update { true }
        messagesRepository.getAllPreviewMessagesFlow().collectLatest { newMessages ->
            _loading.update { false }
            _messages.update { newMessages }
        }
    }

    private fun startService() {
        val activityManager = context.getSystemService(ActivityManager::class.java)
        val isRun = activityManager.getRunningServices(Int.MAX_VALUE).any {
            it.service.className == MySmsListenerService::class.java.name
        }
        if (isRun.not()) {
            val intent = Intent(context, MySmsListenerService::class.java)
            context.startService(intent)
        }
    }
}