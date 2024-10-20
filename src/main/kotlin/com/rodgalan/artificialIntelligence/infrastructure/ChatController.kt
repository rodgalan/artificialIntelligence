package com.rodgalan.artificialIntelligence.infrastructure
import dev.langchain4j.model.ollama.OllamaChatModel
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController


@RestController
class ChatController {
    @GetMapping("chat")
    fun getResponse(@RequestBody request: String): String {
        val modelName = "gemma";
        val ollamaServer = "http://localhost:11434";
        val model = OllamaChatModel.builder().baseUrl(ollamaServer).modelName(modelName).build()
        return model.generate(request)
    }
}