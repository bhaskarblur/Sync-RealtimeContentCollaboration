package com.bhaskarblur.sync_realtimecontentwriting.data.repository

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

        if(user.id != null) {
            spManager.storeSession(UserModelDto(user.id, user.userName, user.fullName))
            emit(user)
        }

        emit(UserModel(null, null, null))

    }.flowOn(Dispatchers.IO)

    override fun getUserDetails(): Flow<UserModel>  = flow {
        emit(spManager.getSession().toUserModel())
    }
}