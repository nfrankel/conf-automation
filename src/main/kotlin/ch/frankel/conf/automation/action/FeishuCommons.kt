package ch.frankel.conf.automation.action

import ch.frankel.conf.automation.AppProperties
import io.netty.handler.logging.LogLevel
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.netty.http.client.HttpClient
import reactor.netty.transport.logging.AdvancedByteBufFormat


internal val feishuClient: WebClient
    get() {
        val httpClient = HttpClient
            .create()
            .wiretap("reactor.netty.http.client.HttpClient", LogLevel.DEBUG, AdvancedByteBufFormat.TEXTUAL)
        return WebClient
            .builder()
            .clientConnector(ReactorClientHttpConnector(httpClient))
            .baseUrl("https://open.feishu.cn/open-apis")
            .build()
    }

internal fun AppProperties.bearerToken(): String? {
    data class TokenResponse(
        val app_access_token: String,
        val code: String,
        val expire: Int,
        val msg: String,
        val tenant_access_token: String
    )
    return feishuClient
        .post()
        .uri("/auth/v3/app_access_token/internal")
        .body(
            BodyInserters.fromValue(
                mapOf(
                    "app_id" to feishu.appId,
                    "app_secret" to feishu.appSecret
                )
            )
        ).retrieve()
        .bodyToMono<TokenResponse>()
        .block()
        ?.app_access_token
}
