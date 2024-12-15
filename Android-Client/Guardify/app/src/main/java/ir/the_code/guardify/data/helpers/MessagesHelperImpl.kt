package ir.the_code.guardify.data.helpers

import android.content.ContentResolver
import android.content.Context
import android.database.ContentObserver
import android.database.Cursor
import android.net.Uri
import android.os.Handler
import android.provider.ContactsContract
import android.provider.Telephony
import android.util.Log
import ir.the_code.guardify.data.models.MessageInfo
import ir.the_code.guardify.data.models.SmsMessage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow


class MessagesHelperImpl(private val context: Context) : MessagesHelper {
    override fun getAllPreviewMessagesFlow(): Flow<List<MessageInfo>> =
        callbackFlow {
            val contentResolver = context.contentResolver
            val smsUri = Uri.parse("content://sms/inbox")
            val observer = object : ContentObserver(null) {
                override fun onChange(selfChange: Boolean) {
                    trySend(getAllPreviewMessages())
                }
            }
            contentResolver.registerContentObserver(smsUri, true, observer)

            trySend(getAllPreviewMessages())

            awaitClose { contentResolver.unregisterContentObserver(observer) }
        }

    override fun getMessagesByPhoneNumberFlow(phoneNumber: String): Flow<List<SmsMessage>> =
        callbackFlow {
            val contentResolver: ContentResolver = context.contentResolver
            val smsUri = Uri.parse("content://sms/")

            val observer = object : ContentObserver(null) {
                override fun onChange(selfChange: Boolean) {
                    super.onChange(selfChange)
                    trySend(getMessagesByPhoneNumber(contentResolver, phoneNumber))
                }
            }

            contentResolver.registerContentObserver(smsUri, true, observer)

            trySend(getMessagesByPhoneNumber(contentResolver, phoneNumber))

            awaitClose {
                contentResolver.unregisterContentObserver(observer)
            }
        }

    private fun getMessagesByPhoneNumber(
        contentResolver: ContentResolver,
        phoneNumber: String
    ): List<SmsMessage> {
        val smsUri = Telephony.Sms.CONTENT_URI
        val projection = arrayOf("_id", "address", "date", "body", "type")

        val selection = "address = ?"
        val selectionArgs = arrayOf(phoneNumber)

        val messages = mutableListOf<SmsMessage>()

        val cursor: Cursor? = contentResolver.query(
            smsUri,
            projection,
            selection,
            selectionArgs,
            "date DESC"
        )

        cursor?.use {
            if (it.moveToFirst()) {
                do {
                    val id = it.getLong(it.getColumnIndexOrThrow("_id"))
                    val address = it.getString(it.getColumnIndexOrThrow("address"))
                    val date = it.getLong(it.getColumnIndexOrThrow("date"))
                    val body = it.getString(it.getColumnIndexOrThrow("body")) ?: ""
                    val type = it.getInt(it.getColumnIndexOrThrow("type"))

                    val sentByMe = type == 2

                    if (address != null) {
                        messages += SmsMessage(
                            id = id,
                            phoneNumber = address,
                            messageBody = body,
                            messageDate = date,
                            sentByMe = sentByMe
                        )
                    }
                } while (it.moveToNext())
            }
        }

        return messages
    }

    fun getAllPreviewMessages(): List<MessageInfo> {
        val contentResolver: ContentResolver = context.contentResolver
        val smsUri = Uri.parse("content://sms/inbox")
        val messagesMap = mutableMapOf<String, MessageInfo>()

        val projection = arrayOf("address", "date", "body")
        val cursor: Cursor? = contentResolver.query(smsUri, projection, null, null, "date DESC")

        cursor?.use {
            while (it.moveToNext()) {
                val phoneNumber = it.getString(it.getColumnIndexOrThrow("address")) ?: continue
                val messageDate = it.getLong(it.getColumnIndexOrThrow("date"))
                val lastMessage = it.getString(it.getColumnIndexOrThrow("body")) ?: ""
                if (!messagesMap.containsKey(phoneNumber)) {
                    val photoUri = getContactPhotoUri(phoneNumber)
                    messagesMap[phoneNumber] = MessageInfo(
                        phoneNumber = phoneNumber,
                        lastMessage = lastMessage,
                        messageDate = messageDate,
                        photoUri = photoUri,
                    )
                }
            }
        }

        return messagesMap.values.toList().sortedByDescending { it.messageDate }
    }

    private fun getContactPhotoUri(phoneNumber: String): String? {
        val contentResolver: ContentResolver = context.contentResolver
        val uri = Uri.withAppendedPath(
            ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
            Uri.encode(phoneNumber)
        )
        val projection = arrayOf(ContactsContract.PhoneLookup.PHOTO_URI)

        val cursor: Cursor? = contentResolver.query(uri, projection, null, null, null)

        cursor?.use {
            if (it.moveToFirst()) {
                return it.getString(it.getColumnIndexOrThrow(ContactsContract.PhoneLookup.PHOTO_URI))
            }
        }

        return null
    }
}