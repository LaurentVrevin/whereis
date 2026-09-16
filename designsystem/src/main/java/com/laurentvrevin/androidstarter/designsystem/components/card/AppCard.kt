package com.laurentvrevin.androidstarter.designsystem.components.card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.styleable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.laurentvrevin.androidstarter.designsystem.theme.AppTheme

@OptIn(ExperimentalFoundationStyleApi::class)
@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    style: Style? = null,
    content: @Composable () -> Unit,
) {
    val cardStyle = style ?: AppTheme.cardStyles.default()

    Column(
        modifier = modifier.styleable(style = cardStyle),
    ) {
        content()
    }
}

@Preview(showBackground = true)
@Composable
private fun AppCardPreview() {
    AppTheme {
        Column(
            modifier = Modifier.padding(AppTheme.spacing.standard),
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.standard),
        ) {
            AppCard {
                Text("Default Card Content")
            }
            AppCard(style = AppTheme.cardStyles.elevated()) {
                Text("Elevated Card Content")
            }
            AppCard(style = AppTheme.cardStyles.outlined()) {
                Text("Outlined Card Content")
            }
        }
    }
}
