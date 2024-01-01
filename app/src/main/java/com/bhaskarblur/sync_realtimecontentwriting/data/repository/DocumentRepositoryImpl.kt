package com.bhaskarblur.sync_realtimecontentwriting.data.repository

import com.bhaskarblur.dictionaryapp.core.utils.Resources
import com.bhaskarblur.sync_realtimecontentwriting.data.remote.FirebaseManager
import com.bhaskarblur.sync_realtimecontentwriting.domain.model.DocumentModel
import com.bhaskarblur.sync_realtimecontentwriting.domain.repository.IDocumentRepository
import com.google.firebase.database.ChildEventListener
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DocumentRepositoryImpl @Inject constructor(
    private val firebaseManager: FirebaseManager
) : IDocumentRepository {

    override fun updateContent(documentId: String, content: String): Flow<Boolean>  = flow {
        emit(firebaseManager.updateDocumentContent(documentId, content))
    }

    override fun getDocumentDetails(documentId: String): Flow<Resources<DocumentModel>> = flow {
        emit(Resources.Loading())

        val documentData = firebaseManager.getDocumentDetails(documentId)
        if(documentData.documentId != null) {
            emit(Resources.Success(data = documentData))
        }
        else {
            emit(Resources.Error(message = "", data = null))
        }
    }

    override fun switchUserToOffline(userId: String, documentId: String) = flow {
        emit(firebaseManager.switchUserToOffline(documentId, userId))
    }

    override fun switchUserToOnline(userId: String, documentId: String): Flow<Boolean> = flow {
        emit(firebaseManager.switchUserToOnline(documentId, userId))
    }

    override fun liveChangesListener(documentId: String): Flow<ChildEventListener> = flow {
        emit(firebaseManager.liveChangesListener(documentId))
    }


}