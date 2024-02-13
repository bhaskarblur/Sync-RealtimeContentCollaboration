package com.bhaskarblur.sync_realtimecontentwriting.domain.model

data class DocumentModel(
    val documentId : String?,
    var documentName : String?,
    var content : ContentModel?,
    val createdBy : String ="",
    val creationDateTime : Long = 0,
    val liveCollaborators : List<UserModelCursor>?,
    var promptsList : ArrayList<PromptModel>? = arrayListOf(),
    var commentsList: ArrayList<CommentsModel> = arrayListOf()
)