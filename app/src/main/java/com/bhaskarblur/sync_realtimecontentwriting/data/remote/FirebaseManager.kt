package com.bhaskarblur.sync_realtimecontentwriting.data.remote

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.bhaskarblur.sync_realtimecontentwriting.R
import com.bhaskarblur.sync_realtimecontentwriting.core.utils.ColorHelper
import com.bhaskarblur.sync_realtimecontentwriting.data.local.SharedPreferencesManager
import com.bhaskarblur.sync_realtimecontentwriting.data.remote.dto.CommentsModelDto
import com.bhaskarblur.sync_realtimecontentwriting.data.remote.dto.ContentModelDto
import com.bhaskarblur.sync_realtimecontentwriting.data.remote.dto.DocumentModelDto
import com.bhaskarblur.sync_realtimecontentwriting.data.remote.dto.DocumentModelDtoNoList
import com.bhaskarblur.sync_realtimecontentwriting.data.remote.dto.PromptModelDto
import com.bhaskarblur.sync_realtimecontentwriting.data.remote.dto.UserModelCursorDto
import com.bhaskarblur.sync_realtimecontentwriting.data.remote.dto.UserModelDto
import com.bhaskarblur.sync_realtimecontentwriting.data.remote.dto.UserRecentDocsDto
import com.bhaskarblur.sync_realtimecontentwriting.domain.model.CommentsModel
import com.bhaskarblur.sync_realtimecontentwriting.domain.model.DocumentModel
import com.bhaskarblur.sync_realtimecontentwriting.domain.model.UserModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


