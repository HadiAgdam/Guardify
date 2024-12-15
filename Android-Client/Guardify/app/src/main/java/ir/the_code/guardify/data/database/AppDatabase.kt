package ir.the_code.guardify.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import ir.the_code.guardify.data.models.phone.BlockedPhone
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Database(
    entities = [
        BlockedPhone::class
    ],
    version = 2
)
@TypeConverters(StringListConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun blockedPhonesDao(): BlockedPhoneDao
}

class StringListConverter {
    @TypeConverter
    fun fromList(list: List<String>?): String {
        return if (list.isNullOrEmpty()) "" else Json.encodeToString(list)
    }

    @TypeConverter
    fun toList(data: String?): List<String> {
        return if (data.isNullOrEmpty()) emptyList() else Json.decodeFromString(data)
    }
}