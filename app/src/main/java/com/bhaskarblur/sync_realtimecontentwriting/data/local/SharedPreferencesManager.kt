package com.bhaskarblur.sync_realtimecontentwriting.data.local

import android.R
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.bhaskarblur.sync_realtimecontentwriting.data.remote.dto.UserModelDto
import javax.inject.Inject


class SharedPreferencesManager @Inject constructor(
    private val context : Context
) {

    private lateinit var sharedPreferences: SharedPreferences

    init {
        sharedPreferences = context.getSharedPreferences("userData", 0)
    }
    fun storeSession(userData : UserModelDto) {
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString("id", userData.id)
        editor.putString("userName", userData.userName)
        editor.putString("fullName", userData.fullName)
        editor.apply()
    }

    fun getSession() : UserModelDto {
      val id = sharedPreferences.getString("id","")?: ""
      val userName = sharedPreferences.getString("userName","")
      val fullName = sharedPreferences.getString("fullName","")
        return UserModelDto(
            id, userName, fullName
        )
    }
}