package com.bhaskarblur.sync_realtimecontentwriting.presentation.signUp

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bhaskarblur.sync_realtimecontentwriting.domain.model.UserModel
import com.bhaskarblur.sync_realtimecontentwriting.domain.use_case.UserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val userUseCase: UserUseCase
) : ViewModel() {

    private var _userState = mutableStateOf(UserModel())
    var userState = _userState

    fun signUpUser(userName: String, fullName : String) {
        viewModelScope.launch {
            userUseCase.createUser(userName, fullName).collectLatest { user ->
                if(!user.id.isNullOrEmpty()) {
                    _userState.value = user
                }
            }
        }
    }

    fun isUserLogged() : Boolean {
        var flag = false
        viewModelScope.launch {
            userUseCase.getUserDetails().collectLatest { user ->
                Log.d("userData", user.toString())
                if(user.id != null) {
                    if(user.id.isNotBlank()) {
                        flag = true
                        _userState.value = user
                    }
                }
            }
        }
        Log.d("isLogged", flag.toString())
        return flag
    }

}