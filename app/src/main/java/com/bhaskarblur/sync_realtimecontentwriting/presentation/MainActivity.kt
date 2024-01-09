package com.bhaskarblur.sync_realtimecontentwriting.presentation
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.bhaskarblur.sync_realtimecontentwriting.presentation.document.DocumentPage
import com.bhaskarblur.sync_realtimecontentwriting.presentation.document.DocumentViewModel
import com.bhaskarblur.sync_realtimecontentwriting.presentation.Registration.SignUpPage
import com.bhaskarblur.sync_realtimecontentwriting.ui.theme.backgroundColor
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var documentViewModel : DocumentViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        documentViewModel = viewModels<DocumentViewModel>().value
        setContent {
            val scaffoldState = remember { SnackbarHostState() }
            val context = LocalContext.current
            Scaffold(
                snackbarHost = { SnackbarHost(hostState = scaffoldState) }) {
                it
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(backgroundColor)

                ) {
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("resumed","Yes")
    }

    override fun onDestroy() {
        super.onDestroy()
        documentViewModel.switchUserOff()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        documentViewModel.switchUserOff()
    }
    override fun onStop() {
        super.onStop()
        Log.d("onStop","Yes")
        documentViewModel.switchUserOff()

    }
}
