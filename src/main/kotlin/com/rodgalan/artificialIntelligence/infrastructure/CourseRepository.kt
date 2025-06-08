package com.rodgalan.artificialIntelligence.infrastructure

import com.pgvector.PGvector;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.core.namedparam.SqlParameterSource
import org.springframework.stereotype.Repository

@Repository
class CourseRepository(private val jdbcTemplate: NamedParameterJdbcTemplate) {

    fun saveCourse(id: String, name: String, embedding: List<Float>) {

        val params = mapOf(
            "id" to id,
            "name" to name,
            "embedding" to PGvector(embedding)
        )

        val sql = """
        INSERT INTO courses (id, name, embedding)
        VALUES (:id, :name, :embedding)
        ON CONFLICT (id) DO UPDATE SET
            name = :name,
            embedding = :embedding
    """

        jdbcTemplate.update(sql, params)
    }

    fun find(embedding: List<Float>): List<String> {
        val sql = """
            select name from courses order by embedding <-> :embedding limit 5
        """.trimIndent()

        val params = MapSqlParameterSource(
            mapOf("embedding" to PGvector(embedding))
        )

        return jdbcTemplate.queryForList(sql, params, String::class.java)
    }

}