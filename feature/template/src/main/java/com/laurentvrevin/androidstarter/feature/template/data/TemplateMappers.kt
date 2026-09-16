package com.laurentvrevin.androidstarter.feature.template.data

import com.laurentvrevin.androidstarter.data.local.database.template.TemplateEntity
import com.laurentvrevin.androidstarter.feature.template.domain.TemplateItem

fun TemplateEntity.toExternalModel() =
    TemplateItem(
        id = id,
        title = title,
        description = description,
    )

fun TemplateItem.toEntity() =
    TemplateEntity(
        id = id,
        title = title,
        description = description,
    )
