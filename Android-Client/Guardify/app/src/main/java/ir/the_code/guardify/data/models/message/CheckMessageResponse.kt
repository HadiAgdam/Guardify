package ir.the_code.guardify.data.models.message


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CheckMessageResponse(
    val category: Category,
    val message: Message
) {
    @Serializable
    data class Category(
        val category: String,
        val url: String
    )

    @Serializable
    data class Message(
        @SerialName("created_at")
        val createdAt: String,
        val id: Int,
        @SerialName("number_from")
        val numberFrom: String,
        @SerialName("number_to")
        val numberTo: String? = null,
        val status: String,
        val text: String,
        @SerialName("updated_at")
        val updatedAt: String,
        @SerialName("user_id")
        val userId: String
    )
}