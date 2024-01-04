package com.bhaskarblur.sync_realtimecontentwriting.data.remote.dto

import androidx.compose.runtime.Stable
import com.bhaskarblur.gptbot.models.GptMessageModel
import com.bhaskarblur.gptbot.models.MessageBody
import com.bhaskarblur.sync_realtimecontentwriting.domain.model.PromptModel
import com.bhaskarblur.sync_realtimecontentwriting.domain.model.UserModel

@Stable
data class PromptModelDto(
    val message : String = "",
    val role : String = "",
    val sentBy : UserModelDto? = UserModelDto(),
    val timeStamp: Long = 0
) {

    fun toPromptModel() : PromptModel {
        return PromptModel(message, role, sentBy?.toUserModel(), timeStamp)
    }


    companion object {
        fun fromPromptModel(message: PromptModel): PromptModelDto {
            return PromptModelDto(
                message.message, message.role,
                UserModelDto.fromUserModel(message.sentBy?: UserModel()), message.timeStamp
            )
        }

        fun listToArrayList(list: List<PromptModel>) : ArrayList<PromptModel> {
            val arrayList = arrayListOf<PromptModel>()

            list.forEach {
                arrayList.add(it)
            }
            return arrayList
        }
    }
}