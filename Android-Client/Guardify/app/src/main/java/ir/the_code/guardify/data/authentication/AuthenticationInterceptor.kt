package ir.the_code.guardify.data.authentication

import ir.the_code.guardify.data.preferences.UserPreferences
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthenticationInterceptor(private val userPreferences: UserPreferences) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking { userPreferences.token.firstOrNull() }
        val newRequest = chain.request().newBuilder().apply {
            if (token != null) {
                addHeader("Authorization", "Bearer $token")
            }
        }.build()
        return chain.proceed(newRequest)
    }
}