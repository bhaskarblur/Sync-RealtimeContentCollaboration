package com.bhaskarblur.sync_realtimecontentwriting.presentation.signUp

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bhaskarblur.dictionaryapp.core.utils.Resources
import com.bhaskarblur.sync_realtimecontentwriting.domain.model.DocumentModel
import com.bhaskarblur.sync_realtimecontentwriting.domain.model.UserModel
import com.bhaskarblur.sync_realtimecontentwriting.domain.use_case.DocumentUseCase
import com.bhaskarblur.sync_realtimecontentwriting.domain.use_case.UserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DocumentViewModel @Inject constructor(
    private val documentUseCase: DocumentUseCase
) : ViewModel() {

    private val _documentData : MutableState<DocumentModel> = documentUseCase._documentDetails
    val documentData : State<DocumentModel> = _documentData

    private val _changeHistory = mutableListOf<String>()
    val changeHistory : List<String> = _changeHistory.toList()

    private val _eventFlow = MutableSharedFlow<UIEvents>()
    val eventFlow = _eventFlow

    private var updateJob : Job? = null

    fun getDocumentData(documentId : String) {
        viewModelScope.launch {
        documentUseCase.getDocumentDetails(documentId).collectLatest {
            when(it) {
                is Resources.Success -> {
                    it.data?.let {
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

        documentUseCase.listenToLiveChanges(documentId)
    }

    fun switchUserOn(userId: String, documentId: String) {
        viewModelScope.launch {
            documentUseCase.switchUserToOnline(documentId, userId)
        }
    }

    fun switchUserOff(userId: String, documentId: String) {
        viewModelScope.launch {
            documentUseCase.switchUserToOffline(documentId, userId)
        }
    }

    fun updateContent(content : String) {
        _documentData.value.content?.content = content

        updateJob?.cancel()
        updateJob = viewModelScope.launch {
            delay(200)
            _documentData.value.documentId?.let { documentUseCase.updateContent(content = content, documentId = it) }

        }
    }

    sealed class UIEvents {
        data class ShowSnackbar(val message: String) : UIEvents()
    }


}