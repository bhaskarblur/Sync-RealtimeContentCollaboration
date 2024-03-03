package com.bhaskarblur.sync_realtimecontentwriting.data.repository

import com.bhaskarblur.dictionaryapp.core.utils.Resources
import com.bhaskarblur.sync_realtimecontentwriting.data.remote.FirebaseManager
import com.bhaskarblur.sync_realtimecontentwriting.domain.model.CommentsModel
import com.bhaskarblur.sync_realtimecontentwriting.domain.repository.ICommentsRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CommentsRepositoryImpl @Inject constructor(
    private val firebaseManager : FirebaseManager
) : ICommentsRepo {
    override fun getDocumentComments() {
        firebaseManager.getDocumentComments()
    }
    override fun addComment(documentId: String, comment: CommentsModel): Flow<Resources<Boolean>> {
        return try {
            firebaseManager.addCommentToDocument(documentId,comment)
            flow {
                emit(
                    Resources.Success(true)
                )
            }
        } catch (e : Exception) {
            e.printStackTrace()
            flow {
                emit(
                    Resources.Success(false)
                )
            }
        }
    }

    override fun deleteComment(documentId: String, commentId: String): Flow<Resources<Boolean>> {
        return try {
            firebaseManager.deleteComment(documentId,commentId)
            flow {
                emit(
                    Resources.Success(true)
                )
            }
        } catch (e : Exception) {
            e.printStackTrace()
            flow {
                emit(
                    Resources.Success(false)
                )
            }
        }
    }
}