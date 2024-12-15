package ir.the_code.guardify.data.models.phone


import androidx.compose.runtime.Immutable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Immutable
@Entity(
    "blocked_phones"
)
data class BlockedPhone(
    @SerialName("created_at")
    val createdAt: String,
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val number: String,
    val reasons: List<String>,
    @SerialName("updated_at")
    val updatedAt: String
)