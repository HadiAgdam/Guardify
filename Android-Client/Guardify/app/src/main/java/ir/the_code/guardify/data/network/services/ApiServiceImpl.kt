package ir.the_code.guardify.data.network.services

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import ir.the_code.guardify.data.models.message.CheckMessageResponse
import ir.the_code.guardify.data.models.message.MessageBody
import ir.the_code.guardify.data.models.notification.Notification
import ir.the_code.guardify.data.models.phone.BlockedPhone
import ir.the_code.guardify.data.models.register.LoginBody
import ir.the_code.guardify.data.models.register.LoginResponse
import ir.the_code.guardify.data.models.register.RegisterBody
import ir.the_code.guardify.data.models.url.IsUrlBlocked
import ir.the_code.guardify.data.models.url.IsUrlBlockedBody
import ir.the_code.guardify.data.network.response.NetworkErrors
import ir.the_code.guardify.data.network.response.RequestCallableScope
import ir.the_code.guardify.data.network.response.Response

class ApiServiceImpl(private val httpClient: HttpClient) : ApiService {
    override suspend fun login(body: LoginBody): Response<LoginResponse, NetworkErrors> {
        return RequestCallableScope.launch {
            httpClient.post("users/login") {
                setBody(body)
            }
        }
    }

    override suspend fun register(body: RegisterBody): Response<LoginResponse, NetworkErrors> {
        return RequestCallableScope.launch {
            httpClient.post("users/register") {
                setBody(body)
            }
        }
    }

    override suspend fun isLinkBlocked(link: String): Response<IsUrlBlocked, NetworkErrors> {
        return RequestCallableScope.launch {
            httpClient.post("users/security/link") {
                setBody(
                    IsUrlBlockedBody(url = link)
                )
            }
        }
    }

    override suspend fun getAllBlockedMobiles(): Response<List<BlockedPhone>, NetworkErrors> {
        return RequestCallableScope.launch {
            httpClient.get("users/security/numbers")
        }
    }

    override suspend fun checkMessageHasDangerous(body: MessageBody): Response<CheckMessageResponse, NetworkErrors> {
        return RequestCallableScope.launch {
            httpClient.post("users/security/message") {
                setBody(body)
            }
        }
    }

    override suspend fun getMyNotifications(): Response<List<Notification>, NetworkErrors> {
        return RequestCallableScope.launch {
            httpClient.post("users/messages/") {
                setBody(
                    mapOf("type" to "NOTIFICATION")
                )
            }
        }
    }
}