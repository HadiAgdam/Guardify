package ir.the_code.guardify.data.helpers

import ir.the_code.guardify.data.models.MessageInfo
import ir.the_code.guardify.data.models.SmsMessage
import kotlinx.coroutines.flow.Flow

interface MessagesHelper {
    fun getAllPreviewMessagesFlow(): Flow<List<MessageInfo>>
    fun getMessagesByPhoneNumberFlow(phoneNumber: String): Flow<List<SmsMessage>>
}