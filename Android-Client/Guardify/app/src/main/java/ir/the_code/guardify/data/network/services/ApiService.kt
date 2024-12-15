package ir.the_code.guardify.data.network.services

import ir.the_code.guardify.data.models.message.CheckMessageResponse
import ir.the_code.guardify.data.models.message.MessageBody
import ir.the_code.guardify.data.models.notification.Notification
import ir.the_code.guardify.data.models.phone.BlockedPhone
import ir.the_code.guardify.data.models.register.LoginBody
import ir.the_code.guardify.data.models.register.LoginResponse
import ir.the_code.guardify.data.models.register.RegisterBody
import ir.the_code.guardify.data.models.url.IsUrlBlocked
import ir.the_code.guardify.data.network.response.NetworkErrors
import ir.the_code.guardify.data.network.response.Response

interface ApiService {
    suspend fun login(body: LoginBody): Response<LoginResponse, NetworkErrors>
    suspend fun register(body: RegisterBody): Response<LoginResponse, NetworkErrors>
    suspend fun isLinkBlocked(link: String): Response<IsUrlBlocked, NetworkErrors>
    suspend fun getAllBlockedMobiles(): Response<List<BlockedPhone>, NetworkErrors>
    suspend fun checkMessageHasDangerous(body: MessageBody): Response<CheckMessageResponse, NetworkErrors>
    suspend fun getMyNotifications(): Response<List<Notification>, NetworkErrors>
}