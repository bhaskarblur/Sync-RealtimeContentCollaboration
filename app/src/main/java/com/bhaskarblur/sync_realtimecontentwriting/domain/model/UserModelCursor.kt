package com.bhaskarblur.sync_realtimecontentwriting.domain.model

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import javax.annotation.concurrent.Immutable

@Stable
data class UserModelCursor(
    val userDetails : UserModel,
    val color : Color,
    val position : Int?
)