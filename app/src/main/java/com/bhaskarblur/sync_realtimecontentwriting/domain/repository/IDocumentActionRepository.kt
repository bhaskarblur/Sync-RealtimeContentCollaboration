package com.bhaskarblur.sync_realtimecontentwriting.domain.repository

import com.bhaskarblur.dictionaryapp.core.utils.Resources
import com.bhaskarblur.sync_realtimecontentwriting.domain.model.ContentModel
import kotlinx.coroutines.flow.Flow

interface IDocumentActionRepository {

    fun notifyContentChanges() : Flow<Resources<ContentModel>>
}