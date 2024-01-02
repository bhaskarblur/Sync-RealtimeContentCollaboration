package com.bhaskarblur.sync_realtimecontentwriting.data.remote.dto

import com.bhaskarblur.sync_realtimecontentwriting.domain.model.DocumentModel

data class DocumentModelDto(
    val documentId : String = "",
    val documentName : String = "",
    val content : ContentModelDto? = ContentModelDto(null, null),
    val liveCollaborators : List<UserModelCursorDto>? = listOf()
) {

    fun toDocumentModel() : DocumentModel {
        return DocumentModel(documentId, documentName, content?.toContentModel(), liveCollaborators?.map {  it.toUserModelCursor()})
    }
}

data class DocumentModelDtoNoList(
    val documentId : String = "",
    val documentName : String = "",
    val content : ContentModelDto? = ContentModelDto(null, null),
    val liveCollaborators : Any? = Any()
)