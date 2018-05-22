package io.github.yusaka39.easySlackbot.slack.impl

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.ullink.slack.simpleslackapi.SlackSession
import io.github.yusaka39.easySlackbot.api.entity.Channel
import io.github.yusaka39.easySlackbot.slack.ChannelImpl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.apache.commons.codec.net.URLCodec
import java.util.concurrent.TimeUnit

const val IM_LIST_ENDPOINT = "https://slack.com/api/im.list"

private val CLIENT = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .build()

private val MAPPER = ObjectMapper().registerModule(KotlinModule())

internal fun SlackSession.fetchDirectMessages(token: String): Map<String, Channel> {
    var cursor: String? = null
    var lst: List<Im> = listOf()
    do {
        val res: ImListResponse? = CLIENT.newCall(
                getRequestForImList(token, cursor)
        ).execute().use {
            it.body()?.byteStream()?.use {
                MAPPER.readValue(it)
            }
        }
        cursor = res?.metadata?.nextCursor
        res?.let {
            lst += it.ims
        }
    } while (cursor != null)
    return lst.fold(mutableMapOf()) { acc, im ->
        acc.apply {
            this[im.user] = ChannelImpl(im.id, null)
        }
    }
}

private fun getRequestForImList(token: String, cursor: String? = null) =
        Request.Builder()
                .get()
                .url("$IM_LIST_ENDPOINT?token=${token.toUrlEncoded()}" +
                        (cursor?.let { "&cursor=${cursor.toUrlEncoded()}" } ?: ""))
                .build()

private fun String.toUrlEncoded() = URLCodec("UTF-8").encode(this)

@JsonIgnoreProperties(ignoreUnknown = true)
private data class ImListResponse @JsonCreator constructor(
        @param:JsonProperty("ok") val ok: Boolean,
        @param:JsonProperty("ims") val ims: List<Im>,
        @param:JsonProperty("response_metadata") val metadata: ResponseMetadata? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
private data class Im(
        @param:JsonProperty("id") val id: String,
        @param:JsonProperty("created") val created: Long,
        @param:JsonProperty("is_im") val isIm: Boolean,
        @param:JsonProperty("is_org_shared") val isOrgShared: Boolean,
        @param:JsonProperty("user") val user: String,
        @param:JsonProperty("is_user_deleted") val isUserDeleted: Boolean,
        @param:JsonProperty("priority") val priority: Int
)

@JsonIgnoreProperties(ignoreUnknown = true)
private data class ResponseMetadata(
        @param:JsonProperty("next_cursor") val nextCursor: String?
)