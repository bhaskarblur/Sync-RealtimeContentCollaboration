package com.bhaskarblur.sync_realtimecontentwriting.presentation.document

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bhaskarblur.dictionaryapp.core.utils.Resources
import com.bhaskarblur.gptbot.models.GptBody
import com.bhaskarblur.sync_realtimecontentwriting.core.utils.AppNetworkManager
import com.bhaskarblur.sync_realtimecontentwriting.data.remote.dto.PromptModelDto
import com.bhaskarblur.sync_realtimecontentwriting.domain.model.CommentsModel
import com.bhaskarblur.sync_realtimecontentwriting.domain.model.ContentModel
import com.bhaskarblur.sync_realtimecontentwriting.domain.model.DocumentModel
import com.bhaskarblur.sync_realtimecontentwriting.domain.model.PromptModel
import com.bhaskarblur.sync_realtimecontentwriting.domain.model.UserModel
import com.bhaskarblur.sync_realtimecontentwriting.domain.repository.IUserRepository
import com.bhaskarblur.sync_realtimecontentwriting.domain.use_case.CommentsUseCase
import com.bhaskarblur.sync_realtimecontentwriting.domain.use_case.DocumentUseCase
import com.bhaskarblur.sync_realtimecontentwriting.domain.use_case.GptUseCase
import com.bhaskarblur.sync_realtimecontentwriting.presentation.UIEvents
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Stack
import javax.inject.Inject

