package com.bhaskarblur.sync_realtimecontentwriting.data.remote.dto

import com.bhaskarblur.sync_realtimecontentwriting.domain.model.DocumentModel

data class DocumentModelDto(
    val documentId : String,
    val content : ContentModelDto?,
    val liveCollaborators : List<UserModelCursorDto>?
) {

    fun toDocumentModel() : DocumentModel {
        return DocumentModel(documentId, content?.toContentModel(), liveCollaborators?.map {  it.toUserModelCursor()})
    }
}