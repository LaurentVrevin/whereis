package com.laurentvrevin.androidstarter.designsystem.components.button

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.rememberUpdatedStyleState
import androidx.compose.foundation.style.styleable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.laurentvrevin.androidstarter.designsystem.foundation.AppSize
import com.laurentvrevin.androidstarter.designsystem.theme.AppTheme

/**
 * Bouton générique du design system utilisant l'API Styles.
 */
@OptIn(ExperimentalFoundationStyleApi::class)
@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    style: Style? = null,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    interactionSource: MutableInteractionSource? = null,
) {
    val buttonStyle = style ?: AppTheme.buttonStyles.primary(AppSize.Medium)
    val spacing = AppTheme.spacing
    val interactionSource = interactionSource ?: remember { MutableInteractionSource() }
    val styleState =
        rememberUpdatedStyleState(interactionSource) {
            it.isEnabled = enabled
        }

    Row(
        modifier =
            modifier
                .clickable(
                    onClick = onClick,
                    enabled = enabled,
                    interactionSource = interactionSource,
                    indication = LocalIndication.current,
                )
                .styleable(styleState, buttonStyle),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (leadingIcon != null) {
            leadingIcon()
            Spacer(Modifier.width(spacing.small))
        }

        Text(text = text)

        if (trailingIcon != null) {
            Spacer(Modifier.width(spacing.small))
            trailingIcon()
        }
    }
}

/**
 * Raccourcis pour les variantes communes utilisant les styles injectés.
 */

@Composable
fun AppPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    size: AppSize = AppSize.Medium,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
) = AppButton(
    text = text,
    onClick = onClick,
    modifier = modifier,
    enabled = enabled,
    style = AppTheme.buttonStyles.primary(size),
    leadingIcon = leadingIcon,
    trailingIcon = trailingIcon,
)

@Composable
fun AppSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    size: AppSize = AppSize.Medium,
) = AppButton(
    text = text,
    onClick = onClick,
    modifier = modifier,
    enabled = enabled,
    style = AppTheme.buttonStyles.secondary(size),
)

@Composable
fun AppOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    size: AppSize = AppSize.Medium,
) = AppButton(
    text = text,
    onClick = onClick,
    modifier = modifier,
    enabled = enabled,
    style = AppTheme.buttonStyles.outlined(size),
)

@Composable
fun AppGhostButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    size: AppSize = AppSize.Medium,
) = AppButton(
    text = text,
    onClick = onClick,
    modifier = modifier,
    enabled = enabled,
    style = AppTheme.buttonStyles.ghost(size),
)

@Composable
fun AppDangerButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    size: AppSize = AppSize.Medium,
) = AppButton(
    text = text,
    onClick = onClick,
    modifier = modifier,
    enabled = enabled,
    style = AppTheme.buttonStyles.danger(size),
)

@Preview(showBackground = true)
@Composable
private fun AppButtonPreview() {
    AppTheme {
        Column(
            modifier = Modifier.padding(AppTheme.spacing.standard),
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.small),
        ) {
            AppPrimaryButton(text = "Primary Medium", onClick = {})
            AppPrimaryButton(text = "Primary Small", size = AppSize.Small, onClick = {})
            AppPrimaryButton(text = "Primary Large", size = AppSize.Large, onClick = {})
            AppSecondaryButton(text = "Secondary", onClick = {})
            AppOutlinedButton(text = "Outlined", onClick = {})
            AppGhostButton(text = "Ghost", onClick = {})
            AppDangerButton(text = "Danger", onClick = {})
        }
    }
}
