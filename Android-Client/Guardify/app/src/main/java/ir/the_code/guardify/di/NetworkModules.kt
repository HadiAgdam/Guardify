package ir.the_code.guardify.di

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import ir.the_code.guardify.data.authentication.AuthenticationInterceptor
import ir.the_code.guardify.data.network.services.ApiService
import ir.the_code.guardify.data.network.services.ApiServiceImpl
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module
import java.util.concurrent.TimeUnit

private const val AUTH_QUALIFIER = "auth"

val networkModules = module {
    single<Interceptor>(named(AUTH_QUALIFIER)) {
        AuthenticationInterceptor(get())
    }
    single {
        HttpClient(OkHttp) {
            engine {
                addInterceptor(get(named(AUTH_QUALIFIER)))
                config {
                    retryOnConnectionFailure(true)
                    readTimeout(1, TimeUnit.MINUTES)
                    writeTimeout(5, TimeUnit.MINUTES)
                    connectTimeout(1, TimeUnit.MINUTES)
                }
            }
            defaultRequest {
                url("https://api.foodskills.ir/")
                contentType(ContentType.Application.Json)
            }
            install(ContentNegotiation) {
                json(
                    Json {
                        isLenient = true
                        explicitNulls = true
                        prettyPrint = true
                        ignoreUnknownKeys = true
                    }
                )
            }
        }
    }
    singleOf(::ApiServiceImpl) bind ApiService::class
}