package com.bhaskarblur.sync_realtimecontentwriting.core.utils

import com.bhaskarblur.sync_realtimecontentwriting.domain.model.PromptModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object UiUtils {

    fun getDateTime(s: String): String? {
        return try {
            val sdf: SimpleDateFormat = SimpleDateFormat("MMM dd, hh:mm", Locale.getDefault())
            val netDate = Date(s.toLong())
            sdf.format(netDate)
        } catch (e: Exception) {
            e.toString()
        }
    }

}