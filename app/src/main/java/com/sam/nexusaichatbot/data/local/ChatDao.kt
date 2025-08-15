package com.sam.nexusaichatbot.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {
    @Insert
    suspend fun insertMessage(message: ChatEntity)

    @Query("SELECT * FROM chat_messages ORDER BY id ASC")
    fun getAllMessages(): Flow<List<ChatEntity>>
//    suspend fun getAllMessages(): List<ChatEntity>


    @Query("DELETE FROM chat_messages WHERE isUser = 0")
    suspend fun deleteAllAiMessages()

    @Query("DELETE FROM chat_messages")
    suspend fun clearAll()

    @Query("DELETE FROM chat_messages WHERE groupId = :groupId")
    suspend fun deleteGroup(groupId: Long)
}