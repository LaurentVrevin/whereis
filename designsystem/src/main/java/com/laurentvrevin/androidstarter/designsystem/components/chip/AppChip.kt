package com.laurentvrevin.androidstarter.designsystem.components.chip

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.rememberUpdatedStyleState
import androidx.compose.foundation.style.styleable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.laurentvrevin.androidstarter.designsystem.foundation.AppSize
import com.laurentvrevin.androidstarter.designsystem.theme.AppTheme

@OptIn(ExperimentalFoundationStyleApi::class)
@Composable
fun AppChip(
    text: String,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    size: AppSize = AppSize.Small,
    style: Style? = null,
) {
    val chipStyle = style ?: AppTheme.chipStyles.default(size)
    val styleState =
        rememberUpdatedStyleState(null) {
            it.isSelected = selected
        }

    Text(
        text = text,
        modifier = modifier.styleable(styleState, chipStyle),
    )
}

@Preview(showBackground = true)
@Composable
private fun AppChipPreview() {
    AppTheme {
        Row(
            modifier = Modifier.padding(AppTheme.spacing.standard),
            horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.small),
        ) {
            AppChip(text = "Small Chip")
            AppChip(text = "Medium Chip", size = AppSize.Medium)
            AppChip(text = "Selected Chip", selected = true)
        }
    }
}
