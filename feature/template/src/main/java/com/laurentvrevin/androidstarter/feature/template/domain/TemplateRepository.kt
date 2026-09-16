package com.laurentvrevin.androidstarter.feature.template.domain

import kotlinx.coroutines.flow.Flow

interface TemplateRepository {
    fun getTemplates(): Flow<List<TemplateItem>>

    suspend fun addTemplate(
        title: String,
        description: String,
    )

    suspend fun deleteTemplate(id: Int)
}
