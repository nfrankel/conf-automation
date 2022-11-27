package ch.frankel.conf.automation.feishu

import ch.frankel.conf.automation.AppProperties
import com.fasterxml.jackson.annotation.JsonProperty
import io.netty.handler.logging.LogLevel
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono
import reactor.netty.http.client.HttpClient
import reactor.netty.transport.logging.AdvancedByteBufFormat

class FeishuClient(props: AppProperties) {

    private val webClient: WebClient
    private val feishuProps = props.feishu

    init {
        val httpClient = HttpClient
            .create()
            .wiretap("reactor.netty.http.client.HttpClient", LogLevel.DEBUG, AdvancedByteBufFormat.TEXTUAL)
        webClient = WebClient
            .builder()
            .clientConnector(ReactorClientHttpConnector(httpClient))
            .baseUrl("https://open.feishu.cn/open-apis")
            .build()
    }

    fun post() = webClient.post()
    fun put() = webClient.put()

    internal fun getBearerToken(): Mono<BearerToken> = post()
        .uri("/auth/v3/app_access_token/internal")
        .body(
            BodyInserters.fromValue(
                mapOf(
                    "app_id" to feishuProps.appId,
                    "app_secret" to feishuProps.appSecret
                )
            )
        ).retrieve()
        .bodyToMono<TokenResponse>()
        .map { it.appAccessToken }
        .map { BearerToken(it) }

    private data class TokenResponse(
        @JsonProperty("app_access_token")
        val appAccessToken: String,
        val code: String,
        val expire: Int,
        val msg: String,
        @JsonProperty("tenant_access_token")
        val tenantAccessToken: String
    )

    @JvmInline
    internal value class BearerToken(val token: String)
}
