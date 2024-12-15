package ir.the_code.guardify.data.models.notification


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Notification(
    @SerialName("created_at")
    val createdAt: String,
    val id: Int,
    @SerialName("number_from")
    val numberFrom: String,
    @SerialName("number_to")
    val numberTo: String?,
    val status: String,
    val text: String,
    val type: String,
    @SerialName("updated_at")
    val updatedAt: String,
    @SerialName("user_id")
    val userId: String
)