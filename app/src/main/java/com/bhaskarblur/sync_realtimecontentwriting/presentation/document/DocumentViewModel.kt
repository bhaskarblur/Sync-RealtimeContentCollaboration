package com.bhaskarblur.sync_realtimecontentwriting.presentation.document

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bhaskarblur.dictionaryapp.core.utils.Resources
import com.bhaskarblur.sync_realtimecontentwriting.domain.model.DocumentModel
import com.bhaskarblur.sync_realtimecontentwriting.domain.model.UserModel
import com.bhaskarblur.sync_realtimecontentwriting.domain.repository.IUserRepository
import com.bhaskarblur.sync_realtimecontentwriting.domain.use_case.DocumentUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Stack
import javax.inject.Inject

@HiltViewModel
class DocumentViewModel @Inject constructor(
    private val documentUseCase: DocumentUseCase,
    private val userRepo: IUserRepository
) : ViewModel() {

    private val _documentData = documentUseCase._documentDetails
    val documentData: State<DocumentModel> = _documentData
    private val _changeHistory = mutableListOf<String>()
    val undoStack: Stack<String> = Stack()
    val redoStack: Stack<String> = Stack()

    private val _eventFlow = MutableSharedFlow<UIEvents>()
    val eventFlow = _eventFlow

    private var updateJob: Job? = null

    private val userDetails = mutableStateOf(UserModel())

    fun initDocument(documentId: String) {
        viewModelScope.launch {
            userRepo.getUserDetails().collect {
                Log.d("userDataEmitted", it.toString())
                userDetails.value = it
            }
            delay(1000)
            getDocumentData(documentId)
            switchUserOn(userDetails.value.id!!, documentId)
        }

    }

    fun getDocumentData(documentId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            documentUseCase.getDocumentDetails(documentId, userDetails.value.id!!).collectLatest {
                when (it) {
                    is Resources.Success -> {
                        it.data?.let {
                            Log.d("dataDocument", it.toString())
                            _documentData.value = it
                        }
                    }

                    is Resources.Error -> {
                        _eventFlow.emit(UIEvents.ShowSnackbar(it.message ?: "There was an error"))
                    }

                    else -> {}
                }
            }
        }
    }

    fun switchUserOn(userId: String, documentId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            documentUseCase.switchUserToOnline(documentId, userId)
        }
    }

    fun switchUserOff() {
        viewModelScope.launch(Dispatchers.IO) {
            documentUseCase.switchUserToOffline(
                documentData.value.documentId!!,
                userDetails.value.id!!
            )
        }
    }

    fun updateContent(content: String, cursorPosition: Int) {
        updateJob?.cancel()
        updateJob = viewModelScope.launch {
            documentUseCase.updateContent(_documentData.value.documentId!!,
                content)
            documentUseCase.updateCursorPosition(
                documentId = _documentData.value.documentId!!,
                position = cursorPosition, userDetails.value.id!!
            )

        }
    }

    fun updateTitle(title: String) {
        updateJob?.cancel()
        updateJob = viewModelScope.launch {
            documentUseCase.updateDocumentTitle(
                _documentData.value.documentId!!,
                title)
        }
    }

    sealed class UIEvents {
        data class ShowSnackbar(val message: String) : UIEvents()
    }


}