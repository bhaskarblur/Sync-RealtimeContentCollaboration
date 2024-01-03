package com.bhaskarblur.gptbot.models

import com.google.gson.annotations.SerializedName

data class GptBodyDto(
    var messages: List<MessageBody>,
    var model: String = "gpt-3.5-turbo",
) {

    fun toGptBody() : GptBody {
        return GptBody(messages, model)
    }

    companion object {
        fun fromGptBody(messages: List<MessageBody>, model: String): GptBodyDto {
            return GptBodyDto(messages, model)
        }
    }
}
data class MessageBody(
    var role: String,
    var content: String
)

data class ChoiceBody(
    var index: Int,
    var message: MessageBody
)

data class MessageResponseDto(
    var choices: List<ChoiceBody>,
    var model: String,
    @SerializedName("object")
    var _object: String,
)