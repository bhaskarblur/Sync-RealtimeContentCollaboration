package com.bhaskarblur.sync_realtimecontentwriting.domain.model

import javax.annotation.concurrent.Immutable

data class ContentModel(
    val documentId : String?,
    var content : String?,
    val changedBy : String? = ""
)
