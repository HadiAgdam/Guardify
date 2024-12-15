package ir.the_code.guardify.data.models.url

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class IsUrlBlockedBody(
    val url: String
)
