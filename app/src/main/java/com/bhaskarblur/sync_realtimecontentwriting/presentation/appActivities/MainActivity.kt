package com.bhaskarblur.sync_realtimecontentwriting.presentation.appActivities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bhaskarblur.sync_realtimecontentwriting.core.utils.Constants
import com.bhaskarblur.sync_realtimecontentwriting.presentation.Screens
import com.bhaskarblur.sync_realtimecontentwriting.presentation.UIEvents
import com.bhaskarblur.sync_realtimecontentwriting.presentation.document.DocumentViewModel
import com.bhaskarblur.sync_realtimecontentwriting.presentation.registration.LoginPage
import com.bhaskarblur.sync_realtimecontentwriting.presentation.registration.SignUpPage
import com.bhaskarblur.sync_realtimecontentwriting.presentation.registration.SignUpViewModel
import com.bhaskarblur.sync_realtimecontentwriting.presentation.tabScreens.TabScreenPage
import com.bhaskarblur.sync_realtimecontentwriting.presentation.widgets.NoInternetPage
import com.bhaskarblur.sync_realtimecontentwriting.ui.theme.backgroundColor
import com.bhaskarblur.sync_realtimecontentwriting.ui.theme.primaryColor
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterIsInstance
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var documentViewModel: DocumentViewModel
    private lateinit var userViewModel: SignUpViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userViewModel = viewModels<SignUpViewModel>().value
        val loggedData by userViewModel.userState
        val networkAvailable by userViewModel.isInternetAvailable
        val data: Uri? = intent?.data
        setContent {
            val context = LocalContext.current
            val scaffoldState = remember { SnackbarHostState() }
            val currentPage = rememberSaveable {
                mutableStateOf("")
            }
            val navController = rememberNavController()

            LaunchedEffect(userViewModel.event.value) {
                if (userViewModel.event.value.isNotEmpty()) {
                    Toast.makeText(context, userViewModel.event.value, Toast.LENGTH_SHORT).show()
                    userViewModel.event.value = ""
                }
            }
            LaunchedEffect(networkAvailable, loggedData) {
                if (userViewModel.checkNetworkAvailable()) {
                    userViewModel.initData()
                    delay(500)
                    Log.d("userData__", loggedData.id.isNullOrEmpty().toString())
                    if (!loggedData.id.isNullOrEmpty()) {
                        documentViewModel.setUser()
                        currentPage.value = Screens.TabScreen.route

                    } else {
                        currentPage.value = Screens.RegistrationPage.route
                    }
                } else {
                    currentPage.value = Screens.NoInternet.route
                }
            }

            LaunchedEffect(documentViewModel.eventFlow) {
                Log.d("valueChange", "yes")

                documentViewModel.eventFlow
                    .filterIsInstance<UIEvents.ShareDocument>()
                    .collectLatest { documentShared ->
                        documentShared.documentId.let { documentId ->
                            val sendIntent = Intent()
                            sendIntent.setAction(Intent.ACTION_SEND)
                            sendIntent.putExtra(
                                Intent.EXTRA_TEXT,
                                "Hey there! I invite you to collaborate with me in my content creation on Sync App: ${
                                    Constants.appDeepLinkUrl.plus(
                                        documentId
                                    )
                                }"
                            )
                            sendIntent.setType("text/plain")
                            startActivity(Intent.createChooser(sendIntent,"Share Document"))
                            documentViewModel.emitUIEvent(UIEvents.DefaultState())
                        }

                    }

                documentViewModel.eventFlow
                    .filterIsInstance<UIEvents.DocumentCreated>()
                    .collectLatest { documentCreated ->
                        documentCreated.documentId.let { documentId ->
                            val intent = Intent(context, DocumentActivity::class.java)
                            intent.putExtra("documentId", documentId)
                            context.startActivity(intent)
                        }

                    }

            }

            data?.let {
                if(!userViewModel.userState.value.id.isNullOrEmpty()) {
                    Log.d("receivedDeepLink", it.toString())
                }
            }
            Scaffold(
                snackbarHost = { SnackbarHost(hostState = scaffoldState) }) {
                it
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(backgroundColor)

                ) {
                    if (currentPage.value.isNotEmpty()) {
                        NavHost(
                            navController = navController,
                            startDestination = currentPage.value,
                        ) {

                            composable(
                                route = Screens.RegistrationPage.route
                            ) {
                                SignUpPage(userViewModel, navController, context)
                            }
                            composable(
                                route = Screens.LoginPage.route
                            ) {
                                LoginPage(userViewModel, navController)
                            }

                            composable(
                                route = Screens.TabScreen.route
                            ) {
                                TabScreenPage(navController, userViewModel, documentViewModel)
                            }

                            composable(
                                route = Screens.NoInternet.route
                            ) {
                                NoInternetPage {
                                    userViewModel.checkNetworkAvailable()
                                }

                            }
                        }
                    } else {
                        Column(
                            Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(
                                color = primaryColor,
                                modifier = Modifier.size(42.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
