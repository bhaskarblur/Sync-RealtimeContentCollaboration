package com.bhaskarblur.sync_realtimecontentwriting.data.remote

import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.bhaskarblur.sync_realtimecontentwriting.R
import com.bhaskarblur.sync_realtimecontentwriting.core.utils.ColorHelper
import com.bhaskarblur.sync_realtimecontentwriting.data.local.SharedPreferencesManager
import com.bhaskarblur.sync_realtimecontentwriting.data.remote.dto.ContentModelDto
import com.bhaskarblur.sync_realtimecontentwriting.data.remote.dto.DocumentModelDto
import com.bhaskarblur.sync_realtimecontentwriting.data.remote.dto.DocumentModelDtoNoList
import com.bhaskarblur.sync_realtimecontentwriting.data.remote.dto.PromptModelDto
import com.bhaskarblur.sync_realtimecontentwriting.data.remote.dto.UserModelCursorDto
import com.bhaskarblur.sync_realtimecontentwriting.data.remote.dto.UserModelDto
import com.bhaskarblur.sync_realtimecontentwriting.domain.model.DocumentModel
import com.bhaskarblur.sync_realtimecontentwriting.domain.model.UserModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.lang.Exception
import javax.inject.Inject

class FirebaseManager @Inject constructor(
    private val documentRef: DatabaseReference,
    private val usersModelRef: DatabaseReference,
    private val shpRepo: SharedPreferencesManager,
) {

    private val _documentDetails = mutableStateOf(DocumentModel(null, null, null, null))
    val documentDetails: MutableState<DocumentModel> = _documentDetails
    private lateinit var changeListener: ValueEventListener
    private var userDetails: UserModelDto = UserModelDto()

    companion object {
        fun DB_URL(context: Context): String {
            return context.getString(R.string.DB_URL)
        }
    }

    suspend fun createUser(userName: String, fullName: String, password: String): UserModel {
        var user = UserModelDto(
            id = usersModelRef.push().key,
            userName = userName, fullName = fullName
        )
        withContext(Dispatchers.IO) {
            val checkUser = getUserByUserName(userName)
            Log.d("checkingUserName", checkUser.toString())
            if (checkUser.id?.isEmpty() == true) {
                usersModelRef.child(user.id ?: "").setValue(
                    UserModelDto(
                        id = user.id ?: "",
                        userName, fullName, password
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
                            userName, fullName,
                            password
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
        kotlinx.coroutines.delay(4000)
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
        kotlinx.coroutines.delay(500)
        Log.d("UserDb", user.toUserModel().toString())
        return user
    }

    suspend fun getDocumentDetails(documentId: String): DocumentModel {
        var document = DocumentModelDto(
            documentId,
            "",
            null,
            null
        )
        userDetails = shpRepo.getSession()
        withContext(Dispatchers.IO) {
            changeListener = documentRef.child(documentId)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            val value = snapshot.getValue(DocumentModelDtoNoList::class.java)!!
                            val contributorsList = arrayListOf<UserModelCursorDto>()
                            snapshot.child("liveCollaborators").children.forEach {
                                val child = it.getValue(UserModelCursorDto::class.java)!!
                                Log.d("docData", child.toString())
                                contributorsList.add(child)
                            }
                            val promptsList = arrayListOf<PromptModelDto>()
                            snapshot.child("promptsList").children.forEach {
                                val child = it.getValue(PromptModelDto::class.java)!!
                                Log.d("promptData", child.toString())
                                promptsList.add(child)
                            }
                            document = DocumentModelDto(
                                documentId = value.documentId,
                                value.documentName,
                                value.content,
                                contributorsList,
                                promptsList = PromptModelDto.listToArrayList(
                                    promptsList.map {
                                        it.toPromptModel()
                                    }.sortedBy {
                                        it.timeStamp
                                    }
                                )
                            )
                            if (value.content?.lastEditedBy != null) {
                                if (value.content.lastEditedBy == userDetails.id!!) {
                                    Log.d(
                                        "hitIf",
                                        _documentDetails.value.content?.content.isNullOrEmpty()
                                            .toString()
                                    )
                                    if (_documentDetails.value.content?.content.isNullOrEmpty() ||
                                        _documentDetails.value.content?.content == null
                                    ) {
                                        _documentDetails.value = document.toDocumentModel()
                                    }
                                } else {
                                    Log.d("hitElse", "yes")
                                    _documentDetails.value = document.toDocumentModel()
                                }
                            } else {
                                Log.d("hitElse", "yes")
                                _documentDetails.value = document.toDocumentModel()
                            }
                        } else {
                            documentRef.child(documentId).setValue(
                                DocumentModelDto(
                                    documentId,
                                    "",
                                    ContentModelDto(documentId, ""),
                                    null
                                )
                            )
                            document = DocumentModelDto(
                                documentId,
                                "",
                                ContentModelDto(documentId, ""),
                                null
                            )
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        error.toException().printStackTrace()
                    }

                })
        }
        return document.toDocumentModel()
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

}