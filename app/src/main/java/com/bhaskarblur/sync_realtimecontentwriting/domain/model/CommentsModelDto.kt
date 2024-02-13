package com.bhaskarblur.sync_realtimecontentwriting.domain.model

import com.bhaskarblur.sync_realtimecontentwriting.data.remote.dto.CommentsModelDto

data class CommentsModel(
    var commentId : String,
    val commentBy : UserModel?,
    val comment : String = "",
    val description : String = "",
    val commentDateTime : Long = 0L
)  {

    fun toCommentModelDto() : CommentsModelDto {
        return CommentsModelDto(commentId,commentBy, comment, description,commentDateTime)
    }
}