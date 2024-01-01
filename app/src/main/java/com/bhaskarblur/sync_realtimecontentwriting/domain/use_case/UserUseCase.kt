package com.bhaskarblur.sync_realtimecontentwriting.domain.use_case

import com.bhaskarblur.sync_realtimecontentwriting.domain.model.UserModel
import com.bhaskarblur.sync_realtimecontentwriting.domain.repository.IUserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UserUseCase(
    private val userRepository: IUserRepository
) {

    fun createUser(userName : String, fullName : String) : Flow<UserModel> {
        return userRepository.signupUser(userName, fullName)
    }

    fun getUserDetails() : Flow<UserModel> {
        return userRepository.getUserDetails()
    }

}