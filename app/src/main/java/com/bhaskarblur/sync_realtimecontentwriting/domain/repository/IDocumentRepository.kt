package com.bhaskarblur.sync_realtimecontentwriting.domain.repository

import com.bhaskarblur.dictionaryapp.core.utils.Resources
import com.bhaskarblur.sync_realtimecontentwriting.domain.model.DocumentModel
import com.google.firebase.database.ChildEventListener
import kotlinx.coroutines.flow.Flow

interface IDocumentRepository {

    fun updateContent(documentId : String, content : String) : Flow<Boolean>
    fun getDocumentDetails(documentId : String) : Flow<Resources<DocumentModel>>
    fun switchUserToOffline(userId : String, documentId : String) : Flow<Boolean>
    fun switchUserToOnline(userId : String, documentId : String) : Flow<Boolean>
    fun liveChangesListener(documentId : String) : Flow<ChildEventListener>

}