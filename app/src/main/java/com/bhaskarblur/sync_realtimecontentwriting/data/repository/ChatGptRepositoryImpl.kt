package com.bhaskarblur.sync_realtimecontentwriting.data.repository

import com.bhaskarblur.dictionaryapp.core.utils.Resources
import com.bhaskarblur.gptbot.models.ChoiceBody
import com.bhaskarblur.gptbot.models.GptBody
import com.bhaskarblur.gptbot.models.GptBodyDto
import com.bhaskarblur.gptbot.models.GptMessageModel
import com.bhaskarblur.sync_realtimecontentwriting.data.remote.ApiRoutes
import com.bhaskarblur.sync_realtimecontentwriting.domain.repository.IChatGptRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.lang.Exception
import javax.inject.Inject

class ChatGptRepositoryImpl @Inject constructor(
    private val apiImpl : ApiRoutes
) : IChatGptRepo {
    override fun getGptSuggestion(gptBody: GptBody): Flow<Resources<List<ChoiceBody>>> = flow {
        emit(Resources.Loading(data = listOf()))
        val result = apiImpl.sendMessageToGpt(gptBody = GptBodyDto.fromGptBody(gptBody.messages, gptBody.model))

        try {
            if (result.choices.isNotEmpty()) {
                emit(Resources.Success(data= result.choices))
            } else {
                emit(Resources.Error(data = listOf(), message = "There was an error!"))
            }
        }
        catch (e : Exception) {
            e.printStackTrace()
            emit(Resources.Error(data = listOf(), message = e.message))
        }
    }
}