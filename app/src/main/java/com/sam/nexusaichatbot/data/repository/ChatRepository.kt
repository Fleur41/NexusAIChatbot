package com.sam.nexusaichatbot.data.repository

import com.sam.nexusaichatbot.data.local.ChatDao
import com.sam.nexusaichatbot.data.local.ChatEntity
import com.sam.nexusaichatbot.data.remote.OpenRouterApiService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ChatRepository @Inject constructor(
    private val dao: ChatDao,
) {
    fun getAllMessages(): Flow<List<ChatEntity>> = dao.getAllMessages()

    suspend fun insert(message: ChatEntity) = dao.insertMessage(message)

    suspend fun clearAll() = dao.clearAll()

    suspend fun clearOnlyAI() = dao.deleteAllAiMessages()

    suspend fun deleteGroup(groupId: Long) = dao.deleteGroup(groupId)

}