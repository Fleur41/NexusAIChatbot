package com.sam.nexusaichatbot.presentation.viewmodel


import android.Manifest
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sam.nexusaichatbot.BuildConfig
import com.sam.nexusaichatbot.data.local.ChatEntity
import com.sam.nexusaichatbot.data.remote.ChatMessage
import com.sam.nexusaichatbot.data.remote.OpenRouterApiService
import com.sam.nexusaichatbot.data.remote.OpenRouterMessage
import com.sam.nexusaichatbot.data.remote.OpenRouterRequest
import com.sam.nexusaichatbot.data.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject




@HiltViewModel
class ChatViewModel @Inject constructor(
    private val api: OpenRouterApiService,
    private val repository: ChatRepository
) : ViewModel() {
//    private val _chatList = MutableStateFlow<List<ChatMessage>>(emptyList())
//    val chatList: StateFlow<List<ChatMessage>> get() = _chatList.asStateFlow()

    private val _chatList = mutableStateOf<List<ChatMessage>>(emptyList())
    val chatList: State<List<ChatMessage>> = _chatList

    private val _isTyping = mutableStateOf(false)
    val isTyping: State<Boolean> = _isTyping

    private  val messageHistory = mutableListOf<OpenRouterMessage>()

    init {
        viewModelScope.launch {
            repository.getAllMessages().collect{ messages ->
                _chatList.value = messages.map {
                    ChatMessage(it.text, it.isUser, it.groupId)
                }
            }
            messageHistory.clear()
            messageHistory.addAll(chatList.value.map {
                OpenRouterMessage(if (it.isUser) "user" else "assistant", it.text)
            })
        }
    }
    fun clearAllChats(){
        viewModelScope.launch {
            repository.clearAll()
        }
    }

    fun clearOnlyAI(){
        viewModelScope.launch {
            repository.clearOnlyAI()
        }
    }

    fun deletePair(groupId: Long){
        viewModelScope.launch {
            repository.deleteGroup(groupId)
        }
    }

    companion object{
        private const val TAG = "ChatViewModel"
    }
    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    fun sendMessage(userInput: String, context: Context){
        if (userInput.isBlank()) return
        if(!isNetworkAvailable(context)){
            val currentMessages = _chatList.value.toMutableList() // Get a mutable copy
            currentMessages.add(ChatMessage("❌ No internet connection", false, -1L))
            _chatList.value = currentMessages.toList()
//            _chatList.value = _chatList +
//                    ChatMessage("❌ No internet connection", false, -1L)
            return
        }
        val groupId = System.currentTimeMillis()
        val userMessage = ChatMessage(userInput, true, groupId)
        val userMessages = _chatList.value.toMutableList()
        userMessages.add(userMessage)
        _chatList.value = userMessages.toList()
        messageHistory.add(OpenRouterMessage("user", userInput))

        _isTyping.value = true
        viewModelScope.launch {
            try {
                repository.insert(
                    ChatEntity(
                        text = userInput,
                        isUser = true,
                        groupId = groupId
                    )
                )
                val response = api.sendMessage(
                    OpenRouterRequest(
                        model = "mistralai/mistral-7b-instruct",
                        messages = messageHistory
                    ),
                    auth = "Bearer ${BuildConfig.OPENROUTER_API_KEY}"
                )
                Log.d(TAG, "API Response: $response")
//                Log.d("TAG", "API Response: $response")

                val reply = response.choices.firstOrNull()?.message?.content ?: "❌ No response"
                val aiMessage = ChatMessage(reply, false, groupId)
                val aiMessages = _chatList.value.toMutableList()
                aiMessages.add(aiMessage)
                _chatList.value = aiMessages.toList()

                messageHistory.add(OpenRouterMessage("assistant", reply))

                repository.insert(
                    ChatEntity(
                        text = reply,
                        isUser = false,
                        groupId = groupId
                    )
                )
            } catch (e: Exception) {
                val currentMessages = _chatList.value.toMutableList()
                currentMessages.add(ChatMessage("❌ Error: ${e.message}", false, -1L))
                _chatList.value = currentMessages.toList()
            } finally {
                _isTyping.value = false
            }
        }

    }

    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    private fun isNetworkAvailable(context: Context): Boolean{
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return  capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

}
