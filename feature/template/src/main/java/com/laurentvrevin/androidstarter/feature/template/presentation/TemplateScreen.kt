package com.laurentvrevin.androidstarter.feature.template.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.laurentvrevin.androidstarter.designsystem.components.button.AppPrimaryButton
import com.laurentvrevin.androidstarter.designsystem.components.card.AppCard
import com.laurentvrevin.androidstarter.designsystem.components.feedback.EmptyState
import com.laurentvrevin.androidstarter.designsystem.components.feedback.LoadingOverlay
import com.laurentvrevin.androidstarter.designsystem.components.topbar.AppTopBar
import com.laurentvrevin.androidstarter.designsystem.theme.AppTheme
import com.laurentvrevin.androidstarter.feature.template.R
import com.laurentvrevin.androidstarter.feature.template.domain.TemplateItem
import org.koin.androidx.compose.koinViewModel

@Composable
fun TemplateScreen(
    onBack: () -> Unit,
    viewModel: TemplateViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val newTitle = stringResource(R.string.template_item_title_new)
    val newDesc = stringResource(R.string.template_item_desc_new)

    Scaffold(
        topBar = {
            AppTopBar(
                title = stringResource(R.string.template_title),
                onBackClick = onBack,
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                viewModel.addTemplate(newTitle, newDesc)
            }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.template_action_add),
                )
            }
        },
    ) { innerPadding ->
        Box(
            modifier =
                Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
        ) {
            if (state.items.isEmpty() && !state.isInitialLoading) {
                val itemTitle = stringResource(R.string.template_item_title_1)
                val itemDesc = stringResource(R.string.template_item_desc_1)
                EmptyState(
                    message = stringResource(R.string.template_empty_msg),
                    action = {
                        AppPrimaryButton(
                            text = stringResource(R.string.template_btn_add_template),
                            onClick = { viewModel.addTemplate(itemTitle, itemDesc) },
                        )
                    },
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(AppTheme.spacing.standard),
                    verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.standard),
                ) {
                    items(state.items, key = { it.id }) { item ->
                        TemplateItemCard(
                            item = item,
                            onDelete = { viewModel.deleteTemplate(item.id) },
                        )
                    }
                }
            }

            state.error?.let { error ->
                Snackbar(
                    modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp),
                    action = {
                        TextButton(onClick = { /* Refresh could be triggered here */ }) {
                            Text(stringResource(R.string.template_action_retry))
                        }
                    },
                ) {
                    Text(error.asString())
                }
            }

            LoadingOverlay(isLoading = state.isInitialLoading)
        }
    }
}

@Composable
private fun TemplateItemCard(
    item: TemplateItem,
    onDelete: () -> Unit,
) {
    AppCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(AppTheme.spacing.standard),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(item.title, style = AppTheme.typography.titleLarge)
                Spacer(Modifier.height(4.dp))
                Text(item.description, style = AppTheme.typography.bodyLarge)
            }
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.template_action_delete),
                    tint = AppTheme.colors.error,
                )
            }
        }
    }
}
