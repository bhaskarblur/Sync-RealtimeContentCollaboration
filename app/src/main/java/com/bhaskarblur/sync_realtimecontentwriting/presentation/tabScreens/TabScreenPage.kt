package com.bhaskarblur.sync_realtimecontentwriting.presentation.tabScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.BottomNavigation
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bhaskarblur.sync_realtimecontentwriting.presentation.document.DocumentViewModel
import com.bhaskarblur.sync_realtimecontentwriting.presentation.tabScreens.documentsHome.DocumentsList
import com.bhaskarblur.sync_realtimecontentwriting.presentation.tabScreens.documentsHome.SearchDocumentsPage
import com.bhaskarblur.sync_realtimecontentwriting.presentation.tabScreens.profile.ProfilePage
import com.bhaskarblur.sync_realtimecontentwriting.presentation.registration.SignUpViewModel
import com.bhaskarblur.sync_realtimecontentwriting.presentation.widgets.NoInternetPage
import com.bhaskarblur.sync_realtimecontentwriting.ui.theme.backgroundColor
import com.bhaskarblur.sync_realtimecontentwriting.ui.theme.colorSecondary
import com.bhaskarblur.sync_realtimecontentwriting.ui.theme.primaryColor
import com.bhaskarblur.sync_realtimecontentwriting.ui.theme.textColorPrimary
import com.bhaskarblur.sync_realtimecontentwriting.ui.theme.textColorSecondary

@Composable
fun TabScreenPage(
    parentNavController: NavController,
    userViewModel: SignUpViewModel,
    documentViewModel: DocumentViewModel
) {
    val context = LocalContext.current
    val scaffoldState = remember { SnackbarHostState() }
    val navController = rememberNavController()
    val currentPage = rememberSaveable {
        mutableStateOf<String>(TabScreens.HomePage.route)
    }
    val bottomItemsList = listOf<TabScreenBottomModel>(
        TabScreenBottomModel("Home", TabScreens.HomePage.route, Icons.Filled.Home),
        TabScreenBottomModel("Explore", TabScreens.ExplorePage.route, Icons.Filled.Explore),
        TabScreenBottomModel("Profile", TabScreens.ProfilePage.route, Icons.Filled.AccountCircle),
    )

    val networkAvailable by userViewModel.isInternetAvailable

    navController.addOnDestinationChangedListener { _, _, _ -> userViewModel.checkNetworkAvailable() }

    LaunchedEffect(networkAvailable) {
        if (userViewModel.checkNetworkAvailable()) {
            currentPage.value = TabScreens.HomePage.route
        } else {
            currentPage.value = TabScreens.NoInternet.route
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = scaffoldState) },
        bottomBar = {
            BottomNavigation(
                backgroundColor = colorSecondary,
                elevation = 4.dp,
                modifier = Modifier.height(62.dp)
            ) {
                bottomItemsList.forEachIndexed { index, navItem ->
                    NavigationBarItem(selected = when (navItem.route) {
                        currentPage.value -> true
                        else -> false
                    },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = colorSecondary
                        ),
                        onClick = {
                            if (navItem.route == TabScreens.HomePage.route) {
                                navController.popBackStack()
                            }
                            currentPage.value = navItem.route
                            navController.navigate(currentPage.value)

                    }, icon = {
                        Column(verticalArrangement = Arrangement.SpaceBetween,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .width((LocalConfiguration.current.screenWidthDp / 3).dp)
                        ) {
                            Icon(
                                navItem.image!!, contentDescription = "", Modifier.size(22.dp),
                                tint = when (navItem.route == currentPage.value) {
                                    true -> textColorPrimary
                                    else -> textColorSecondary
                                }
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                navItem.label,
                                fontWeight = FontWeight.Medium,
                                fontSize = 12.sp,
                                color = when (navItem.route == currentPage.value) {
                                    true -> textColorPrimary
                                    else -> textColorSecondary
                                }
                            )
                        }
                    })
                }
            }
        }) {
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
                        route = TabScreens.HomePage.route
                    ) {
                        DocumentsList(
                            userViewModel, documentViewModel,
                            context, navController
                        )
                    }

                    composable(
                        route = TabScreens.ExplorePage.route
                    ) {
                    }

                    composable(
                        route = TabScreens.ProfilePage.route
                    ) {
                        ProfilePage(userViewModel, documentViewModel)
                    }

                    composable(
                        route = TabScreens.SearchDocumentPage.route
                    ) {
                        SearchDocumentsPage(documentViewModel, navController, context)
                    }



                    composable(
                        route = TabScreens.NoInternet.route
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