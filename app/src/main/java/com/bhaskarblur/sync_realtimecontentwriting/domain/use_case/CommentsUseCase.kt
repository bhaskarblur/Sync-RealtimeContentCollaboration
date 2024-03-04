package com.bhaskarblur.sync_realtimecontentwriting.domain.use_case

import com.bhaskarblur.dictionaryapp.core.utils.Resources
import com.bhaskarblur.sync_realtimecontentwriting.domain.model.CommentsModel
import com.bhaskarblur.sync_realtimecontentwriting.domain.repository.ICommentsRepo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CommentsUseCase @Inject constructor(
    private val commentsRepo : ICommentsRepo
) {
    fun getAllComments() {
        commentsRepo.getDocumentComments()
    }

    fun addComment(documentId : String, comment : CommentsModel) : Flow<Resources<CommentsModel>> {
        return commentsRepo.addComment(documentId, comment)
    }

    fun deleteComment(documentId : String, commentId : String) {
        commentsRepo.deleteComment(documentId, commentId)
    }
}