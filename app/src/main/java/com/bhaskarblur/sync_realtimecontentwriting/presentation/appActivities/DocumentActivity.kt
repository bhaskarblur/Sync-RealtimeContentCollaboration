package com.bhaskarblur.sync_realtimecontentwriting.presentation.appActivities

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.platform.LocalContext
import com.bhaskarblur.sync_realtimecontentwriting.presentation.document.DocumentPage
import com.bhaskarblur.sync_realtimecontentwriting.presentation.document.DocumentViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DocumentActivity : ComponentActivity() {

    @Inject
    lateinit var documentViewModel: DocumentViewModel
    var documentId = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        documentId = intent.getStringExtra("documentId").toString()
        documentViewModel.getDocumentData(documentId)
        setContent {
            DocumentPage(viewModel = documentViewModel, context = LocalContext.current)
        }
    }

    override fun onResume() {
        super.onResume()
        documentViewModel.switchUserOn()
    }
    override fun onDestroy() {
        super.onDestroy()
        documentViewModel.switchUserOff()
        Log.d("switchedOff","yes")
    }
}
