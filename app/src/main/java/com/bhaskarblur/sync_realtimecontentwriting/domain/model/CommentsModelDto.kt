package com.bhaskarblur.sync_realtimecontentwriting.domain.model

import com.bhaskarblur.sync_realtimecontentwriting.data.remote.dto.CommentsModelDto

data class CommentsModel(
    var commentId : String = "id",
    val commentBy : UserModel?,
    val commentText : String = "",
    val description : String = "",
    val commentDateTime : Long = 0L
)  {

    fun toCommentModelDto() : CommentsModelDto {
        return CommentsModelDto(commentId,commentBy, commentText, description,commentDateTime)
    }
}