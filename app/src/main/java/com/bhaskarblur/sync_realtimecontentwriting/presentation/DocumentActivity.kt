package com.bhaskarblur.sync_realtimecontentwriting.presentation

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.bhaskarblur.sync_realtimecontentwriting.presentation.document.DocumentPage
import com.bhaskarblur.sync_realtimecontentwriting.presentation.document.DocumentViewModel
import com.bhaskarblur.sync_realtimecontentwriting.presentation.document.ui.theme.SyncRealtimeContentWritingTheme
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
