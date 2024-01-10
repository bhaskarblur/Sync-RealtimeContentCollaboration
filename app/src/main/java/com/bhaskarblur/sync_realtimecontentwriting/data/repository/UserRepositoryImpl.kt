package com.bhaskarblur.sync_realtimecontentwriting.data.repository

import android.util.Log
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
    private val spManager: SharedPreferencesManager
): IUserRepository {
    override fun signupUser(userName: String, fullName: String): Flow<UserModel> = flow {
        val user = firebaseManager.createUser(userName, fullName)

        if(!user.id.isNullOrEmpty()) {
            Log.d("FirebaseUserToBeStored", user.toString())
            spManager.storeSession(UserModelDto(user.id, user.userName, user.fullName))
            emit(user)
        }
        else {
            emit(UserModel(null, null, null))
        }

    }.flowOn(Dispatchers.IO)

    override fun getUserDetails(): Flow<UserModel>  = flow {
        val userDetails = spManager.getSession().toUserModel()
        if(!userDetails.id.isNullOrEmpty()) {
            val checkUserInDB = firebaseManager.getUserById(userDetails.id)
            Log.d("checkUserInDB", checkUserInDB.toString());
            if(!checkUserInDB.id.isNullOrBlank()) {
                emit(checkUserInDB)
                spManager.storeSession(UserModelDto.fromUserModel(checkUserInDB))
                return@flow
            }
            else {
                emit(checkUserInDB)
                return@flow
            }
        }
        emit(userDetails)
    }.flowOn(Dispatchers.IO)

    override fun logOutUser(): Flow<Boolean> = flow {
        spManager.logOutSession()
        Log.d("userLoggedOutRepo", "true")
    }
}