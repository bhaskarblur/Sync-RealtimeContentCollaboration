package com.bhaskarblur.sync_realtimecontentwriting.di

import android.content.Context
import com.bhaskarblur.sync_realtimecontentwriting.data.local.SharedPreferencesManager
import com.bhaskarblur.sync_realtimecontentwriting.data.remote.FirebaseManager
import com.bhaskarblur.sync_realtimecontentwriting.data.repository.DocumentRepositoryImpl
import com.bhaskarblur.sync_realtimecontentwriting.data.repository.UserRepositoryImpl
import com.bhaskarblur.sync_realtimecontentwriting.domain.repository.IDocumentRepository
import com.bhaskarblur.sync_realtimecontentwriting.domain.repository.IUserRepository
import com.bhaskarblur.sync_realtimecontentwriting.domain.use_case.DocumentUseCase
import com.bhaskarblur.sync_realtimecontentwriting.domain.use_case.UserUseCase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModules {

    @Provides
    @Singleton
    fun providesAppContext(@ApplicationContext context: Context) : Context  {
        return context
    }

    @Provides
    @Singleton
    fun providesSharedPfManager(context: Context) : SharedPreferencesManager {
        return SharedPreferencesManager(context)
    }

    @Provides
    @Singleton
    fun providesFirebaseManager(context: Context, firebaseDatabase: FirebaseDatabase,
                              @Named("docsRef") databaseReference: DatabaseReference,
                              @Named("usersRef") usersRef: DatabaseReference,
                                userRepository: IUserRepository
                                ) : FirebaseManager {
        return FirebaseManager(providesFirebaseDatabase(context),
            databaseReference, usersRef, userRepository)
    }

    @Provides
    @Singleton
    fun providesFirebaseDatabase( context: Context) : FirebaseDatabase {
        return FirebaseDatabase.getInstance(FirebaseManager.DB_URL(context))
    }

    @Provides
    @Singleton
    fun providesUserRepository(firebaseManager : FirebaseManager, sharedPreferencesManager: SharedPreferencesManager) : IUserRepository {
        return UserRepositoryImpl(firebaseManager, sharedPreferencesManager)
    }
    @Provides
    @Singleton
    fun providesDocumentRepository(firebaseManager : FirebaseManager) : IDocumentRepository {
        return DocumentRepositoryImpl(firebaseManager)
    }

    @Provides
    @Singleton
    fun providesUserUseCase(context: Context, userRepository: IUserRepository) : UserUseCase {
        return UserUseCase(userRepository)
    }

    @Provides
    @Singleton
    fun providesDocumentUseCase(documentRepo : IDocumentRepository) : DocumentUseCase {
        return DocumentUseCase(documentRepo)
    }

    @Provides
    @Singleton
    @Named("docsRef")
    fun providesDocumentRef(database: FirebaseDatabase) : DatabaseReference {
        return database.getReference("documents")
    }

    @Provides
    @Singleton
    @Named("usersRef")
    fun providesUserRef(database: FirebaseDatabase) : DatabaseReference {
        return database.getReference("users")
    }
}