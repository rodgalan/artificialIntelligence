package com.rodgalan.artificialIntelligence.infrastructure

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.pgvector.PGvector
import dev.langchain4j.model.ollama.OllamaEmbeddingModel
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class CourseRepository(private val jdbcTemplate: NamedParameterJdbcTemplate) {

    fun save(course: Course) {
        val mapper = ObjectMapper()

        val embeddingModel = OllamaEmbeddingModel.builder()
            .baseUrl("http://localhost:11434")
            .modelName("nomic-embed-text")
            .build()

        val params = mapOf(
                "id" to course.id,
                "name" to course.name,
                "summary" to course.summary,
                "categories" to mapper.writeValueAsString(course.categories),
                "published_at" to course.published_at,
                "embedding" to PGvector(embeddingModel.embed(embedingString(course)).content().vectorAsList())
        )

        val sql = """
        INSERT INTO courses (id, name, summary, categories, published_at, embedding)
        VALUES (:id, :name, :summary, :categories::jsonb, :published_at, :embedding)
        ON CONFLICT (id) DO UPDATE SET
            name = :name,
            summary = :summary,
            categories = :categories::jsonb,
            published_at = :published_at,
            embedding = :embedding
    """

        jdbcTemplate.update(sql, params)
    }

    private fun find(courseIds: List<String>): List<Course> {
        val sql =
            """ 
                select id, name, summary, categories, published_at from courses
                where id in (:ids)
            """.trimIndent()

        val params = MapSqlParameterSource(
            mapOf("ids" to courseIds)
        )

       return  jdbcTemplate.query(sql, params, mapAd())
    }

    private fun embedingString(course: Course) = """
      - name: ${course.name}
        summary: ${course.summary}
        categories: ${course.categories.joinToString(", ")}
        published_at: ${course.published_at}
    """.trimIndent()

    private fun embedingString(courses: List<Course>) =
        courses.map { embedingString(it) }.joinToString("\n")

    fun findSimilar(courseIds: List<String>): List<Course> {
        val embeddingModel = OllamaEmbeddingModel.builder()
            .baseUrl("http://localhost:11434")
            .modelName("nomic-embed-text")
            .build()

        val patternCourses =  find(courseIds)

        val sql = """
            select id, name, summary, categories, published_at from courses where id not in (:ids) order by embedding <-> :embedding limit 5
        """.trimIndent()

        val params = MapSqlParameterSource(
            mapOf(
                "embedding" to PGvector(embeddingModel.embed(embedingString(patternCourses)).content().vectorAsList()),
                "ids" to courseIds
            )
        )

        return jdbcTemplate.query(sql, params, mapAd())
    }

    private fun mapAd(): RowMapper<Course> {
        val mapper = jacksonObjectMapper()
        return RowMapper { rs: ResultSet, _: Int ->
            Course(
                rs.getString("id"),
                rs.getString("name"),
                rs.getString("summary"),
                rs.getString("categories")?.let{mapper.readValue(rs.getString("categories"), object : TypeReference<List<String>?>() {})} ?: emptyList(),
                rs.getDate("published_at")
            )
        }
    }


}