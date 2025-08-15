package com.sam.nexusaichatbot.data.remote

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface OpenRouterApiService {
    @POST("chat/completions")
    suspend fun sendMessage(
        @Body request: OpenRouterRequest,
        @Header("Authorization") auth: String,
        @Header("Content-Type") contentType: String = "application/json"
    ): OpenRouterResponse
}