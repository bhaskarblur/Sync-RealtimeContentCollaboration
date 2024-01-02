package com.bhaskarblur.sync_realtimecontentwriting.data.remote.dto

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import com.bhaskarblur.sync_realtimecontentwriting.domain.model.UserModelCursor

@Stable
data class UserModelCursorDto(
    val userDetails : UserModelDto? = UserModelDto("","",""),
    val color : Int = 0,
    val position : Int? = 0
) {

    fun toUserModelCursor() : UserModelCursor {
        return UserModelCursor(userDetails?.toUserModel(), Color(color), position)
    }
}