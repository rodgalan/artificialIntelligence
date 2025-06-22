package com.rodgalan.artificialIntelligence.infrastructure

import dev.langchain4j.data.document.loader.FileSystemDocumentLoader
import dev.langchain4j.data.document.parser.TextDocumentParser
import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser
import dev.langchain4j.model.ollama.OllamaEmbeddingModel
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore
import org.springframework.stereotype.Repository
import java.nio.file.FileSystems

@Repository
class ScrapingRepository() {

    fun load() {
        val embeddingModel = OllamaEmbeddingModel.builder()
            .baseUrl("http://localhost:11434")
            .modelName("nomic-embed-text")
            .build()

        val store = PgVectorEmbeddingStore.builder()
            .host("localhost")
            .port(5432)
            .user("anna")
            .password("123456789")
            .database("postgres")
            .dimension(embeddingModel.dimension())
            .createTable(true)
            .dropTableFirst(true)
            .table("documents")
            .build()

        val documents =
            matcherWithParser.flatMap { FileSystemDocumentLoader.loadDocuments(DOCUMENTS_PATH, it.key, it.value) }
        documents.forEach {
            val embed = embeddingModel.embed(it.toTextSegment())
            store.add(embed.content(), it.toTextSegment())
        }
    }

    companion object {
        private const val DOCUMENTS_PATH =
            "/Users/anna.rodriguez/Documents/Anna/artificialIntelligence/src/main/resources/scraping"
        private val matcherWithParser = mapOf(
            FileSystems.getDefault().getPathMatcher("glob:*.txt") to TextDocumentParser(),
            FileSystems.getDefault().getPathMatcher("glob:*.pdf") to ApachePdfBoxDocumentParser()
        )
    }
}