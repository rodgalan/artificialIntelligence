package com.rodgalan.artificialIntelligence.infrastructure

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import dev.langchain4j.model.ollama.OllamaEmbeddingModel
import org.springframework.core.io.ClassPathResource
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.nio.charset.StandardCharsets

@RestController
class InitCoursesController(private val courseRepository: CourseRepository) {

    @GetMapping("init")
    fun init(): ResponseEntity<String> {
        return try {
            val jsonContent =ClassPathResource("courses.json").inputStream.readBytes()
            val mapper = ObjectMapper()
            val root: JsonNode = mapper.readTree(jsonContent)

            val embeddingModel = OllamaEmbeddingModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("nomic-embed-text")
                .build()

            if (root.isArray) {
                root.forEach { course ->
                    val id = course["id"]?.asText() ?: ""
                    val name = course["name"]?.asText() ?: ""
                    val embedding = embeddingModel.embed(name).content().vectorAsList()

                    courseRepository.saveCourse(id, name, embedding)
                    println("Name: $name, Embedding: $mapper.writeValueAsString(embedding)")
                }
            }

            ResponseEntity.ok(jsonContent.toString(StandardCharsets.UTF_8))

        } catch (e: Exception) {
            ResponseEntity.internalServerError().body("Failed to load courses data: ${e.message}")
        }
    }

}