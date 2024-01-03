package com.bhaskarblur.sync_realtimecontentwriting.domain.use_case

import com.bhaskarblur.dictionaryapp.core.utils.Resources
import com.bhaskarblur.gptbot.models.ChoiceBody
import com.bhaskarblur.gptbot.models.GptBody
import com.bhaskarblur.sync_realtimecontentwriting.domain.repository.IChatGptRepo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GptUseCase @Inject constructor(
    private val gptRepo : IChatGptRepo
) {

    fun getGptSuggestion(gptBody: GptBody) : Flow<Resources<List<ChoiceBody>>> {
        return gptRepo.getGptSuggestion(gptBody)
    }
}