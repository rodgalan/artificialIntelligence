package com.rodgalan.artificialIntelligence.infrastructure

import dev.langchain4j.data.segment.TextSegment
import dev.langchain4j.model.embedding.EmbeddingModel
import dev.langchain4j.model.ollama.OllamaEmbeddingModel
import dev.langchain4j.store.embedding.EmbeddingStore
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RAGConfiguration {

    @Bean
    fun embeddingModelForVectors(): EmbeddingModel =
        OllamaEmbeddingModel.builder()
            .baseUrl("http://localhost:11434")
            .modelName("nomic-embed-text")
            .build()

    @Bean
    fun embeddingStore(embeddingModel: EmbeddingModel): EmbeddingStore<TextSegment> =
        PgVectorEmbeddingStore.builder()
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

    @Bean
    fun scrapingRepository(embeddingModel: EmbeddingModel, embeddingStore: EmbeddingStore<TextSegment>) =
        ScrapingRepository(embeddingModel, embeddingStore)


}