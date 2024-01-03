package com.bhaskarblur.gptbot.models

import javax.annotation.concurrent.Immutable

@Immutable
data class GptMessageModelDto(
    val message: String,
) {

    fun toGptMessageModel() : GptMessageModel {
        return GptMessageModel(message)
    }
}
