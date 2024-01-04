package com.bhaskarblur.sync_realtimecontentwriting.domain.model

import com.bhaskarblur.sync_realtimecontentwriting.data.remote.dto.PromptModelDto

data class DocumentModel(
    val documentId : String?,
    val documentName : String?,
    var content : ContentModel?,
    val liveCollaborators : List<UserModelCursor>?,
    val promptsList : ArrayList<PromptModel>? = arrayListOf()
)