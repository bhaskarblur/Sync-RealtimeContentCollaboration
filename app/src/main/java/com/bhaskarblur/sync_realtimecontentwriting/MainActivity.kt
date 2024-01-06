package com.bhaskarblur.sync_realtimecontentwriting
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.bhaskarblur.sync_realtimecontentwriting.presentation.document.DocumentPage
import com.bhaskarblur.sync_realtimecontentwriting.presentation.document.DocumentViewModel
import com.bhaskarblur.sync_realtimecontentwriting.presentation.signUp.SignUpPage
import com.bhaskarblur.sync_realtimecontentwriting.presentation.signUp.SignUpViewModel
import com.bhaskarblur.sync_realtimecontentwriting.ui.theme.backgroundColor
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var documentViewModel : DocumentViewModel
    private lateinit var userViewModel : SignUpViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userViewModel = viewModels<SignUpViewModel>().value
        documentViewModel = viewModels<DocumentViewModel>().value
        setContent {
            val scaffoldState = remember { SnackbarHostState() }
            var loggedData by userViewModel.userState
            val context = LocalContext.current

            LaunchedEffect(key1 = true) {
                userViewModel.isUserLogged()
                documentViewModel.initDocument("Playground")
                documentViewModel.eventFlow.collectLatest { event ->
                    when (event) {
                        is DocumentViewModel.UIEvents.ShowSnackbar -> {
                            scaffoldState.showSnackbar(message = event.message)
                        }
                    }
                }
                loggedData = userViewModel.userState.value
                Log.d("user", userViewModel.userState.value.toString())
            }
            Scaffold(
                snackbarHost = { SnackbarHost(hostState = scaffoldState) }) {
                it
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(backgroundColor)

                ) {
                    if (!loggedData.id.isNullOrEmpty()) {
                        DocumentPage(documentViewModel,
                            userViewModel, context)
                    } else {
                        SignUpPage(viewModel = userViewModel)
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("resumed","Yes")
        if(userViewModel.isUserLogged()) {
            documentViewModel.switchUserOn()
        }
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
