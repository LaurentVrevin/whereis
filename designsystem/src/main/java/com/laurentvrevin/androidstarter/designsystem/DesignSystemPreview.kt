package com.laurentvrevin.androidstarter.designsystem

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.laurentvrevin.androidstarter.designsystem.components.button.AppOutlinedButton
import com.laurentvrevin.androidstarter.designsystem.components.button.AppPrimaryButton
import com.laurentvrevin.androidstarter.designsystem.components.button.AppSecondaryButton
import com.laurentvrevin.androidstarter.designsystem.components.card.AppCard
import com.laurentvrevin.androidstarter.designsystem.components.chip.AppChip
import com.laurentvrevin.androidstarter.designsystem.styles.LocalCardStyles
import com.laurentvrevin.androidstarter.designsystem.theme.AppTheme

@Preview(showBackground = true)
@Composable
fun DesignSystemStylesPreview() {
    AppTheme {
        val cardStyles = LocalCardStyles.current
        val spacing = AppTheme.spacing

        Column(
            modifier =
                Modifier
                    .padding(spacing.standard)
                    .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(spacing.standard),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(spacing.small)) {
                AppPrimaryButton(text = "Primary", onClick = {})
                AppSecondaryButton(text = "Secondary", onClick = {})
                AppOutlinedButton(text = "Outlined", onClick = {})
            }

            AppCard {
                Text("Ceci est une Card par défaut")
            }

            AppCard(style = cardStyles.elevated()) {
                Text("Ceci est une Card avec élévation")
            }

            var chipSelected by remember { mutableStateOf(false) }
            Row(horizontalArrangement = Arrangement.spacedBy(spacing.small)) {
                AppChip(
                    text = "Chip Interactive",
                    selected = chipSelected,
                    modifier = Modifier.clickable { chipSelected = !chipSelected },
                )
                AppChip(text = "Chip Selected", selected = true)
                AppChip(text = "Chip Normal", selected = false)
            }
        }
    }
}
