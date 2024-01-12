package com.bhaskarblur.sync_realtimecontentwriting.domain.repository

import com.bhaskarblur.dictionaryapp.core.utils.Resources
import com.bhaskarblur.sync_realtimecontentwriting.data.remote.dto.UserModelDto
import com.bhaskarblur.sync_realtimecontentwriting.domain.model.UserModel
import kotlinx.coroutines.flow.Flow

interface IUserRepository {

    fun signupUser(userName : String, fullName : String, password : String) : Flow<UserModel>

    fun loginUser(userName : String, password : String) : Flow<UserModel>
    fun getUserDetails() : Flow<UserModel>

    fun logOutUser() : Flow<Boolean>
}