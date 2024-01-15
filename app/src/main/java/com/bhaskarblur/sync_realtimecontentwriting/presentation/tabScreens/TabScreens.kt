package com.bhaskarblur.sync_realtimecontentwriting.presentation.tabScreens

sealed class TabScreens(val route: String) {
    object HomePage : TabScreens("HomePage")
    object ProfilePage : TabScreens("ProfilePage")
    object ExplorePage : TabScreens("ExplorePage")
    object SearchDocumentPage : TabScreens("SearchDocumentPage")

    object NoInternet : TabScreens("NoInternetPage")
}