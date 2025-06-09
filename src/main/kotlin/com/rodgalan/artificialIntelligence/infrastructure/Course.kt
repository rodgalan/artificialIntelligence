package com.rodgalan.artificialIntelligence.infrastructure

import java.util.Date

data class Course(
    val id: String,
    val name: String,
    val summary: String,
    val categories: List<String>,
    val published_at: Date
)
