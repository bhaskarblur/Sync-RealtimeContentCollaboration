package com.bhaskarblur.sync_realtimecontentwriting.presentation.signUp

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
    var userState : State<UserModel> = mutableStateOf(UserModel())

    fun signUpUser(userName: String, fullName : String) {
        viewModelScope.launch {
            userUseCase.createUser(userName, fullName).collectLatest { user ->
                user.id?.let {
                    _userState.value = user
                }
            }
        }
    }

    fun isUserLogged() : Boolean {
        var flag = false
        viewModelScope.launch {
            userUseCase.getUserDetails().collectLatest {

                if(it.id != null) {
                    if(it.id.isNotBlank()) {
                        flag = true
                    }
                }
            }
        }
        return flag
    }

}