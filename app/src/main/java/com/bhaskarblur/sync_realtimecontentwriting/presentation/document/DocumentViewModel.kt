package com.bhaskarblur.sync_realtimecontentwriting.presentation.document

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bhaskarblur.dictionaryapp.core.utils.Resources
import com.bhaskarblur.gptbot.models.GptBody
import com.bhaskarblur.gptbot.models.GptMessageModel
import com.bhaskarblur.gptbot.models.MessageBody
import com.bhaskarblur.sync_realtimecontentwriting.domain.model.DocumentModel
import com.bhaskarblur.sync_realtimecontentwriting.domain.model.UserModel
import com.bhaskarblur.sync_realtimecontentwriting.domain.repository.IUserRepository
import com.bhaskarblur.sync_realtimecontentwriting.domain.use_case.DocumentUseCase
import com.bhaskarblur.sync_realtimecontentwriting.domain.use_case.GptUseCase
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
    private val userRepo: IUserRepository,
    private val gptRepo: GptUseCase
) : ViewModel() {

    private val _documentData = documentUseCase._documentDetails
    val documentData: State<DocumentModel> = _documentData
    private val _changeHistory = mutableListOf<String>()
    val undoStack by mutableStateOf(Stack<String>())
    var redoStack by mutableStateOf(Stack<String>())

    private val _gptData = mutableStateOf(MessageBody(role = "", content = ""))
    val gptData get() = _gptData
    private val gptMessagesList  = mutableListOf<MessageBody>()

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
            switchUserOn()
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

    fun switchUserOn() {
        viewModelScope.launch(Dispatchers.IO) {
            if (!userDetails.value.id.isNullOrEmpty()) {
                viewModelScope.launch(Dispatchers.IO) {
                    if(documentData.value.documentId != null) {
                        documentUseCase.switchUserToOnline(
                            documentData.value.documentId!! ,
                            userDetails.value.id!!
                        )
                    }
                }
            }
        }
    }

    fun switchUserOff() {
        viewModelScope.launch(Dispatchers.IO) {
            if(!userDetails.value.id.isNullOrEmpty()) {
                documentUseCase.switchUserToOffline(
                    documentData.value.documentId!!,
                    userDetails.value.id!!
                )
            }
        }
    }


    fun undoChanges() {
        if(undoStack.size > 0) {
            Log.d("calledForUndo", undoStack.peek().toString())
            updateContent(undoStack.peek(), undoStack.peek().length-1)
            redoStack.push(undoStack.peek())
            undoStack.pop()
        }
    }

    fun redoChanges() {
        if(redoStack.size > 0) {
            Log.d("calledForRedo", redoStack.peek())
            updateContent(redoStack.peek(), redoStack.peek().length-1)
            redoStack.pop()
        }
    }
    fun handleUndoRedoStack(content: String) {
        viewModelScope.launch {
            delay(200)
            undoStack.push(content)
            try {
                redoStack = Stack()
            } catch (e: Exception) {
                e.printStackTrace()
            }
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
            switchUserOn()
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

    fun getGptSuggestions(message: String) {
        gptMessagesList.add(MessageBody("user", message))
        val body = GptBody(messages = gptMessagesList)
        Log.d("body", body.toString())
        viewModelScope.launch {
            _gptData.value = MessageBody("User","Generating..")
           gptRepo.getGptSuggestion(body).collectLatest { res ->
               when(res) {
                   is Resources.Success -> {
                       Log.d("data", res.data?.get(0)?.message.toString())
                       _gptData.value = MessageBody("User","")
                       res.data?.get(0)?.message?.content?.split(" ")?.forEach { char ->
                           delay(40)
                           _gptData.value= MessageBody(role = "assistant",gptData.value.content.plus(char).plus(" "))
                       }

                       gptMessagesList.add(MessageBody("assistant", res.data?.get(0)?.message?.content?:""))
                   }
                   is Resources.Error -> {
                       _eventFlow.emit(UIEvents.ShowSnackbar(res.message ?: "There was an error"))
                       _gptData.value = MessageBody("User","Error generating content")
                   }
                   is Resources.Loading -> {
                       _gptData.value = MessageBody("User","Generating..")
                   }

                   else -> {}
               }

           }
        }
    }

    sealed class UIEvents {
        data class ShowSnackbar(val message: String) : UIEvents()
    }


}