package com.bhaskarblur.sync_realtimecontentwriting.data.remote.dto
import com.bhaskarblur.sync_realtimecontentwriting.domain.model.CommentsModel
import com.bhaskarblur.sync_realtimecontentwriting.domain.model.DocumentModel
import com.bhaskarblur.sync_realtimecontentwriting.domain.model.PromptModel

data class DocumentModelDto(
    val documentId: String = "",
    val documentName: String = "",
    val createdBy : String? ="",
    val creationDateTime : Long? = 0,
    var content: ContentModelDto? = ContentModelDto(null, null),
    val liveCollaborators: List<UserModelCursorDto>? = listOf(),
    val promptsList: ArrayList<PromptModel> = arrayListOf(),
    val commentsList: ArrayList<CommentsModel> = arrayListOf()
) {

    fun toDocumentModel() : DocumentModel {
        return DocumentModel(documentId, documentName, content?.toContentModel(), createdBy?:"", creationDateTime?:0, liveCollaborators?.map {  it.toUserModelCursor()},
            PromptModelDto.listToArrayList(promptsList))
    }
}

data class DocumentModelDtoNoList(
    val documentId : String = "",
    val documentName : String = "",
    val createdBy : String? ="",
    val creationDateTime : Long? = 0,
    val content : ContentModelDto? = ContentModelDto(null, null),
    val liveCollaborators : Any? = Any(),
    val promptsList : Any? = Any(),
    val commentsList : Any? = Any()
)