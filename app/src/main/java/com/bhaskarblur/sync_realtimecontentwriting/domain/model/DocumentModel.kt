package com.bhaskarblur.sync_realtimecontentwriting.domain.model

data class DocumentModel(
    val documentId : String?,
    val content : ContentModel?,
    val liveCollaborators : List<UserModelCursor>?
)