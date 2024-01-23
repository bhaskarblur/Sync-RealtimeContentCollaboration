package com.bhaskarblur.sync_realtimecontentwriting.domain.model

import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

data class TextSizeModel(
    val label : String = "16.sp",
    val size : TextUnit = 16.sp
)