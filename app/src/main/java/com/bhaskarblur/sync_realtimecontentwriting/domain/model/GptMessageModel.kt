package com.bhaskarblur.gptbot.models

import javax.annotation.concurrent.Immutable

@Immutable
data class GptMessageModel(
    val message: String,
)
