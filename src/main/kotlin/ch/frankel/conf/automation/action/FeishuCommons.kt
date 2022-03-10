package ch.frankel.conf.automation.action

import ch.frankel.conf.automation.AppProperties
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono

internal val feishuClient: WebClient
    get() = WebClient.create("https://open.feishu.cn/open-apis")

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
