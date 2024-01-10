package com.bhaskarblur.sync_realtimecontentwriting.presentation.registration

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bhaskarblur.sync_realtimecontentwriting.domain.model.UserModel
import com.bhaskarblur.sync_realtimecontentwriting.domain.use_case.UserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val userUseCase: UserUseCase
) : ViewModel() {

    private var _userState = mutableStateOf(UserModel())
    var userState = _userState

    fun initData() {
        viewModelScope.launch {
            userUseCase.getUserDetails().collectLatest { user ->
                Log.d("userData", user.toString())
                if(user.id != null) {
                    if(user.id.isNotBlank()) {
                        _userState.value = user
                    }
                }
            }
        }
    }
    fun signUpUser(userName: String, fullName : String) {
        viewModelScope.launch {
            userUseCase.createUser(userName, fullName).collectLatest { user ->
                if(!user.id.isNullOrEmpty()) {
                    _userState.value = user
                }
            }
        }
    }

    fun logOutUser() {
        viewModelScope.launch {
            userUseCase.logOutUser()
            userState.value = UserModel()
            Log.d("UserLoggedOut", "true")
        }
    }

}