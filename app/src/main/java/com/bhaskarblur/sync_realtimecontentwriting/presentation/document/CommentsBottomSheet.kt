package com.bhaskarblur.sync_realtimecontentwriting.presentation.document

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.bhaskarblur.sync_realtimecontentwriting.domain.model.DocumentModel

@Composable
fun CommentsBottomSheet(
    viewModel: DocumentViewModel, data: DocumentModel, onAddComment: (msg: String) -> Unit,
    onDeleteComment: () -> Unit
) {

    val ctnScope = rememberCoroutineScope()

    val commentsScrollState = rememberLazyListState()
}
