package ir.the_code.guardify.data.models.register


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginBody(
    val mobile: String,
    val password: String
)