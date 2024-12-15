package ir.the_code.guardify.data.network.response

import android.util.Log
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.serialization.SerializationException

object RequestCallableScope {
    suspend inline fun <reified D> launch(action: () -> HttpResponse): Response<D, NetworkErrors> {
        val response = try {
            action.invoke()
        } catch (e: UnresolvedAddressException) {
            return Response.Error(NetworkErrors.NO_INTERNET)
        } catch (e: SerializationException) {
            return Response.Error(NetworkErrors.SERIALIZE)
        } catch (e: ServerResponseException) {
            return Response.Error(NetworkErrors.SERVER_ERROR)
        } catch (e: ClientRequestException) {
            return Response.Error(NetworkErrors.CLIENT_ERROR)
        } catch (e: Exception) {
            return Response.Error(NetworkErrors.UNKNOWN)
        }
        Log.d("'sdfsfsdfsd'", "launch: ${response.bodyAsText()}")
        return when (response.status.value) {
            in 200..299 -> {
                try {
                    val serialized = response.body<D>()
                    Response.Success(serialized)
                } catch (e: SerializationException) {
                    Response.Error(NetworkErrors.SERIALIZE)
                }
            }

            400 -> Response.Error(NetworkErrors.BAD_REQUEST)
            401 -> Response.Error(NetworkErrors.UNAUTHORIZED)
            422 -> Response.Error(NetworkErrors.INVALID_DATA)
            408 -> Response.Error(NetworkErrors.REQUEST_TIMEOUT)
            413 -> Response.Error(NetworkErrors.PAY_LOAD_TOO_LARGE)
            in 500..599 -> Response.Error(NetworkErrors.SERVER_ERROR)
            else -> Response.Error(NetworkErrors.UNKNOWN)
        }
    }
}