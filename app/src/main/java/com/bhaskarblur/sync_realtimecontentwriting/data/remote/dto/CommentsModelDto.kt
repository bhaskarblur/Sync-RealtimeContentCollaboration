package com.bhaskarblur.sync_realtimecontentwriting.data.remote.dto

import com.bhaskarblur.sync_realtimecontentwriting.domain.model.CommentsModel
import com.bhaskarblur.sync_realtimecontentwriting.domain.model.PromptModel
import com.bhaskarblur.sync_realtimecontentwriting.domain.model.UserModel

data class CommentsModelDto(
    val commentId : String,
    val commentBy : UserModel? = null,
    val comment : String = "",
    val description : String = "",
    val commentDateTime : Long = 0L
) {
    fun toCommentsModel() : CommentsModel {
        return CommentsModel(commentId,commentBy, comment, description, commentDateTime)
    }

    companion object {
        fun listToArrayList(list: List<CommentsModel>) : ArrayList<CommentsModel> {
            val arrayList = arrayListOf<CommentsModel>()

            list.forEach {
                arrayList.add(it)
            }
            return arrayList
        }
    }
}