package com.bhaskarblur.gptbot.models

import javax.annotation.concurrent.Immutable

@Immutable
data class GptBody(
    var messages: List<MessageBody>,
    var model: String = "gpt-3.5-turbo"
)
