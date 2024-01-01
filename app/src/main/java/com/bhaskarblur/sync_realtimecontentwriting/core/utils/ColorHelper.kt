package com.bhaskarblur.sync_realtimecontentwriting.core.utils

import androidx.compose.ui.graphics.Color
import kotlin.random.Random

object ColorHelper {

    fun generateColor() : Color {
        val random = Random
        val red = random.nextInt(256)
        val green = random.nextInt(256)
        val blue = random.nextInt(256)

        return Color(red, green, blue)
    }
}