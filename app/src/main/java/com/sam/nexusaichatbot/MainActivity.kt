package com.sam.nexusaichatbot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.hilt.navigation.compose.hiltViewModel
import com.sam.nexusaichatbot.presentation.screen.ChatScreen
import com.sam.nexusaichatbot.presentation.viewmodel.ChatViewModel
import com.sam.nexusaichatbot.ui.theme.NexusAIChatbotTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContent {
            NexusAIChatbotTheme {
                val viewModel: ChatViewModel = hiltViewModel()
                ChatScreen(viewModel)
            }
        }
    }
}

