package com.bhaskarblur.sync_realtimecontentwriting.domain.repository

import com.bhaskarblur.dictionaryapp.core.utils.Resources
import com.bhaskarblur.sync_realtimecontentwriting.domain.model.ContentModel
import kotlinx.coroutines.flow.Flow

interface IDocumentAction {

    fun notifyContentChanges(changeType : EventType ) : Flow<Resources<ContentModel>>
}

enum class EventType {
    USER_ADDED_IN_DOCUMENT, USER_REMOVED_FROM_DOCUMENT, CONTENT_CHANGED, POSITION_CHANGED
}