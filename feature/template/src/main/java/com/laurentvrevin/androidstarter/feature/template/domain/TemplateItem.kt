package com.laurentvrevin.androidstarter.feature.template.domain

import kotlinx.serialization.Serializable

/**
 * Modèle de données d'exemple pour la feature Template.
 */
@Serializable
data class TemplateItem(
    val id: Int,
    val title: String,
    val description: String,
)
