package com.sam.nexusaichatbot.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import dagger.hilt.android.qualifiers.ApplicationContext

@Database(entities = [ChatEntity::class], version = 1)
abstract class ChatDatabase: RoomDatabase() {
    abstract fun chatDao(): ChatDao

    companion object {

        fun getInstance(@ApplicationContext context: Context): ChatDatabase{
            return Room.databaseBuilder(context, ChatDatabase::class.java, "chat_database").build()
        }
//        @Volatile
//        private var INSTANCE: ChatDatabase? = null
//
//        fun getInstance(context: Context): ChatDatabase {
//            return INSTANCE ?: synchronized(this){
//                INSTANCE ?: Room.databaseBuilder(
//                    context.applicationContext,
//                    ChatDatabase::class.java,
//                    "chat_database"
//                ).build().also { INSTANCE = it }
//            }
//        }

    }
}