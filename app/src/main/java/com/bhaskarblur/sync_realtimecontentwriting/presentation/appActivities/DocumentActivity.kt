package com.bhaskarblur.sync_realtimecontentwriting.presentation.appActivities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.coroutineScope
import com.bhaskarblur.sync_realtimecontentwriting.core.utils.Constants
import com.bhaskarblur.sync_realtimecontentwriting.presentation.UIEvents
import com.bhaskarblur.sync_realtimecontentwriting.presentation.document.DocumentPage
import com.bhaskarblur.sync_realtimecontentwriting.presentation.document.DocumentViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class DocumentActivity : ComponentActivity() {

    @Inject
    lateinit var documentViewModel: DocumentViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val documentId = intent.getStringExtra("documentId").toString()
        val applyDocCheck = intent.getBooleanExtra("applyDocCheck", false)
        lifecycle.coroutineScope.launch {
            if (applyDocCheck) {
                val isValidCode = documentViewModel.getDocumentById(documentId)
                if (!isValidCode) {
                    Toast.makeText(
                        this@DocumentActivity,
                        "Invalid Code! Try again or check your internet.", Toast.LENGTH_SHORT
                    )
                        .show()
                    finish()
                } else {
                    documentViewModel.getDocumentData(documentId)
                }
            }
            else {
                documentViewModel.getDocumentData(documentId)
            }
        }

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
