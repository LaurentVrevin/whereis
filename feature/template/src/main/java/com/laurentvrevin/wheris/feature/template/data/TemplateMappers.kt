package com.laurentvrevin.wheris.feature.template.data

import com.laurentvrevin.wheris.data.local.database.template.TemplateEntity
import com.laurentvrevin.wheris.feature.template.domain.TemplateItem

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
