package com.bhaskarblur.sync_realtimecontentwriting.presentation.registration

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bhaskarblur.sync_realtimecontentwriting.domain.model.UserModel
import com.bhaskarblur.sync_realtimecontentwriting.domain.use_case.UserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val userUseCase: UserUseCase
) : ViewModel() {

    private var _userState = mutableStateOf(UserModel())
    val userState = _userState
    val event = mutableStateOf("")

    suspend fun initData() : Boolean{
        var logged = false
        runBlocking {
            userUseCase.getUserDetails().collectLatest { user ->
                Log.d("userData", user.toString())
                if(user.id != null) {
                    if(user.id.isNotBlank()) {
                        _userState.value = user
                        logged = true
                    }
                }
            }
        }
        return logged
    }

    fun signUpUser(userName: String, fullName : String, password : String) {
        viewModelScope.launch {
            userUseCase.createUser(userName, fullName, password).collectLatest { user ->
                if(!user.id.isNullOrEmpty()) {
                    _userState.value = user
                    event.value = ""
                }
                else {
                    event.value = "This username already exists! Please use a different username!"
                }
            }
        }
    }

    fun loginUser(userName: String,password : String) {
        viewModelScope.launch {
            userUseCase.loginUser(userName, password).collectLatest { user ->
                if(!user.id.isNullOrEmpty()) {
                    _userState.value = user
                    event.value = ""
                }
                else {
                    event.value = "Incorrect username or password! Please try again"
                }
            }
        }
    }
    fun logOutUser() {
        viewModelScope.launch {
            userUseCase.logOutUser()
            userState.value = UserModel()
            event.value = "Logged out!"
            Log.d("UserLoggedOut", "true")
        }
    }

}