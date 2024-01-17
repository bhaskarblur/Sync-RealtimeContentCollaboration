package com.bhaskarblur.sync_realtimecontentwriting.data.repository

import android.net.Uri
import android.util.Log
import com.bhaskarblur.sync_realtimecontentwriting.core.utils.PasswordUtil
import com.bhaskarblur.sync_realtimecontentwriting.data.local.SharedPreferencesManager
import com.bhaskarblur.sync_realtimecontentwriting.data.remote.FirebaseManager
import com.bhaskarblur.sync_realtimecontentwriting.data.remote.dto.UserModelDto
import com.bhaskarblur.sync_realtimecontentwriting.domain.model.UserModel
import com.bhaskarblur.sync_realtimecontentwriting.domain.repository.IUserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val firebaseManager: FirebaseManager,
    private val spManager: SharedPreferencesManager,
    private val passUtil : PasswordUtil
): IUserRepository {
    override fun signupUser(userName: String, fullName: String,userEmail:String, password : String,userPicture:Uri): Flow<UserModel> = flow {
        val hashedPass = passUtil.hashPassword(password)
        val user = firebaseManager.createUser(userName, fullName, userEmail,hashedPass,userPicture)
        if(!user.id.isNullOrEmpty()) {
            Log.d("FirebaseUserToBeStored", user.toString())
            spManager.storeSession(UserModelDto(user.id, user.userName, user.fullName,user.userEmail,user.userPicture))
            emit(user)
        }
        else {
            emit(UserModel(null, null, null,null,null))
        }

    }.flowOn(Dispatchers.IO)

    override fun loginUser(userName: String, password: String): Flow<UserModel> = flow{
        val user = firebaseManager.getUserByUserName(userName)
        val passIsCorrect = passUtil.verifyPassword(password, user.password!!)
        if(!user.id.isNullOrEmpty()) {
            if(passIsCorrect) {
                Log.d("firebasePasswordCorrect", user.toString())
                spManager.storeSession(UserModelDto(user.id, user.userName, user.fullName))
                emit(user.toUserModel())
            }
            else {
                emit(UserModel(null, null, null))
            }
        }
        else {
            emit(UserModel(null, null, null))
        }
    }.flowOn(Dispatchers.IO)

    override fun getUserDetails(): Flow<UserModel>  = flow {
        val userDetails = spManager.getSession().toUserModel()
        if(!userDetails.id.isNullOrEmpty()) {
            Log.d("checkUserInDB", userDetails.toString());
            if(!userDetails.id.isNullOrBlank()) {
                emit(userDetails)
                spManager.storeSession(UserModelDto.fromUserModel(userDetails))
                return@flow
            }
            else {
                emit(UserModel())
                return@flow
            }
        }
        emit(userDetails)
    }.flowOn(Dispatchers.IO)

    override fun logOutUser(): Flow<Boolean> {
        spManager.logOutSession()
        Log.d("userLoggedOutRepo", "true")
        return flow {
            emit(true)
    }
    }
}