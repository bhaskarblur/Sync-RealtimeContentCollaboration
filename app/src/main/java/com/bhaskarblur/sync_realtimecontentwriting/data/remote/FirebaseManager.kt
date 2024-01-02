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
import com.bhaskarblur.sync_realtimecontentwriting.data.remote.dto.UserModelCursorDto
import com.bhaskarblur.sync_realtimecontentwriting.data.remote.dto.UserModelDto
import com.bhaskarblur.sync_realtimecontentwriting.domain.model.DocumentModel
import com.bhaskarblur.sync_realtimecontentwriting.domain.model.UserModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception
import javax.inject.Inject

class FirebaseManager @Inject constructor(
    private val database: FirebaseDatabase,
    private val documentRef: DatabaseReference,
    private val usersModelRef: DatabaseReference,
    private val shpRepo: SharedPreferencesManager,
) {

    private val _documentDetails = mutableStateOf(DocumentModel(null, null, null, null))
    val documentDetails: MutableState<DocumentModel> = _documentDetails

    companion object {
        fun DB_URL(context: Context): String {
            return context.getString(R.string.DB_URL)
        }
    }

    suspend fun createUser(userName: String, fullName: String): UserModel {
        var user: UserModelDto = UserModelDto(
            id = usersModelRef.push().key,
            userName = userName, fullName = fullName
        )

        withContext(Dispatchers.IO) {
            usersModelRef.child(user.id ?: "").setValue(
                UserModelDto(
                    id = user.id ?: "",
                    userName, fullName
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
                        userName, fullName
                    )
                    return@addOnCompleteListener
                }

            }
        }
        Log.d("FirebaseUserCreated", user.toUserModel().toString())
        return user.toUserModel()
    }

    suspend fun getUserById(userId: String): UserModel {
        lateinit var user: UserModelDto
        withContext(Dispatchers.IO) {
            usersModelRef.child(userId).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        user = dataSnapshot.getValue(UserModelDto::class.java)!!
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    databaseError.toException().printStackTrace()
                }
            })
        }
        return user.toUserModel()
    }

    suspend fun getDocumentDetails(documentId: String): DocumentModel {
        var document: DocumentModelDto = DocumentModelDto(
            documentId,
            "",
            null,
            null
        )
        withContext(Dispatchers.IO) {
            documentRef.child(documentId)
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
                            Log.d("documentExists", value.toString())
                            document = DocumentModelDto(
                                documentId = value.documentId,
                                value.documentName,
                                value.content,
                                contributorsList
                            )
                            _documentDetails.value = document.toDocumentModel()
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
                    Log.d("switchedOff", snapshot.toString())
                    if (snapshot.exists()) {
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
        if (userId.isNotEmpty()) {
            documentRef.child(documentId).child("liveCollaborators")
                .child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        Log.d("switchedOff", snapshot.toString())
                        if (!snapshot.exists()) {
                            documentRef.child(documentId).child("liveCollaborators")
                                .child(userId)
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

                    override fun onCancelled(error: DatabaseError) {
                        error.toException().printStackTrace()
                    }
                })
        }
        return flag
    }

    fun changeUserCursorPosition(
        documentId: String,
        userId: String,
        position: Int
    ): Boolean {
        var flag = false
        val userData = shpRepo.getSession()
        documentRef.child(documentId).child("liveCollaborators")
            .child(userId)
            .child("position").setValue(position).addOnCompleteListener {
                if (it.isSuccessful) {
                    flag = true
                }
            }
        return flag
    }

}