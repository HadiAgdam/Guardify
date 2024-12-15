package ir.the_code.guardify.data.repositories.messages

import ir.the_code.guardify.data.helpers.MessagesHelper
import ir.the_code.guardify.data.models.MessageInfo
import ir.the_code.guardify.data.models.SmsMessage
import kotlinx.coroutines.flow.Flow

class MessagesRepositoryImpl(private val messagesHelper: MessagesHelper) : MessagesRepository {
    override fun getAllPreviewMessagesFlow(): Flow<List<MessageInfo>> =
        messagesHelper.getAllPreviewMessagesFlow()

    override fun getMessagesByPhoneNumberFlow(phoneNumber: String): Flow<List<SmsMessage>> =
        messagesHelper.getMessagesByPhoneNumberFlow(phoneNumber)
}