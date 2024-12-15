package ir.the_code.guardify.data.models.register


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val message: String,
    val token: String,
    val user: User
) {
    @Serializable
    data class User(
        @SerialName("created_at")
        val createdAt: String,
        val id: String,
        val mobile: String,
        val name: String,
        val password: String,
        @SerialName("updated_at")
        val updatedAt: String
    )
}