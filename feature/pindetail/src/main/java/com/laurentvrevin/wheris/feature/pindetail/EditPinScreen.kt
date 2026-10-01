package com.laurentvrevin.wheris.feature.pindetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.laurentvrevin.wheris.core.designsystem.foundation.WherisSpacing

@Composable
fun EditPinScreen(
    uiState: EditPinUiState,
    onNameChange: (String) -> Unit,
    onNoteChange: (String) -> Unit,
    onSave: () -> Unit,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.imePadding().verticalScroll(rememberScrollState()).padding(WherisSpacing.lg),
            verticalArrangement = Arrangement.spacedBy(WherisSpacing.md),
        ) {
            Text(stringResource(R.string.editpin_title), style = MaterialTheme.typography.headlineSmall)
            when (uiState) {
                EditPinUiState.Loading, EditPinUiState.Saved -> CircularProgressIndicator()
                EditPinUiState.NotFound -> Text(stringResource(R.string.pindetail_not_found_description))
                EditPinUiState.LoadFailed -> {
                    Text(stringResource(R.string.pindetail_error_title))
                    Button(onClick = onRetry) { Text(stringResource(R.string.editpin_retry)) }
                }
                is EditPinUiState.Content -> {
                    OutlinedTextField(
                        value = uiState.name,
                        onValueChange = onNameChange,
                        label = { Text(stringResource(R.string.editpin_name)) },
                        singleLine = true,
                        enabled = !uiState.isSaving,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    OutlinedTextField(
                        value = uiState.note,
                        onValueChange = onNoteChange,
                        label = { Text(stringResource(R.string.editpin_note)) },
                        minLines = 4,
                        enabled = !uiState.isSaving,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    if (uiState.saveFailed || uiState.noLongerExists) {
                        Text(
                            text =
                                stringResource(
                                    if (uiState.noLongerExists) R.string.editpin_missing else R.string.editpin_save_error,
                                ),
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                    Button(
                        onClick = onSave,
                        enabled = !uiState.isSaving && !uiState.noLongerExists,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(stringResource(if (uiState.isSaving) R.string.editpin_saving else R.string.editpin_save))
                    }
                }
            }
            OutlinedButton(
                onClick = onBack,
                enabled = (uiState as? EditPinUiState.Content)?.isSaving != true && uiState != EditPinUiState.Saved,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.pindetail_cancel))
            }
        }
    }
}
