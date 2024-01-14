package com.bhaskarblur.sync_realtimecontentwriting.presentation.appActivities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.bhaskarblur.sync_realtimecontentwriting.R
import com.bhaskarblur.sync_realtimecontentwriting.ui.theme.backgroundColor
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

@AndroidEntryPoint
class SplashScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LaunchedEffect(true) {
                delay(1000)
                startActivity(Intent(this@SplashScreen, MainActivity::class.java))
                finish()
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
            }
            Column(
                Modifier
                    .fillMaxSize()
                    .background(backgroundColor),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "logo",
                    Modifier
                        .width(196.dp)
                        .height(196.dp)
                )
            }
        }
    }
}