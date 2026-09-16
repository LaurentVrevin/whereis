@file:Suppress("ktlint:standard:no-wildcard-imports")

package com.laurentvrevin.androidstarter.designsystem.patterns

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.laurentvrevin.androidstarter.designsystem.theme.AppTheme

@Composable
fun SectionBlock(
    title: String,
    content: @Composable () -> Unit,
) {
    val spacing = AppTheme.spacing
    val typography = AppTheme.typography

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = typography.labelMedium,
            color = AppTheme.colors.primary,
        )
        Spacer(modifier = Modifier.height(spacing.small))
        content()
    }
}
