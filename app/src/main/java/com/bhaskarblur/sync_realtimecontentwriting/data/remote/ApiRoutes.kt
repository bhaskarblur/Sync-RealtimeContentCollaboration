package com.bhaskarblur.sync_realtimecontentwriting.data.remote

import android.content.Context
import com.bhaskarblur.gptbot.models.GptBodyDto
import com.bhaskarblur.gptbot.models.MessageResponseDto
import com.bhaskarblur.sync_realtimecontentwriting.R
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiRoutes {

   @POST("completions")
   suspend fun sendMessageToGpt(@Body gptBody: GptBodyDto) : MessageResponseDto
   companion object {
      fun BASE_URL(context: Context): String {
         return context.getString(R.string.GPT_BASE_URl)
      }
      fun API_KEY(context: Context): String {
         return context.getString(R.string.GPT_API_KEY)
      }
   }
}