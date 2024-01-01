package com.bhaskarblur.sync_realtimecontentwriting.domain.use_case

import com.bhaskarblur.dictionaryapp.core.utils.Resources
import com.bhaskarblur.sync_realtimecontentwriting.domain.model.DocumentModel
import com.bhaskarblur.sync_realtimecontentwriting.domain.repository.IDocumentRepository
import com.google.firebase.database.ChildEventListener
import kotlinx.coroutines.flow.Flow

class DocumentUseCase(
    private val documentRepository: IDocumentRepository
) {

    fun updateContent(documentId : String, content : String) : Flow<Boolean> {
        return documentRepository.updateContent(documentId, content)
    }

    fun getDocumentDetails(documentId : String) : Flow<Resources<DocumentModel>> {
        return documentRepository.getDocumentDetails(documentId)
    }

    fun switchUserToOffline(documentId : String, userId : String) : Flow<Boolean> {
        return documentRepository.switchUserToOffline(documentId = documentId,
            userId = userId)
    }

    fun switchUserToOnline(documentId : String, userId : String) : Flow<Boolean> {
        return documentRepository.switchUserToOnline(documentId = documentId,
            userId = userId)
    }

    fun liveChangesListener(documentId : String) : Flow<ChildEventListener> {
        return documentRepository.liveChangesListener(documentId)
    }

}