class FirebaseManager @Inject constructor(
    private val storageReference: StorageReference,
    private val documentRef: DatabaseReference,
    private val usersModelRef: DatabaseReference,
    private val shpRepo: SharedPreferencesManager,
) {

    private val _documentDetails = mutableStateOf(DocumentModel(null, null, null, "", 0, null))
    val documentDetails: MutableState<DocumentModel> = _documentDetails
    val recentDocuments: MutableState<ArrayList<DocumentModel>> =
        mutableStateOf(arrayListOf<DocumentModel>())
    private var userDetails: UserModelDto = UserModelDto()

    companion object {
        fun DB_URL(context: Context): String {
            return context.getString(R.string.DB_URL)
        }

        fun STORAGE_URL(context: Context): String {
            return context.getString(R.string.STORAGE_URL)
        }
    }

    suspend fun createUser(
        userName: String,
        fullName: String,
        userEmail: String,
        password: String,
        userPictureUri: Uri
    ): UserModel {
        val imageUrl = uploadProfileImage(userName, userPictureUri)
        var user = UserModelDto(
            id = usersModelRef.push().key,
            userName = userName,
            fullName = fullName,
            userEmail = userEmail,
            password = password,
            userPicture = imageUrl
        )
        withContext(Dispatchers.IO) {
            val checkUser = getUserByUserName(userName)
            Log.d("checkingUserName", checkUser.toString())
            if (checkUser.id?.isEmpty() == true) {
                usersModelRef.child(user.id ?: "").setValue(
                    UserModelDto(
                        id = user.id ?: "",
                        user.userName,
                        user.fullName,
                        user.userEmail,
                        user.password,
                        user.userPicture
                    )
                ).addOnCompleteListener {
                    try {
                        Log.d("taskResult", it.isSuccessful.toString())
                        Log.d("taskResult", it.isComplete.toString())
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    if (it.isSuccessful) {
                        user = UserModelDto(
                            id = user.id,
                            user.userName,
                            user.fullName,
                            user.userEmail,
                            user.password,
                            user.userPicture
                        )
                        return@addOnCompleteListener
                    }

                }
            } else {
                user = UserModelDto()
            }
        }
        Log.d("FirebaseUserCreated", user.toUserModel().toString())
        return user.toUserModel()
    }

    private suspend fun uploadProfileImage(
        userName: String,
        imageUri: Uri
    ): String {
        val imgRef = storageReference.child("Profile/$userName/${UUID.randomUUID()}")
        val uploadTask = imgRef.putFile(imageUri)

        return suspendCoroutine { continuation ->
            uploadTask
                .addOnSuccessListener { _ ->
                    imgRef.downloadUrl.addOnSuccessListener { uri ->
                        continuation.resume(uri.toString())
                    }
                }
                .addOnFailureListener {
                    continuation.resumeWithException(it)
                }
        }
    }

    suspend fun getUserById(userId: String): UserModel {
        var user = UserModelDto()
        runBlocking {
            usersModelRef.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    Log.d("userDb1", dataSnapshot.exists().toString())
                    if (dataSnapshot.exists()) {
                        user = dataSnapshot.getValue(UserModelDto::class.java)!!
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    databaseError.toException().printStackTrace()
                }
            })

        }
        delay(4000)
        Log.d("UserDb2", user.toUserModel().toString())
        return user.toUserModel()
    }

    suspend fun getUserByUserName(userName: String): UserModelDto {
        var user = UserModelDto()
        usersModelRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.d("userDb", dataSnapshot.exists().toString())
                if (dataSnapshot.exists()) {
                    dataSnapshot.children.forEach {
                        val eachUser = it.getValue(UserModelDto::class.java)
                        if (eachUser?.userName?.equals(userName) == true) {
                            user = eachUser
                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                databaseError.toException().printStackTrace()
            }
        })
        delay(500)
        return user
    }

    suspend fun getDocumentById(documentId: String): DocumentModel {
        var document = DocumentModelDto(
            "",
            "",
            "",
            0,
            null,
            null
        )
        documentRef.child(documentId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val value = snapshot.getValue(DocumentModelDtoNoList::class.java)!!
                        val contributorsList = arrayListOf<UserModelCursorDto>()
                        snapshot.child("liveCollaborators").children.forEach {
                            val child = it.getValue(UserModelCursorDto::class.java)!!
                            contributorsList.add(child)
                        }
                        val promptsList = arrayListOf<PromptModelDto>()
                        val commentsList = arrayListOf<CommentsModelDto>()
                        snapshot.child("promptsList").children.forEach {
                            val child = it.getValue(PromptModelDto::class.java)!!
                            promptsList.add(child)
                        }
                        Log.d("checkingCommentsList",snapshot.child("commentsList").toString())
                        snapshot.child("commentsList").children.forEach {
                            val child = it.getValue(CommentsModelDto::class.java)!!
                            commentsList.add(child)
                        }
                        document = DocumentModelDto(
                            documentId = value.documentId,
                            value.documentName,
                            value.createdBy, value.creationDateTime,
                            value.content,
                            contributorsList,
                            promptsList = PromptModelDto.listToArrayList(
                                promptsList.map {
                                    it.toPromptModel()
                                }.sortedBy {
                                    it.timeStamp
                                }
                            ),
                            commentsList = CommentsModelDto.listToArrayList(
                                commentsList.map {
                                    it.toCommentsModel()
                                }.sortedBy { it.commentDateTime }
                            )
                        )
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
        delay(1000)
        return document.toDocumentModel()
    }


    suspend fun getDocumentDetails(documentId: String): DocumentModel {
        var document = DocumentModelDto(
            documentId,
            "",
            "",
            0,
            null,
            null
        )
        userDetails = shpRepo.getSession()
        withContext(Dispatchers.IO) {
            documentRef.child(documentId)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            val value = snapshot.getValue(DocumentModelDtoNoList::class.java)!!
                            val contributorsList = arrayListOf<UserModelCursorDto>()
                            snapshot.child("liveCollaborators").children.forEach {
                                val child = it.getValue(UserModelCursorDto::class.java)!!
                                contributorsList.add(child)
                            }
                            val promptsList = arrayListOf<PromptModelDto>()
                            val commentsList = arrayListOf<CommentsModelDto>()
                            snapshot.child("promptsList").children.forEach {
                                val child = it.getValue(PromptModelDto::class.java)!!
                                promptsList.add(child)
                            }
                            snapshot.child("commentsList").children.forEach {
                                val child = it.getValue(CommentsModelDto::class.java)!!
                                commentsList.add(child)
                            }
                            document = DocumentModelDto(
                                documentId = value.documentId,
                                value.documentName,
                                value.createdBy, 0,
                                value.content,
                                contributorsList,
                                promptsList = PromptModelDto.listToArrayList(
                                    promptsList.map {
                                        it.toPromptModel()
                                    }.sortedBy {
                                        it.timeStamp
                                    }
                                ),
                                commentsList = CommentsModelDto.listToArrayList(
                                    commentsList.map {
                                        it.toCommentsModel()
                                    }.sortedBy { it.commentDateTime }
                                )
                            )
                            if (value.content?.lastEditedBy != null) {
                                if (value.content.lastEditedBy == userDetails.id!!) {
                                    if (_documentDetails.value.content?.content.isNullOrEmpty() ||
                                        _documentDetails.value.content?.content == null
                                    ) {
                                        _documentDetails.value = document.toDocumentModel()
                                        Log.d("checkingCommentsList",
                                            document.commentsList.toString())
                                    }
                                } else {
                                    _documentDetails.value = document.toDocumentModel()
                                    Log.d("checkingCommentsList",
                                        document.commentsList.toString())
                                }
                            } else {
                                _documentDetails.value = document.toDocumentModel()
                                Log.d("checkingCommentsList",
                                    document.commentsList.toString())
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        error.toException().printStackTrace()
                    }

                })
        }
        return document.toDocumentModel()
    }

    fun deleteDocument(documentId: String) {
        documentRef.child(documentId).removeValue()
    }

    suspend fun createDocument(userId: String): DocumentModel {
        var document = DocumentModelDto(
            "",
            "",
            userId, System.currentTimeMillis(),
            ContentModelDto("", ""),
            null
        )
        val dId = documentRef.push().key ?: ""
        documentRef.child(dId).setValue(
            DocumentModelDto(
                dId, "", userId,
                System.currentTimeMillis(), ContentModelDto("", ""), null
            )
        ).addOnCompleteListener {
            if (it.isSuccessful) {
                document = DocumentModelDto(
                    dId,
                    "",
                    createdBy = userId, System.currentTimeMillis(),
                    ContentModelDto("", ""),
                    null
                )
            }
        }
        delay(1500)
        return document.toDocumentModel()
    }

    suspend fun getUserDocumentsByUserId(userId: String): List<DocumentModel> {
        val docsList = arrayListOf<DocumentModelDto>()
        documentRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    snapshot.children.forEach { doc ->
                        val docValue = doc.getValue(DocumentModelDtoNoList::class.java)
                        docValue?.let {
                            Log.d(
                                "firebaseUserDocs",
                                docValue.createdBy?.equals(userId).toString()
                            )
                            if (docValue.createdBy?.equals(userId) == true) {
                                docsList.add(
                                    DocumentModelDto(
                                        documentId = docValue.documentId,
                                        documentName = docValue.documentName,
                                        content = docValue.content,
                                        createdBy = docValue.createdBy,
                                        creationDateTime = docValue.creationDateTime,
                                        liveCollaborators = listOf(),
                                        promptsList = arrayListOf()
                                    )
                                )
                            }
                        }

                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
        delay(3500)
        return docsList.map { it.toDocumentModel() }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun getRecentDocumentsByUserId(userId: String) {
        usersModelRef.child(userId).child("recentDocuments")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val t: GenericTypeIndicator<ArrayList<UserRecentDocsDto>> =
                            object : GenericTypeIndicator<ArrayList<UserRecentDocsDto>>() {}
                        val documents: ArrayList<UserRecentDocsDto> =
                            snapshot.getValue(t) ?: arrayListOf()
                        Log.d(
                            "gotDocToRecent",
                            documents.toString()
                        )
                        GlobalScope.launch {
                            val listDocs = arrayListOf<DocumentModel>()
                            documents.forEach { document ->
                                val documentData = getDocumentById(document.documentId ?: "")
                                if (documentData.createdBy.isEmpty() && documentData.creationDateTime.toInt() == 0) {
                                    listDocs.add(
                                        DocumentModel(
                                            documentId = document.documentId,
                                            documentName = "",
                                            content = null,
                                            liveCollaborators = listOf()
                                        )
                                    )
                                } else {
                                    listDocs.add(documentData)
                                }
                            }
                            recentDocuments.value = listDocs
                        }
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
    }

    fun addToRecentDocuments(userId: String, documentId: String) {
        Log.d("addedDocToRecentId", documentId)
        usersModelRef.child(userId).child("recentDocuments")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val t: GenericTypeIndicator<ArrayList<UserRecentDocsDto>> =
                            object : GenericTypeIndicator<ArrayList<UserRecentDocsDto>>() {}
                        val documents: ArrayList<UserRecentDocsDto> =
                            snapshot.getValue(t) ?: arrayListOf()

                        documents.removeIf {
                            it.documentId == documentId
                        }
                        documents.add(UserRecentDocsDto(documentId))
                        usersModelRef.child(userId)
                            .child("recentDocuments")
                            .setValue(documents).addOnCompleteListener {
                                if (it.isSuccessful) {
                                    Log.d("addedDocToRecent", "yes")
                                }
                            }
                    } else {
                        val listDocs = arrayListOf<UserRecentDocsDto>()
                        listDocs.add(UserRecentDocsDto(documentId))
                        usersModelRef.child(userId)
                            .child("recentDocuments")
                            .setValue(listDocs).addOnCompleteListener {
                                if (it.isSuccessful) {
                                    Log.d("addedDocToRecent", "yes")
                                }
                            }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
    }

    fun updateDocumentContent(documentId: String, content: String): Boolean {
        var flag = false
        documentRef.child(documentId).child("content")
            .child("content").setValue(content).addOnCompleteListener {
                if (it.isSuccessful) {
                    flag = true
                    updateLastEditedByStatus(userDetails.id ?: "", documentId)
                }
            }
        return flag
    }

    fun updateDocumentTitle(documentId: String, title: String): Boolean {
        var flag = false
        documentRef.child(documentId).child("documentName")
            .setValue(title).addOnCompleteListener {
                if (it.isSuccessful) {
                    flag = true
                    updateLastEditedByStatus(userDetails.id ?: "", documentId)
                }
            }
        return flag
    }

    suspend fun getDocumentLiveCollaborators(documentId: String): ArrayList<UserModelCursorDto> {
        val collabList = arrayListOf<UserModelCursorDto>()
        withContext(Dispatchers.IO) {
            documentRef.child(documentId).child("liveCollaborators")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            val dataList = snapshot.children
                            dataList.forEach { user ->
                                val userValue = user.getValue(UserModelCursorDto::class.java)
                                if (userValue != null) {
                                    collabList.add(userValue)
                                }
                            }

                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        error.toException().printStackTrace()
                    }

                })
        }
        return collabList
    }

    fun switchUserToOffline(documentId: String, userId: String): Boolean {
        var flag = false
        Log.d("switchOffFirebase", userId)
        documentRef.child(documentId).child("liveCollaborators")
            .child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    if (snapshot.exists()) {
                        Log.d("switchedOff", snapshot.toString())
                        documentRef.child(documentId).child("liveCollaborators")
                            .child(userId).removeValue().addOnCompleteListener {
                                if (it.isSuccessful) {
                                    flag = true
                                }
                            }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    error.toException().printStackTrace()
                }
            })
        return flag
    }

    fun switchUserToOnline(documentId: String, userId: String): Boolean {
        var flag = false
        val userData = shpRepo.getSession()
        Log.d("localUserData", userData.toString())
        if (userId.isNotEmpty()) {
            documentRef.child(documentId).child("liveCollaborators")
                .child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        Log.d("switchedOn", snapshot.toString())
                        if (!snapshot.exists()) {
                            if (userData.id != null) {
                                documentRef.child(documentId).child("liveCollaborators")
                                    .child(userData.id ?: userId)
                                    .setValue(
                                        UserModelCursorDto(
                                            userData, ColorHelper.generateColor(), -1
                                        )
                                    ).addOnCompleteListener {
                                        if (it.isSuccessful) {
                                            flag = true
                                        }
                                    }
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        error.toException().printStackTrace()
                    }
                })
        }
        return flag
    }

    private fun updateLastEditedByStatus(userId: String?, documentId: String) {
        if (!userId.isNullOrEmpty()) {
            documentRef.child(documentId).child("content")
                .child("lastEditedBy").setValue(userId)
        }
    }

    fun changeUserCursorPosition(
        documentId: String,
        userId: String,
        position: Int
    ): Boolean {
        var flag = false
        Log.d("cursorPosition", position.toString())
        if (userId.isNotEmpty()) {
            documentRef.child(documentId).child("liveCollaborators")
                .child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            documentRef.child(documentId).child("liveCollaborators")
                                .child(userId)
                                .child("position").setValue(position).addOnCompleteListener {
                                    if (it.isSuccessful) {
                                        flag = true
                                        updateLastEditedByStatus(userDetails.id ?: "", documentId)
                                    }
                                }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        error.toException().printStackTrace()
                    }

                })
        }
        return flag
    }

    fun addPromptMessage(documentId: String, message: PromptModelDto): Boolean {
        val key = documentRef.push().key ?: ""
        var flag = false
        documentRef.child(documentId).child("promptsList")
            .child(key).setValue(message).addOnCompleteListener {
                if (it.isSuccessful) {
                    flag = true
                    updateLastEditedByStatus(userDetails.id ?: "", documentId)
                }
            }

        return flag
    }

    fun clearPromptsList(documentId: String): Boolean {
        var flag = false
        documentRef.child(documentId).child("promptsList")
            .removeValue().addOnCompleteListener {
                if (it.isSuccessful) {
                    flag = true
                    updateLastEditedByStatus(userDetails.id ?: "", documentId)
                }
            }

        return flag
    }

    fun getDocumentComments() {
        documentRef.child(documentDetails.value.documentId ?:"").child("commentsList")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    snapshot.children.forEach { child ->
                        val comment = child.getValue(CommentsModelDto::class.java)
                        val commentsList = arrayListOf<CommentsModelDto>()
                            .apply {
                                comment?.let {
                                    add(comment)
                                }
                            }
//                        documentDetails.value = documentDetails.value.apply {
//                            this.commentsList = CommentsModelDto.listToArrayList(
//                                commentsList.map {
//                                it.toCommentsModel()
//                            })
//                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    error.toException().printStackTrace()
                }

            })
    }

    fun addCommentToDocument(documentId : String, comment : CommentsModel) {
        val key = documentRef.push().key ?: ""
        comment.apply {
            this.commentId = key
        }
        documentRef.child(documentId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()) {
                    documentRef.child(documentId).child("commentsList").child(key)
                        .setValue(comment).addOnCompleteListener {
                            if(it.isSuccessful) {
                                comment.apply {
                                    commentId = key
                                }
                                Log.d("FirebaseAddComment", "Successful")
                                _documentDetails.value = _documentDetails.value.apply {
                                    commentsList.add(comment)
                                }
                                updateLastEditedByStatus(userDetails.id ?: "", documentId)
                            }
                        }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                error.toException().printStackTrace()
            }

        })
    }

    fun deleteComment(documentId : String, commentId : String) {
        val key = documentRef.push().key ?: ""
        documentRef.child(documentId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()) {
                    documentRef.child(documentId).child("commentsList").child(commentId)
                        .removeValue().addOnCompleteListener{
                            if(it.isSuccessful) {
                                Log.d("FirebaseAddComment", "Successful")
                                _documentDetails.value = _documentDetails.value.apply {
                                    commentsList.removeIf{
                                        it.commentId == commentId
                                    }
                                }
                                updateLastEditedByStatus(userDetails.id ?: "", documentId)
                            }
                        }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                error.toException().printStackTrace()
            }
        })
    }


}