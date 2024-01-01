package com.bhaskarblur.sync_realtimecontentwriting.data.remote

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import com.bhaskarblur.sync_realtimecontentwriting.R
import com.bhaskarblur.sync_realtimecontentwriting.core.utils.ColorHelper
import com.bhaskarblur.sync_realtimecontentwriting.data.remote.dto.DocumentModelDto
import com.bhaskarblur.sync_realtimecontentwriting.data.remote.dto.UserModelCursorDto
import com.bhaskarblur.sync_realtimecontentwriting.data.remote.dto.UserModelCursorListDto
import com.bhaskarblur.sync_realtimecontentwriting.data.remote.dto.UserModelDto
import com.bhaskarblur.sync_realtimecontentwriting.data.repository.UserRepositoryImpl
import com.bhaskarblur.sync_realtimecontentwriting.domain.model.DocumentModel
import com.bhaskarblur.sync_realtimecontentwriting.domain.model.UserModel
import com.bhaskarblur.sync_realtimecontentwriting.domain.repository.IUserRepository
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FirebaseManager @Inject constructor(
    private val database: FirebaseDatabase,
    private val documentRef: DatabaseReference,
    private val usersModelRef: DatabaseReference,
    private val usersRepo: IUserRepository,
) {

    private val _documentDetails = mutableStateOf(DocumentModel(null, null, null))
    val documentDetails: MutableState<DocumentModel> get() = _documentDetails
    companion object {
        fun DB_URL(context: Context,) : String {
            return context.getString(R.string.DB_URL)
        }
    }

    suspend fun createUser(userName: String, fullName: String): UserModel {
        var user: UserModelDto = UserModelDto(userName = userName, fullName = fullName)
        val key = usersModelRef.push().key

        withContext(Dispatchers.IO) {
            usersModelRef.child(key ?: "").setValue(
                UserModelDto(
                    id = key ?: "",
                    userName, fullName
                )
            ).addOnCompleteListener {
                if (it.isSuccessful) {
                    user = UserModelDto(
                        id = key ?: "",
                        userName, fullName
                    )
                    return@addOnCompleteListener
                }

            }
        }

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
        lateinit var document: DocumentModelDto
        withContext(Dispatchers.IO) {
            documentRef.child(documentId).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        document = dataSnapshot.getValue(DocumentModelDto::class.java)!!
                    } else {
                        documentRef.child(documentId).setValue(
                            DocumentModelDto(
                                documentId,
                                null,
                                null
                            )
                        )
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    databaseError.toException().printStackTrace()
                }
            })
        }
        return document.toDocumentModel()
    }

    suspend fun updateDocumentContent(documentId: String, content: String): Boolean {
        var flag = false
        withContext(Dispatchers.IO) {
            documentRef.child(documentId).child("content")
                .child("content").setValue(content).addOnCompleteListener {
                    if (it.isSuccessful) {
                        flag = true
                    }
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
                            dataList.forEach {user ->
                                val userValue = user.getValue(UserModelCursorDto::class.java)
                                if (userValue != null) {
                                    collabList.add(userValue)
                                }
                            }

                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }

                })
        }
        return collabList
    }

    suspend fun switchUserToOffline(documentId: String, userId: String): Boolean {
        var flag = false
        withContext(Dispatchers.IO) {
            documentRef.child(documentId).child("liveCollaborators")
                .child(userId).removeValue().addOnCompleteListener {
                    if (it.isSuccessful) {
                        flag = true
                    }
                }
        }
        return flag
    }

    suspend fun switchUserToOnline(documentId: String, userId: String) : Boolean {
        var flag = false

        withContext(Dispatchers.IO) {
            lateinit  var userData : UserModelDto
            usersRepo.getUserDetails().collectLatest {
                userData = UserModelDto(it.id, it.userName, it.fullName)
            }

            documentRef.child(documentId).child("liveCollaborators")
                .child(userId)
                .setValue(UserModelCursorDto(
                    userData, ColorHelper.generateColor(), -1
                )).addOnCompleteListener {
                    if (it.isSuccessful) {
                        flag = true
                    }
                }
        }
        return flag
    }

    suspend fun changeUserCursorLocation(documentId: String, userId: String, position : Int) : Boolean {
        var flag = false

        withContext(Dispatchers.IO) {
            lateinit  var userData : UserModelDto
            usersRepo.getUserDetails().collectLatest {
                userData = UserModelDto(it.id, it.userName, it.fullName)
            }

            documentRef.child(documentId).child("liveCollaborators")
                .child(userId)
                .child("position").setValue(position).addOnCompleteListener {
                    if (it.isSuccessful) {
                        flag = true
                    }
                }
        }
        return flag
    }

    suspend fun liveChangesListener(documentId: String) : Unit {
        withContext(Dispatchers.IO) {
            documentRef.child(documentId)
                    .addChildEventListener(object : ChildEventListener {
                        override fun onChildAdded(
                            snapshot: DataSnapshot,
                            previousChildName: String?
                        ) {
                            if(snapshot.exists()) {
                                val value = snapshot.getValue(DocumentModelDto::class.java)
                                value?.let {
                                    _documentDetails.value = value.toDocumentModel()
                                }

                            }
                        }

                        override fun onChildChanged(
                            snapshot: DataSnapshot,
                            previousChildName: String?
                        ) {
                            val value = snapshot.getValue(DocumentModelDto::class.java)
                            value?.let {
                                _documentDetails.value = value.toDocumentModel()
                            }
                        }

                        override fun onChildRemoved(snapshot: DataSnapshot) {
                            val value = snapshot.getValue(DocumentModelDto::class.java)
                            value?.let {
                                _documentDetails.value = value.toDocumentModel()
                            }
                        }

                        override fun onChildMoved(
                            snapshot: DataSnapshot,
                            previousChildName: String?
                        ) {
                            val value = snapshot.getValue(DocumentModelDto::class.java)
                            value?.let {
                                _documentDetails.value = value.toDocumentModel()
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                        }

                    })
        }
    }
}