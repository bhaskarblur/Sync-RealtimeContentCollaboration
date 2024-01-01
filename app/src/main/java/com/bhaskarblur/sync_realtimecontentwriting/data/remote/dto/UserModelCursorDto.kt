package com.bhaskarblur.sync_realtimecontentwriting.data.remote.dto

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import com.bhaskarblur.sync_realtimecontentwriting.domain.model.UserModelCursor

@Stable
data class UserModelCursorDto(
    val userDetails : UserModelDto,
    val color : Color,
    val position : Int?
) {

    fun toUserModelCursor() : UserModelCursor {
        return UserModelCursor(userDetails.toUserModel(), color, position)
    }
}