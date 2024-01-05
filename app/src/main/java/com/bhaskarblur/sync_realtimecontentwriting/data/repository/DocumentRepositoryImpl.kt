package com.bhaskarblur.sync_realtimecontentwriting.data.repository

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.bhaskarblur.dictionaryapp.core.utils.Resources
import com.bhaskarblur.sync_realtimecontentwriting.data.remote.FirebaseManager
import com.bhaskarblur.sync_realtimecontentwriting.data.remote.dto.PromptModelDto
import com.bhaskarblur.sync_realtimecontentwriting.domain.model.DocumentModel
import com.bhaskarblur.sync_realtimecontentwriting.domain.model.PromptModel
import com.bhaskarblur.sync_realtimecontentwriting.domain.repository.IDocumentRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DocumentRepositoryImpl @Inject constructor(
    private val firebaseManager: FirebaseManager
) : IDocumentRepository {

    override var documentDetails: MutableState<DocumentModel> = firebaseManager.documentDetails

    override fun updateContent(documentId: String, content: String): Flow<Boolean>  {
        firebaseManager.updateDocumentContent(documentId, content)
        return flow {
            emit(
                true
            )
        }
    }

    override fun updateTitle(documentId: String, content: String): Flow<Boolean>  {
        firebaseManager.updateDocumentTitle(documentId, content)
        return flow {
            emit(
                true
            )
        }
    }

    override fun clearPromptsList(documentId: String): Flow<Boolean> {
        firebaseManager.clearPromptsList(documentId)
        return flow {
            emit(
                true
            )
        }
    }

    override fun addPromptMessage(documentId: String, message: PromptModel): Flow<Boolean> {
        firebaseManager.addPromptMessage(documentId, PromptModelDto.fromPromptModel(message))
        return flow {
            emit(
                true
            )
        }
    }

    override fun updateCursorPosition(
        documentId: String,
        position: Int,
        userId: String
    ): Flow<Boolean> {
        firebaseManager.changeUserCursorPosition(
            documentId,
            position = position,
            userId = userId
        )
        return flow {
            emit(
               true
            )
        }
    }

    override fun getDocumentDetails(
        documentId: String,
        userId: String
    ): Flow<Resources<DocumentModel>> = flow {
        emit(Resources.Loading())
        val documentData = firebaseManager.getDocumentDetails(documentId)
        Log.d("docDataInRepo", documentData.toString())
        if (documentData.documentId != null) {
            documentDetails.value = documentData
            emit(Resources.Success(data = documentData))
        } else {
            emit(Resources.Error(message = "", data = null))
        }
    }

    override fun switchUserToOffline(userId: String, documentId: String): Flow<Boolean> {
        Log.d("switchOffRepo", userId.toString())
        firebaseManager.switchUserToOffline(documentId, userId)
        return flow {
            emit(true)
        }
    }

    override fun switchUserToOnline(userId: String, documentId: String): Flow<Boolean> {
        firebaseManager.switchUserToOnline(documentId, userId)
        Log.d("switchOnRepository", "Yes")
        return flow {
            emit(true)
        }
    }

    override fun liveChangesListener(documentId: String): Flow<Unit> = flow {
    }


}