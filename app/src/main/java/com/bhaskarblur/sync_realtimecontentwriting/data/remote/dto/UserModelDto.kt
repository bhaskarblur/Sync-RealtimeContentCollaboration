package com.bhaskarblur.sync_realtimecontentwriting.data.remote.dto

import com.bhaskarblur.sync_realtimecontentwriting.domain.model.UserModel
import javax.annotation.concurrent.Immutable

@Immutable
data class UserModelDto(
    var id : String? = "",
    var userName : String? = "",
    var fullName : String? = "",
    var password : String? = ""
) {

    fun toUserModel() : UserModel {
        return UserModel(id, userName, fullName)
    }

    companion object {
        fun fromUserModel(user: UserModel): UserModelDto {
            return UserModelDto(
                user.id, user.userName, user.fullName
            )
        }
    }
}