package ir.the_code.guardify.data.models

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
data class SmsMessage(
    val id: Long,
    val phoneNumber: String,
    val messageBody: String,
    val messageDate: Long,
    val sentByMe: Boolean
)

val numericRegex = Regex("\\b\\d{4,8}\\b")
fun SmsMessage.hasPin() = numericRegex.containsMatchIn(messageBody)
fun MessageInfo.hasPin() = numericRegex.containsMatchIn(lastMessage)
fun MessageInfo.getHiddenMessage() = lastMessage.replace(Regex("\\b\\d{4,6}\\b")) { matchResult ->
    "*".repeat(matchResult.value.length)
}