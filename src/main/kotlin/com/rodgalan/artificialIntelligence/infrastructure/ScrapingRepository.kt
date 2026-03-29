package com.rodgalan.artificialIntelligence.infrastructure

import dev.langchain4j.data.document.loader.FileSystemDocumentLoader
import dev.langchain4j.data.document.parser.TextDocumentParser
import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser
import dev.langchain4j.data.document.splitter.DocumentByParagraphSplitter
import dev.langchain4j.data.segment.TextSegment
import dev.langchain4j.model.embedding.EmbeddingModel
import dev.langchain4j.model.ollama.OllamaChatModel
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever
import dev.langchain4j.service.AiServices
import dev.langchain4j.store.embedding.EmbeddingStore
import org.springframework.stereotype.Repository
import java.nio.file.FileSystems

@Repository
class ScrapingRepository(
    private val embeddingModel: EmbeddingModel,
    private val embeddingStore: EmbeddingStore<TextSegment>
) {

    fun load() =
        matcherWithParser
            .flatMap { FileSystemDocumentLoader.loadDocuments(DOCUMENTS_PATH, it.key, it.value) }
            .forEach {
                val embed = embeddingModel.embed(it.toTextSegment())
                embeddingStore.add(embed.content(), it.toTextSegment())
            }

    fun loadChunkingByParagraph() {
        val maxSegmentSizeInChars = 300
        val maxOverlapSizeInChars = 20
        val splitter = DocumentByParagraphSplitter(
            maxSegmentSizeInChars, maxOverlapSizeInChars
        )

        matcherWithParser
            .flatMap { FileSystemDocumentLoader.loadDocuments(DOCUMENTS_PATH, it.key, it.value) }
            .forEach { splitter.split(it)
                .forEach {
                    val embed = embeddingModel.embed(it)
                    embeddingStore.add(embed.content(), it)
                    }
            }
    }

    fun get(userRequest: String): String {
        val contentRetriever = EmbeddingStoreContentRetriever.builder()
            .embeddingModel(embeddingModel)
            .embeddingStore(embeddingStore)
            .maxResults(5)
            .build()

        val modelName = "gemma";
        val ollamaServer = "http://localhost:11434";
        val chatModel = OllamaChatModel.builder().baseUrl(ollamaServer).modelName(modelName).build()

        val assistant =
            AiServices.builder(Assistant::class.java).chatModel(chatModel).contentRetriever(contentRetriever).build()

        return assistant.chat(userRequest)
    }

    companion object {
        private const val DOCUMENTS_PATH =
            "/Users/anna.rodriguez/Documents/Anna/artificialIntelligence/src/main/resources/scraping"
        private val matcherWithParser = mapOf(
            FileSystems.getDefault().getPathMatcher("glob:*.txt") to TextDocumentParser(),
            //FileSystems.getDefault().getPathMatcher("glob:*.pdf") to ApachePdfBoxDocumentParser() -- TODO : Not working
        )
    }
}

interface Assistant {
    fun chat(userMessage: String): String
}