@HiltViewModel
class DocumentViewModel @Inject constructor(
    private val documentUseCase: DocumentUseCase,
    private val userRepo: IUserRepository,
    private val gptRepo: GptUseCase,
    private val commentsUseCase: CommentsUseCase,
    private val appNetworkManager: AppNetworkManager
) : ViewModel() {

    private val _documentData = documentUseCase._documentDetails
    val documentData: State<DocumentModel> = _documentData
    val undoStack by mutableStateOf(Stack<String>())
    var redoStack by mutableStateOf(Stack<String>())
    var hasDoneUndoRedo = mutableStateOf(false)
    private val _gptData = mutableStateOf("")
    private val _eventFlow = MutableSharedFlow<UIEvents>()
    val eventFlow = _eventFlow

    private var updateJob: Job? = null

    val userDocuments = mutableStateListOf<DocumentModel>()
    val recentDocuments by documentUseCase.recentDocuments
    val userDetails = mutableStateOf(UserModel())

    fun setUser() {
        viewModelScope.launch {
            userRepo.getUserDetails().collect {
                Log.d("userDataEmitted", it.toString())
                userDetails.value = it
                getUserDocuments()
            }
            delay(1000)
            switchUserOn()
        }

    }

    fun emitUIEvent(value : UIEvents) {
        viewModelScope.launch {
            Log.d("UIEventEmitted", value.toString())
            _eventFlow.emit(value)
        }
    }
    fun getUserDocuments() {
        viewModelScope.launch {
            delay(800)
            documentUseCase.getUserDocumentsByUserId(userDetails.value.id ?: "").collectLatest {
                userDocuments.clear()
                it.forEach { doc ->
                    Log.d("userDocuments", doc.documentId.toString())
                    userDocuments.add(doc)
                }
            }
        }
    }

    fun getRecentDocuments() {
        viewModelScope.launch {
            documentUseCase.getRecentDocumentsByUserId(userDetails.value.id ?: "")
        }
    }

    fun addToRecentDocuments() {
        Log.d("ViewModelAddToRecent","yes")
        viewModelScope.launch {
           if(recentDocuments.isNotEmpty()) {
               recentDocuments.removeIf {
                   (it.documentId ?: "") == (_documentData.value.documentId ?: "")
               }
           }
            recentDocuments.add(0, _documentData.value)
            documentUseCase.addToRecentDocuments(userDetails.value
                .id?:"",_documentData.value.documentId?:"" )
        }
    }

    fun isMyDocument(docOwnerId: String) : Boolean {
        return docOwnerId == userDetails.value.id
    }
    fun getDocumentById(documentId: String): Boolean {
        var flag = false
        viewModelScope.launch {
        documentUseCase.getDocumentById(documentId).collectLatest {
            Log.d("docDetails", it.toString())
            if (it.documentId!!.isNotEmpty()) {
                flag = true
            }
            Log.d("documentIsValid", flag.toString())
        }
        delay(1500)
        }
        return flag
    }

    fun createDocument() {
        viewModelScope.launch {
            _eventFlow.emit(UIEvents.ShowCreateLoading("1"))
            documentUseCase.createDocument(userDetails.value.id ?: "").collectLatest { doc ->
                Log.d("userDocumentCreated", doc.documentId.toString())
                userDocuments.add(doc)
                recentDocuments.add(doc)
                _eventFlow.emit(UIEvents.ShowCreateLoading("0"))
                emitUIEvent(UIEvents.DocumentCodeApplied(doc.documentId ?:""))
            }
        }
    }

    fun deleteDocument(documentId: String?) {
        viewModelScope.launch {
            documentUseCase.deleteDocument(documentId ?: "")
            documentId?.let {
                userDocuments.removeIf {
                    it.documentId == documentId
                }
                Log.d("userDocumentDeleted", documentId.toString())
            }
        }
    }

    fun getDocumentData(documentId: String) {
        undoStack.clear()
        redoStack.clear()
        hasDoneUndoRedo.value = false
        viewModelScope.launch(Dispatchers.IO) {
            documentUseCase.getDocumentDetails(documentId, userDetails.value.id!!).collectLatest { it ->
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
            userDetails.value.userName?.let {
                if(!documentData.value.documentId.isNullOrEmpty()) {
                if (!userDetails.value.userName.isNullOrEmpty()) {
                    viewModelScope.launch(Dispatchers.IO) {
                        if (documentData.value.documentId != null) {
                            documentUseCase.switchUserToOnline(
                                documentData.value.documentId?:"",
                                userDetails.value.id.toString()
                            )
                        }
                    }
                }
                    }
            }
        }
    }

    private fun addMessageToPrompt(message: PromptModel) {
        viewModelScope.launch {
            documentUseCase.addMessageToPrompt(
                documentId = documentData.value.documentId!!,
                message
            )
        }
    }

    fun switchUserOff() {
        viewModelScope.launch(Dispatchers.IO) {
            if(!documentData.value.documentId.isNullOrEmpty()) {
                documentUseCase.switchUserToOffline(
                    documentData.value.documentId ?: "",
                    userDetails.value.id!!
                )
            }
        }
    }

    fun undoChanges() {
        if (undoStack.size > 0) {
            Log.d("calledForUndo", undoStack.peek().toString())
            hasDoneUndoRedo.value = true
            _documentData.value = DocumentModel(
                documentId = _documentData.value.documentId,
                documentName = _documentData.value.documentName,
                createdBy = _documentData.value.createdBy,
                creationDateTime = _documentData.value.creationDateTime,
                content = ContentModel(
                    content = undoStack.peek().toString(),
                    lastEditedBy = userDetails.value.id
                ),
                liveCollaborators = _documentData.value.liveCollaborators,
                promptsList = _documentData.value.promptsList,
                commentsList = _documentData.value.commentsList
            )
            updateContent(undoStack.peek(), undoStack.peek().length - 1)
            redoStack.push(undoStack.peek())
            undoStack.pop()
        }
    }

    fun redoChanges() {
        if (redoStack.size > 0) {
            Log.d("calledForRedo", redoStack.peek())
            hasDoneUndoRedo.value = true
            _documentData.value = DocumentModel(
                documentId = _documentData.value.documentId,
                _documentData.value.documentName,
                createdBy = _documentData.value.createdBy,
                creationDateTime = _documentData.value.creationDateTime,
                content = ContentModel(
                    content = redoStack.peek().toString(),
                    lastEditedBy = userDetails.value.id
                ),
                liveCollaborators = _documentData.value.liveCollaborators,
                commentsList = _documentData.value.commentsList
            )
            updateContent(redoStack.peek(), redoStack.peek().length - 1)
            undoStack.push(redoStack.peek())
            redoStack.pop()
        }
    }

    fun handleUndoRedoStack(content: String) {
        viewModelScope.launch {
            delay(800)
            undoStack.push(content)
            try {
                redoStack = Stack()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        hasDoneUndoRedo.value = false
    }

    fun updateCursorPosition(cursorPosition: Int) {
        documentUseCase.updateCursorPosition(
            documentId = _documentData.value.documentId!!,
            position = cursorPosition, userDetails.value.id!!
        )
        switchUserOn()
    }
    fun updateContent(content: String, cursorPosition: Int) {
        updateJob?.cancel()
        updateJob = viewModelScope.launch {
            documentUseCase.updateContent(
                _documentData.value.documentId!!,
                content
            )
            documentUseCase.updateCursorPosition(
                documentId = _documentData.value.documentId!!,
                position = cursorPosition, userDetails.value.id!!
            )
            switchUserOn()
            if (userDetails.value.id.isNullOrEmpty()) {
                userRepo.getUserDetails().collectLatest {
                    userDetails.value = it
                }
            }
        }

    }

    fun updateTitle(title: String) {
        updateJob?.cancel()
        updateJob = viewModelScope.launch {
            documentUseCase.updateDocumentTitle(
                _documentData.value.documentId!!,
                title
            )
            userDocuments.map {
                val doc = it
                if (doc.documentId == documentData.value.documentId) {
                    doc.documentName = title
                }
                doc
            }
        }
    }

    fun getGptSuggestions(message: String, ignoreChatHistory: Boolean) {
        val messageModel = PromptModel(
            message = message,
            role = "user",
            sentBy = userDetails.value,
            timeStamp = System.currentTimeMillis()
        )
        val tempMsg = DocumentModel(
            documentData.value.documentId,
            documentData.value.documentName,
            documentData.value.content,
            createdBy = _documentData.value.createdBy,
            creationDateTime = _documentData.value.creationDateTime,
            documentData.value.liveCollaborators,
            PromptModelDto.listToArrayList(
                documentData.value
                    .promptsList?.plus(
                        PromptModel(message, "user", userDetails.value, System.currentTimeMillis())
                    ) ?: listOf()
            ),
            commentsList = _documentData.value.commentsList
        )
        _documentData.value = tempMsg
        addMessageToPrompt(messageModel)
        val body: GptBody = if (ignoreChatHistory) {
            GptBody(messages = arrayListOf(messageModel.toMessageModel()))
        } else {
            GptBody(messages = _documentData.value.promptsList?.map { it.toMessageModel() }
                ?: listOf())
        }
        Log.d("body", body.toString())
        viewModelScope.launch {
            _gptData.value = "Generating..."
            gptRepo.getGptSuggestion(body).collectLatest { res ->
                when (res) {
                    is Resources.Success -> {
                        Log.d("data", res.data?.get(0)?.message.toString())
                        _gptData.value = ""
                        val promptMessage = PromptModel(
                            message = res.data?.get(0)?.message?.content.toString(),
                            role = "assistant", sentBy = null, System.currentTimeMillis()
                        )
                        val tempCnt = DocumentModel(
                            documentData.value.documentId,
                            documentData.value.documentName,
                            documentData.value.content,
                            createdBy = _documentData.value.createdBy,
                            creationDateTime = _documentData.value.creationDateTime,
                            documentData.value.liveCollaborators,
                            PromptModelDto.listToArrayList(
                                documentData.value
                                    .promptsList?.plus(promptMessage) ?: listOf()
                            ),
                            commentsList = _documentData.value.commentsList
                        )
                        _documentData.value = tempCnt
                        addMessageToPrompt(promptMessage)
                    }

                    is Resources.Error -> {
                        _eventFlow.emit(UIEvents.ShowSnackbar(res.message ?: "There was an error"))
                        _gptData.value = "There was an error, try again. "
                    }

                    is Resources.Loading -> {
                        _gptData.value = "Generating..."
                    }

                }

            }
        }
    }

    fun clearPromptHistory() {
        documentData.value.promptsList = arrayListOf()
        viewModelScope.launch {
            documentUseCase.clearPromptList(documentData.value.documentId ?: "")
        }

    }

    fun getAllComments() {
        viewModelScope.launch {
            commentsUseCase.getAllComments()
        }
    }

    fun addComment(comment : CommentsModel) {
        viewModelScope.launch {
            Log.d("requestedToAddComment", comment.toString())
            commentsUseCase.addComment(_documentData.value.documentId ?: "", comment)
                .collectLatest { result ->
                    Log.d("newCommentIsHere", result.data.toString())
                    Log.d("newCommentIsHere", (result is Resources.Success).toString())
                    when(result) {
                        is Resources.Success -> {
                            result.data?.let {cmModel ->
                            }
                        }
                        else -> {

                        }
                    }
                }

        }
    }

    fun deleteComment(commentId : String) {
        viewModelScope.launch {
            commentsUseCase.deleteComment(
                _documentData.value.documentId ?: "", commentId
            )
            _documentData.value = documentData.value.apply {
                commentsList.removeIf { cmm ->
                    cmm.commentId == commentId
                }
            }
        }
    }
}