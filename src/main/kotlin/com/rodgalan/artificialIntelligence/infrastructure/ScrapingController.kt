package com.rodgalan.artificialIntelligence.infrastructure

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController()
class ScrapingController(private val scarapingRepository: ScrapingRepository) {

    @PostMapping("initDocs")
    fun init() {
        scarapingRepository.load()
    }

    @PostMapping("initDocsChunkingByParagraph")
    fun initChunkingByParagraph() {
        scarapingRepository.loadChunkingByParagraph()
    }

    @GetMapping("chatWithRAG")
    fun getResponse(@RequestBody request: String): String {
        return scarapingRepository.get(request)
    }
}