package ir.the_code.guardify.data.models

import androidx.compose.runtime.Immutable

@Immutable
data class MessageInfo(
    val phoneNumber: String,
    val lastMessage: String,
    val messageDate: Long,
    val photoUri: String? = null,
)