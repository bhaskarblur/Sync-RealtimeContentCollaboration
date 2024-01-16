package com.bhaskarblur.sync_realtimecontentwriting.presentation.appActivities

import android.content.Intent
import android.net.Uri
import android.util.Log

object ActivityHelper {

    fun showDeepLinkDocument(appLinkAction: String?, appLinkData: Uri?): String {
        var docCode = ""
        if (Intent.ACTION_VIEW == appLinkAction && appLinkData != null) {
            // 2
            val documentCode = appLinkData.getQueryParameter("code")
            if (documentCode.isNullOrBlank().not()) {
                docCode = documentCode?:""
                Log.d("validDeepLinkUrl", documentCode.toString())
            }
        }
        return docCode

    }
}