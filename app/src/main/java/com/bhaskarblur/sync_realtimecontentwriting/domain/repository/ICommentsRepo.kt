package com.bhaskarblur.sync_realtimecontentwriting.domain.repository

import com.bhaskarblur.dictionaryapp.core.utils.Resources
import com.bhaskarblur.sync_realtimecontentwriting.domain.model.CommentsModel
import kotlinx.coroutines.flow.Flow

interface ICommentsRepo {
    fun getDocumentComments()
    fun addComment(documentId : String, comment : CommentsModel) : Flow<Resources<Boolean>>
    fun deleteComment(documentId : String, commentId : String) : Flow<Resources<Boolean>>


}