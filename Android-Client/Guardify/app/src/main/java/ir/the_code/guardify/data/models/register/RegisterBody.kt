package ir.the_code.guardify.data.models.register


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegisterBody(
    val mobile: String,
    val name: String,
    val password: String
)