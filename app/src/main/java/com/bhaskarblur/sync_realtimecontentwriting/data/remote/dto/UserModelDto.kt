package com.bhaskarblur.sync_realtimecontentwriting.data.remote.dto

import com.bhaskarblur.sync_realtimecontentwriting.domain.model.UserModel
import javax.annotation.concurrent.Immutable

@Immutable
data class UserModelDto(
    val id : String? = "",
    val userName : String? = "",
    val fullName : String? = ""
) {

    fun toUserModel() : UserModel {
        return UserModel(id, userName, fullName)
    }
}