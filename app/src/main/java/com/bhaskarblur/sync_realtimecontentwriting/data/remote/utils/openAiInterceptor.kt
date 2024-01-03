package com.bhaskarblur.gptbot.network

import android.content.Context
import android.util.Log
import com.bhaskarblur.sync_realtimecontentwriting.data.remote.ApiRoutes
import okhttp3.Interceptor
import okhttp3.Response

class OpenAiInterceptor(
    private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request();
        val authenticatedRequest = request.newBuilder()
            .addHeader("Authorization", "Bearer ${ApiRoutes.API_KEY(context)}")
            .build();

        Log.d("headers",authenticatedRequest.headers.get("Authorization")!!);
        return chain.proceed(authenticatedRequest);
    }
}
