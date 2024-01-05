package com.bhaskarblur.sync_realtimecontentwriting.domain.model

import com.bhaskarblur.gptbot.models.GptMessageModel
import com.bhaskarblur.gptbot.models.MessageBody
import javax.annotation.concurrent.Immutable

@Immutable
data class PromptModel(
    var message: String = "",
    val role: String = "",
    val sentBy: UserModel?,
    val timeStamp: Long
) {
    fun toMessageModel(): MessageBody {
        return MessageBody(role, message)
    }
}