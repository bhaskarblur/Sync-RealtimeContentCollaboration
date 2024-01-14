package com.bhaskarblur.sync_realtimecontentwriting.presentation

sealed class Screens(val route: String) {
    object RegistrationPage : Screens("RegistationPage")
    object LoginPage : Screens("LoginPage")
    object HomePage : Screens("HomePage")
    object SearchDocumentPage : Screens("SearchDocumentPage")

}