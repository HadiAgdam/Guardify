package ir.the_code.guardify.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import ir.the_code.guardify.data.models.phone.BlockedPhone
import kotlinx.coroutines.flow.Flow

@Dao
interface BlockedPhoneDao {
    @Query("SELECT * FROM blocked_phones")
    fun getAll(): Flow<List<BlockedPhone>>

    @Query("SELECT * FROM blocked_phones WHERE number == :number")
    fun getPhone(number:String): BlockedPhone?

    @Upsert
    suspend fun upsertAll(blockedPhones: List<BlockedPhone>)

    @Insert
    suspend fun add(blockedPhone: BlockedPhone)
}