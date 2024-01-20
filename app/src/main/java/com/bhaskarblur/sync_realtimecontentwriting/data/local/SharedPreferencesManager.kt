package com.bhaskarblur.sync_realtimecontentwriting.data.local

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
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
        editor.putString("userEmail",userData.userEmail)
        editor.putString("userPicture",userData.userPicture)
        editor.apply()
        Log.d("pictureSaved", userData.userPicture.toString())
        Log.d("sessionStored", userData.toUserModel().toString())
    }

    fun logOutSession() {
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }
    fun getSession() : UserModelDto {
      val id = sharedPreferences.getString("id","")?: ""
      val userName = sharedPreferences.getString("userName","")
        val fullName = sharedPreferences.getString("fullName","")
        val email = sharedPreferences.getString("userEmail","")
        val picture = sharedPreferences.getString("userPicture","")
        Log.d("pictureGot", picture.toString())
        return UserModelDto(
            id, userName, fullName, email,
            "", picture
        )
    }
}