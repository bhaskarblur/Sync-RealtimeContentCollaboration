package com.bhaskarblur.sync_realtimecontentwriting.domain.use_case

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import com.bhaskarblur.dictionaryapp.core.utils.Resources
import com.bhaskarblur.sync_realtimecontentwriting.domain.model.DocumentModel
import com.bhaskarblur.sync_realtimecontentwriting.domain.repository.IDocumentRepository
import kotlinx.coroutines.flow.Flow

class DocumentUseCase(
    private val documentRepository: IDocumentRepository
) {

    val _documentDetails = documentRepository.documentDetails

    fun updateContent(documentId : String, content : String) : Flow<Boolean> {
        return documentRepository.updateContent(documentId, content)
    }

    fun updateCursorPosition(documentId : String, position: Int, userId: String) : Flow<Boolean> {
        return documentRepository.updateCursorPosition(documentId, position, userId)
    }

    fun getDocumentDetails(documentId : String, userId: String) : Flow<Resources<DocumentModel>> {
        return documentRepository.getDocumentDetails(documentId, userId)
    }

    fun switchUserToOffline(documentId : String, userId : String) : Flow<Boolean> {
        Log.d("switchOffUseCase", userId.toString())
        return documentRepository.switchUserToOffline(documentId = documentId,
            userId = userId)
    }

    fun switchUserToOnline(documentId : String, userId : String) : Flow<Boolean> {
        Log.d("switchOnUseCase", userId.toString())
        return documentRepository.switchUserToOnline(documentId = documentId,
            userId = userId)
    }

    fun listenToLiveChanges(documentId : String) : Flow<Unit> {
       return documentRepository.liveChangesListener(documentId)
    }

}