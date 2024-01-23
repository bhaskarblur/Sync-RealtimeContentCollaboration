package com.bhaskarblur.sync_realtimecontentwriting.domain.model

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.GenericFontFamily
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import javax.annotation.concurrent.Immutable

@Immutable
data class TextSizeModel(
    val label : String = "16.sp",
    val size : TextUnit = 16.sp
)
@Immutable
data class FontFamilyModel(
    val label : String = "Default",
    val fontFamily: FontFamily = FontFamily.Default
)