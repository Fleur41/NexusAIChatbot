package com.sam.nexusaichatbot.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sam.nexusaichatbot.data.remote.ChatMessage
import com.sam.nexusaichatbot.presentation.viewmodel.ChatViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    viewModel: ChatViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val chatList by viewModel.chatList
    val isTyping by viewModel.isTyping
    var userInput by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
//    val scrollState = rememberScrollState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row (
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
//                        horizontalArrangement = Arrangement.Center
                        ){
                        Text(
                            text = "NexusAI Chatbot \uD83E\uDD16",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontFamily = FontFamily.Serif,
                                fontStyle = FontStyle.Italic,
                                fontSize = 24.sp
                            )
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.LightGray)
                .padding(12.dp),
//                .verticalScroll(scrollState),
//            verticalArrangement = Arrangement.Center,
//            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            LaunchedEffect(chatList.size) {
                if (chatList.isNotEmpty()){
                    listState.animateScrollToItem(0)
                }
            }
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                state = listState,
                reverseLayout = true
            ) {
                items(chatList.reversed()){message ->
                    ChatBubble(message = message)
                    Spacer(modifier = Modifier.height(6.dp))
                }

                if (isTyping){
                    item{
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start
                        ){
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFF2E2E2E), RoundedCornerShape(18.dp))
                                    .padding(horizontal = 16.dp, vertical = 10.dp)
                            ) {
                                TypingDots()
                            }
                        }
                    }
                }
            }
            //Message input bar
            Row(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .background(Color(0xFF1E1E1E), shape = RoundedCornerShape(30.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ){
                TextField(
                    value = userInput,
                    onValueChange = {userInput = it},
                    modifier = Modifier
                        .weight(1f)
                        .background(Color.Transparent),
                    placeholder = { Text("Type your message...", color = Color.Green)},
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
                IconButton(
                    onClick = {
                        if (userInput.isNotBlank()){
                            viewModel.sendMessage(userInput, context)
                            userInput = ""
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Send",
                        tint = Color.White
                    )
                }
            }

            //Action buttons
            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ){
                Button(
                    onClick = {viewModel.clearAllChats()}
                ) {
                    Text("Clear All")
                }
                Button(
                    onClick = {viewModel.clearOnlyAI()}
                ) {
                    Text("Clear AI")
                }
                Button(
                    onClick = {
                        chatList.lastOrNull()?.groupId?.let {
                            viewModel.deletePair(it)
                        }
                    }
                ) {
                    Text("Delete Pair")
                }
            }

        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
//    val bubbleColor = if (message.isUser) Color(0xFF007AFF) else Color(0xFF2E2E2E)
    val bubbleColor = if (message.isUser) Color.Blue else Color.Yellow
    val textColor = if (message.isUser) Color.White else Color.Black
    val avatar = if (message.isUser) "👤" else "🤖"

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Top
    ){
        if (!message.isUser){
            Text(avatar, modifier = Modifier.padding(end = 4.dp))
        }
        Box(
            modifier = Modifier
                .background(bubbleColor, RoundedCornerShape(18.dp))
                .padding(12.dp)
                .widthIn(max = 300.dp)
        ) {
            Text(message.text, color = textColor)
        }

        if (message.isUser){
            Text(avatar, modifier = Modifier.padding(start = 4.dp))
        }
    }

}

@Composable
fun TypingDots() {
    var dotCount by remember { mutableIntStateOf(1) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(400)
            dotCount = (dotCount % 3) + 1
        }
    }

    Text(
        text = "Typing${".".repeat(dotCount)}",
        color = Color.DarkGray
    )

}
