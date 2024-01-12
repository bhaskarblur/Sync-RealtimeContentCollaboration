package com.bhaskarblur.sync_realtimecontentwriting.domain.repository

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import com.bhaskarblur.dictionaryapp.core.utils.Resources
import com.bhaskarblur.sync_realtimecontentwriting.data.remote.dto.PromptModelDto
import com.bhaskarblur.sync_realtimecontentwriting.domain.model.DocumentModel
import com.bhaskarblur.sync_realtimecontentwriting.domain.model.PromptModel
import kotlinx.coroutines.flow.Flow

interface IDocumentRepository {

    var documentDetails: MutableState<DocumentModel>
    fun updateContent(documentId : String, content : String) : Flow<Boolean>
    fun updateCursorPosition(documentId : String, position: Int,userId: String) : Flow<Boolean>
    fun getDocumentDetails(documentId: String, userId: String) : Flow<Resources<DocumentModel>>
    fun switchUserToOffline(userId : String, documentId : String) : Flow<Boolean>
    fun switchUserToOnline(userId : String, documentId : String) : Flow<Boolean>
    fun liveChangesListener(documentId : String) : Flow<Unit>
    fun updateTitle(documentId: String, content: String): Flow<Boolean>
    fun clearPromptsList(documentId : String) : Flow<Boolean>
    fun addPromptMessage(documentId : String, message : PromptModel) : Flow<Boolean>
    fun createDocument(userId: String) : Flow<DocumentModel>
    fun deleteDocument(documentId: String)
    fun getDocumentsByUserId(userId: String) : Flow<List<DocumentModel>>
}