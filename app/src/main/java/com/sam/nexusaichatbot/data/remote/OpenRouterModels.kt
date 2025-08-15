package com.sam.nexusaichatbot.data.remote


data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val groupId: Long
)

data class OpenRouterMessage(
    val role: String,
    val content: String
)

data class OpenRouterRequest(
    val model: String = "mistral-7b",
    val messages: List<OpenRouterMessage>
)

data class OpenRouterResponse(
    val choices: List<Choice>
)

data class Choice(
    val message: OpenRouterMessage
)


//data class OpenRouterRequest(
//    val model: String = "mistral-7b",
//    val messages: List<OpenRouterMessage>
//)
//
//data class OpenRouterMessage(
//    val role: String,
//    val content: String
//)
//
//data class OpenRouterResponse(
//    val choices: List<Choice>
//)
//
//data class Choice(
//    val message: OpenRouterMessage
//)


