package com.laurentvrevin.androidstarter.feature.template.data

import com.laurentvrevin.androidstarter.data.local.database.template.TemplateDao
import com.laurentvrevin.androidstarter.data.local.database.template.TemplateEntity
import com.laurentvrevin.androidstarter.feature.template.domain.TemplateItem
import com.laurentvrevin.androidstarter.feature.template.domain.TemplateRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TemplateRepositoryImpl(
    private val dao: TemplateDao,
) : TemplateRepository {
    override fun getTemplates(): Flow<List<TemplateItem>> {
        return dao.getAllTemplates().map { entities ->
            entities.map { it.toExternalModel() }
        }
    }

    override suspend fun addTemplate(
        title: String,
        description: String,
    ) {
        dao.upsert(
            TemplateEntity(
                title = title,
                description = description,
            ),
        )
    }

    override suspend fun deleteTemplate(id: Int) {
        dao.deleteById(id)
    }
}
