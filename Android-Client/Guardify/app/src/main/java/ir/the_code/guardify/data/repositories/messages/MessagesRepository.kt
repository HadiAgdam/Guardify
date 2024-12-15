package ir.the_code.guardify.data.repositories.messages

import ir.the_code.guardify.data.models.MessageInfo
import ir.the_code.guardify.data.models.SmsMessage
import kotlinx.coroutines.flow.Flow

interface MessagesRepository {
    fun getAllPreviewMessagesFlow(): Flow<List<MessageInfo>>
    fun getMessagesByPhoneNumberFlow(phoneNumber: String): Flow<List<SmsMessage>>
}