package ir.the_code.guardify.data.models.url

import androidx.compose.runtime.Immutable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class IsUrlBlocked(
    @SerialName("is_block")
    val isBlock: Boolean,
    val message: String
)
