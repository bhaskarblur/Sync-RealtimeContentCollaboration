package com.bhaskarblur.sync_realtimecontentwriting.domain.model

import javax.annotation.concurrent.Immutable

data class ContentModel(
    var content : String?,
    val lastEditedBy : String? = ""
)
