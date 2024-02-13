package com.bhaskarblur.sync_realtimecontentwriting.domain.use_case

import com.bhaskarblur.sync_realtimecontentwriting.domain.model.CommentsModel
import com.bhaskarblur.sync_realtimecontentwriting.domain.repository.ICommentsRepo
import javax.inject.Inject

class CommentsUseCase @Inject constructor(
    private val commentsRepo : ICommentsRepo
) {
    fun getAllComments() {
        commentsRepo.getDocumentComments()
    }

    fun addComment(documentId : String, comment : CommentsModel) {
        commentsRepo.addComment(documentId, comment)
    }

    fun deleteComment(documentId : String, commentId : String) {
        commentsRepo.deleteComment(documentId, commentId)
    }
}