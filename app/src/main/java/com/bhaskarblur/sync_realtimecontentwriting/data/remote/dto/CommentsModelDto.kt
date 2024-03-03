package com.bhaskarblur.sync_realtimecontentwriting.data.remote.dto

import com.bhaskarblur.sync_realtimecontentwriting.domain.model.CommentsModel
import com.bhaskarblur.sync_realtimecontentwriting.domain.model.UserModel

data class CommentsModelDto(
    var commentId : String = "",
    val commentBy : UserModel? = null,
    val commentText : String = "",
    val description : String = "",
    val commentDateTime : Long = 0L
) {
    fun toCommentsModel() : CommentsModel {
        return CommentsModel(commentId,commentBy, commentText, description, commentDateTime)
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