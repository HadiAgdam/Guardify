package ir.the_code.guardify.data.models.message

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class MessageBody(
    val text: String,
    val from: String,
    val type: String
)