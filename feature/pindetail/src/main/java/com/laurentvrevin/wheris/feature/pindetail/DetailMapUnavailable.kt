package com.laurentvrevin.wheris.feature.pindetail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.laurentvrevin.wheris.core.designsystem.foundation.WherisSpacing

@Composable
internal fun DetailMapUnavailable(onRetry: (() -> Unit)? = null) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(WherisSpacing.lg)) {
            Text(stringResource(R.string.pindetail_map_unavailable))
            if (onRetry != null) {
                TextButton(onClick = onRetry) { Text(stringResource(R.string.editpin_retry)) }
            }
        }
    }
}
