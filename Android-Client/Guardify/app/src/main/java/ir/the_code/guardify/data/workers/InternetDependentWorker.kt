package ir.the_code.guardify.data.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import ir.the_code.guardify.data.database.BlockedPhoneDao
import ir.the_code.guardify.data.models.message.MessageBody
import ir.the_code.guardify.data.models.phone.BlockedPhone
import ir.the_code.guardify.data.network.response.onError
import ir.the_code.guardify.data.network.response.onLoading
import ir.the_code.guardify.data.network.response.onSuccess
import ir.the_code.guardify.data.network.services.ApiService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


class InternetDependentWorker(context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams), KoinComponent {
    private val apiService: ApiService by inject()
    private val blockedPhoneDao: BlockedPhoneDao by inject()

    override suspend fun doWork(): Result {
        val body = MessageBody(
            inputData.getString("text") ?: "",
            inputData.getString("from") ?: "",
            inputData.getString("type") ?: "",
        )
        Log.d("sfsfsds", "doWork: $body")
        kotlin.runCatching {
            apiService.checkMessageHasDangerous(body).onSuccess {
                Log.d("sfsfsds", "doWork: success $it")
                if (it.category.category != "SAFE" && body.type == "MESSAGE") {
                    val item = blockedPhoneDao.getPhone(body.from)
                    if (item == null) {
                        blockedPhoneDao.add(
                            BlockedPhone(
                                createdAt = "",
                                updatedAt = "",
                                id = 0,
                                reasons = listOf(it.category.category),
                                number = body.from
                            )
                        )
                    } else {
                        blockedPhoneDao.upsertAll(listOf(item.copy(reasons = (item.reasons + it.category.category).distinct())))
                    }
                }
            }.onError {
                Log.d("sfsfsds", "doWork: failure $it")
            }
        }.onFailure {
            Log.d("sfsfsds", "doWork: failure $it")
        }
        return Result.success()
    }
}
