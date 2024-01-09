package com.bhaskarblur.sync_realtimecontentwriting.presentation

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.bhaskarblur.sync_realtimecontentwriting.presentation.Registration.SignUpPage
import com.bhaskarblur.sync_realtimecontentwriting.presentation.Registration.SignUpViewModel
import com.bhaskarblur.sync_realtimecontentwriting.ui.theme.backgroundColor
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterActivity : ComponentActivity() {
    private lateinit var userViewModel: SignUpViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userViewModel = viewModels<SignUpViewModel>().value
        var loggedData by userViewModel.userState
        setContent {
            LaunchedEffect(key1 = true) {
                userViewModel.isUserLogged()
                loggedData = userViewModel.userState.value
                Log.d("user", userViewModel.userState.value.toString())
            }

            Column(
                Modifier
                    .fillMaxSize()
                    .background(backgroundColor),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SignUpPage(viewModel = userViewModel)
            }
        }
    }
}
