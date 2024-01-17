package com.bhaskarblur.sync_realtimecontentwriting.domain.model

import android.provider.ContactsContract.CommonDataKinds.Email
import javax.annotation.concurrent.Immutable

@Immutable
data class UserModel(
    val id : String? = "",
    var userName : String? = "",
    var fullName : String? = "",
    var userEmail: String?="",
    var userPicture:String?=""
)