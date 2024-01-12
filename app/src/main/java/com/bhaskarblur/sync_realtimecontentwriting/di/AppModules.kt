package com.bhaskarblur.sync_realtimecontentwriting.di

import android.content.Context
import com.bhaskarblur.gptbot.network.LoggingInterceptor
import com.bhaskarblur.gptbot.network.OpenAiInterceptor
import com.bhaskarblur.sync_realtimecontentwriting.core.utils.PasswordUtil
import com.bhaskarblur.sync_realtimecontentwriting.data.local.SharedPreferencesManager
import com.bhaskarblur.sync_realtimecontentwriting.data.remote.ApiRoutes
import com.bhaskarblur.sync_realtimecontentwriting.data.remote.FirebaseManager
import com.bhaskarblur.sync_realtimecontentwriting.data.remote.utils.ApiClient
import com.bhaskarblur.sync_realtimecontentwriting.data.repository.ChatGptRepositoryImpl
import com.bhaskarblur.sync_realtimecontentwriting.data.repository.DocumentRepositoryImpl
import com.bhaskarblur.sync_realtimecontentwriting.data.repository.UserRepositoryImpl
import com.bhaskarblur.sync_realtimecontentwriting.domain.repository.IChatGptRepo
import com.bhaskarblur.sync_realtimecontentwriting.domain.repository.IDocumentRepository
import com.bhaskarblur.sync_realtimecontentwriting.domain.repository.IUserRepository
import com.bhaskarblur.sync_realtimecontentwriting.domain.use_case.DocumentUseCase
import com.bhaskarblur.sync_realtimecontentwriting.domain.use_case.GptUseCase
import com.bhaskarblur.sync_realtimecontentwriting.domain.use_case.UserUseCase
import com.bhaskarblur.sync_realtimecontentwriting.presentation.document.DocumentViewModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModules {

    @Provides
    @Singleton
    fun providesPassUtil() : PasswordUtil {
        return PasswordUtil
    }
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
    fun providesFirebaseManager(@Named("docsRef") databaseReference: DatabaseReference,
                              @Named("usersRef") usersRef: DatabaseReference,
                                sharedPreferencesManager: SharedPreferencesManager
                                ) : FirebaseManager {
        return FirebaseManager(databaseReference, usersRef, sharedPreferencesManager)
    }

    @Provides
    @Singleton
    fun providesFirebaseDatabase(context: Context) : FirebaseDatabase {
        return FirebaseDatabase.getInstance(FirebaseManager.DB_URL(context))
    }

    @Provides
    @Singleton
    fun providesUserRepository(firebaseManager : FirebaseManager, sharedPreferencesManager: SharedPreferencesManager,
                               passUtil:PasswordUtil) : IUserRepository {
        return UserRepositoryImpl(firebaseManager, sharedPreferencesManager,passUtil)
    }
    @Provides
    @Singleton
    fun providesDocumentRepository(firebaseManager : FirebaseManager) : IDocumentRepository {
        return DocumentRepositoryImpl(firebaseManager)
    }

    @Provides
    @Singleton
    fun providesUserUseCase(userRepository: IUserRepository) : UserUseCase {
        return UserUseCase(userRepository)
    }

    @Provides
    @Singleton
    fun providesDocumentUseCase(documentRepo : IDocumentRepository) : DocumentUseCase {
        return DocumentUseCase(documentRepo)
    }

    @Provides
    @Singleton
    fun providesDocumentViewModel(documentUseCase: DocumentUseCase, userRepository: IUserRepository
    , gptUseCase: GptUseCase) : DocumentViewModel {
        return DocumentViewModel(documentUseCase,userRepository,gptUseCase)
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


    @Provides
    @Singleton
    fun returnApiRoutes (apiClient : Retrofit) : ApiRoutes =
        apiClient.create(ApiRoutes::class.java);


    @Singleton
    @Provides
    fun okHttpClient(interceptor: Interceptor) : OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.MINUTES) // Change it as per your requirement
            .readTimeout(5, TimeUnit.MINUTES)// Change it as per your requirement
            .writeTimeout(5, TimeUnit.MINUTES)
            .addInterceptor(interceptor)
            .addInterceptor(LoggingInterceptor())
            .build()
    }

    @Singleton
    @Provides
    fun apiClient(@ApplicationContext context: Context) : Retrofit = ApiClient.getInstance(okHttpClient((OpenAiInterceptor(context))), ApiRoutes.BASE_URL(context))

    @Singleton
    @Provides
    fun provideChatGptRepo(apiImpl: ApiRoutes) : IChatGptRepo {
        return ChatGptRepositoryImpl(apiImpl)
    }

    @Singleton
    @Provides
    fun provideChatGptUseCase(chatGptRepo: IChatGptRepo) : GptUseCase {
        return GptUseCase(chatGptRepo)
    }
}