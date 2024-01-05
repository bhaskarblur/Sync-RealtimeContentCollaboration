package com.bhaskarblur.sync_realtimecontentwriting.data.remote.dto

import com.bhaskarblur.sync_realtimecontentwriting.domain.model.ContentModel

data class ContentModelDto(
    val documentId : String? = "",
    val content : String? = "",
    val lastEditedBy : String? = ""
) {

    fun toContentModel() : ContentModel {
        return ContentModel(documentId, content, lastEditedBy)
    }
}
