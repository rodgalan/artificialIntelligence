package com.rodgalan.artificialIntelligence.infrastructure

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.core.io.ClassPathResource
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.nio.charset.StandardCharsets

@RestController
class CoursesController(private val courseRepository: CourseRepository) {

    @PostMapping("init")
    fun init(): ResponseEntity<String> {
        return try {
            val jsonContent =ClassPathResource("courses.json").inputStream.readBytes()
            val mapper = ObjectMapper().registerModule(KotlinModule.Builder().build())
            val courses = mapper.readValue(jsonContent, object: TypeReference<List<Course>>(){})

            courses.forEach{courseRepository.save(it)}

            ResponseEntity.ok(jsonContent.toString(StandardCharsets.UTF_8))

        } catch (e: Exception) {
            ResponseEntity.internalServerError().body("Failed to load courses data: ${e.message}")
        }
    }

    @GetMapping("findSimilar")
    fun find(@RequestParam courseIds: String): ResponseEntity<String>{
        val result = courseRepository.findSimilar(courseIds.split(",").map { it.trim() })
        return ResponseEntity.ok(result.joinToString { it.name })
    }

    @GetMapping("findSimilarAndRecent")
    fun findSimilarAndRecent(@RequestParam courseIds: String): ResponseEntity<String>{
        val result = courseRepository.findSimilarAndRecent(courseIds.split(",").map { it.trim() })
        return ResponseEntity.ok(result.joinToString { it.name })
    }
}