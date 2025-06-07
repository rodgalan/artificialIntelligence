package com.rodgalan.artificialIntelligence.infrastructure

import com.pgvector.PGvector;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
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

}