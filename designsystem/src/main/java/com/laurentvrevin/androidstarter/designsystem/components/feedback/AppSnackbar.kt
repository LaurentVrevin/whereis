package com.laurentvrevin.androidstarter.designsystem.components.feedback

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.laurentvrevin.androidstarter.designsystem.theme.AppTheme
import com.laurentvrevin.androidstarter.designsystem.ui.SnackbarType

@Composable
fun AppSnackbar(
    snackbarData: SnackbarData,
    type: SnackbarType = SnackbarType.Default,
    modifier: Modifier = Modifier,
) {
    val colors = AppTheme.colors
    val shapes = AppTheme.shapes
    val spacing = AppTheme.spacing

    val backgroundColor =
        when (type) {
            SnackbarType.Default -> colors.inverseSurface
            SnackbarType.Success -> Color(0xFF4CAF50)
            SnackbarType.Error -> colors.error
            SnackbarType.Warning -> Color(0xFFFF9800)
        }

    val contentColor =
        when (type) {
            SnackbarType.Default -> colors.inverseOnSurface
            else -> Color.White
        }

    Snackbar(
        modifier = modifier.padding(spacing.standard),
        containerColor = backgroundColor,
        contentColor = contentColor,
        shape = shapes.medium,
        action = {
            snackbarData.visuals.actionLabel?.let { actionLabel ->
                TextButton(
                    onClick = { snackbarData.performAction() },
                    colors = ButtonDefaults.textButtonColors(contentColor = contentColor),
                ) {
                    Text(actionLabel)
                }
            }
        },
    ) {
        Text(snackbarData.visuals.message)
    }
}
