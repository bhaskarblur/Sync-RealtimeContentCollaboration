package com.bhaskarblur.sync_realtimecontentwriting.core.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import kotlin.random.Random

object ColorHelper {

    fun generateColor() : Int {
        val random = Random
        val red = random.nextInt(256)
        val green = random.nextInt(256)
        val blue = random.nextInt(256)

        return Color(red, green, blue).toArgb()
    }
}