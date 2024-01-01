package com.bhaskarblur.sync_realtimecontentwriting.domain.model

import javax.annotation.concurrent.Immutable

@Immutable
data class UserModel(
    val id : String? = "",
    val userName : String?,
    val fullName : String?
)