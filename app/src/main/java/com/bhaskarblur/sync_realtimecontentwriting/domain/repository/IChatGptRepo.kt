package com.bhaskarblur.sync_realtimecontentwriting.domain.repository

import com.bhaskarblur.dictionaryapp.core.utils.Resources
import com.bhaskarblur.gptbot.models.ChoiceBody
import com.bhaskarblur.gptbot.models.GptBody
import com.bhaskarblur.gptbot.models.GptMessageModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface IChatGptRepo {
    fun getGptSuggestion(gptBody: GptBody) : Flow<Resources<List<ChoiceBody>>>
}