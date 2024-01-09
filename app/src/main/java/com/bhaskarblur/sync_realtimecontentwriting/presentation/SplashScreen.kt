package com.bhaskarblur.sync_realtimecontentwriting.presentation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.bhaskarblur.sync_realtimecontentwriting.R
import com.bhaskarblur.sync_realtimecontentwriting.presentation.Registration.SignUpViewModel
import com.bhaskarblur.sync_realtimecontentwriting.ui.theme.backgroundColor
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

@AndroidEntryPoint
class SplashScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val userViewModel by viewModels<SignUpViewModel>()
            val loggedData by userViewModel.userState

            LaunchedEffect(loggedData) {
                userViewModel.initData()
                delay(2000)
                Log.d("userData__", loggedData.userName.toString())
                if(!loggedData.id.isNullOrEmpty()) {
                    startActivity(Intent(this@SplashScreen, MainActivity::class.java))
                }
                else {
                    startActivity(Intent(this@SplashScreen, RegisterActivity::class.java))
                }
                finish()
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
            }
            Column(
                Modifier
                    .fillMaxSize()
                    .background(backgroundColor),
                verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {

                Image(painter = painterResource(id = R.drawable.logo), contentDescription = "logo",
                    Modifier
                        .width(196.dp)
                        .height(196.dp))
            }
        }
    }
}