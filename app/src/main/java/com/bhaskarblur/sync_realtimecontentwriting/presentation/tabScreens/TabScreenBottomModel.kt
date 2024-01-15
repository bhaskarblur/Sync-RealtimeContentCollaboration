package com.bhaskarblur.sync_realtimecontentwriting.presentation.tabScreens

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.vector.ImageVector

@Stable
data class TabScreenBottomModel(
    val label : String,
    val route : String,
    val image : ImageVector?,
)