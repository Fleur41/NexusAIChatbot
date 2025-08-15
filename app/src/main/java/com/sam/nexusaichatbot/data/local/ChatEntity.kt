package com.sam.nexusaichatbot.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_messages")
data class ChatEntity(
    @PrimaryKey(autoGenerate = true)
    val id : Int = 0,
    val text : String,
    val isUser : Boolean,
    val groupId : Long
)
