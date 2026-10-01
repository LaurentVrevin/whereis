package com.laurentvrevin.wheris.feature.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOff
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import com.laurentvrevin.wheris.core.designsystem.foundation.WherisSpacing

@Composable
fun OnboardingScreen(
    page: OnboardingPage,
    onContinue: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier.fillMaxSize().safeDrawingPadding(),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(WherisSpacing.lg),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(WherisSpacing.xs),
            ) {
                Text(
                    text = stringResource(R.string.onboarding_brand),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Text(
                    text = stringResource(R.string.onboarding_progress, page.ordinal + 1, OnboardingPage.entries.size),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            key(page) {
                Column(
                    modifier =
                        Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = WherisSpacing.xl, vertical = WherisSpacing.lg),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(WherisSpacing.xl),
                ) {
                    PageIntroduction(page)
                    when (page) {
                        OnboardingPage.WELCOME -> Unit
                        OnboardingPage.CONCEPT -> ConceptContent()
                        OnboardingPage.PRIVACY -> PrivacyContent()
                    }
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth().padding(WherisSpacing.lg),
                verticalArrangement = Arrangement.spacedBy(WherisSpacing.sm),
            ) {
                Button(
                    onClick = onContinue,
                    modifier = Modifier.fillMaxWidth().heightIn(min = WherisSpacing.xxxxl),
                ) {
                    Text(stringResource(R.string.onboarding_continue))
                }
                if (page != OnboardingPage.WELCOME) {
                    TextButton(
                        onClick = onBack,
                        modifier = Modifier.fillMaxWidth().heightIn(min = WherisSpacing.xxxxl),
                    ) {
                        Text(stringResource(R.string.onboarding_back))
                    }
                }
            }
        }
    }
}

@Composable
private fun PageIntroduction(
    page: OnboardingPage,
    modifier: Modifier = Modifier,
) {
    val illustration =
        when (page) {
            OnboardingPage.WELCOME -> Icons.Default.Place
            OnboardingPage.CONCEPT -> Icons.Default.BookmarkBorder
            OnboardingPage.PRIVACY -> Icons.Default.Shield
        }
    val title =
        when (page) {
            OnboardingPage.WELCOME -> R.string.onboarding_welcome_title
            OnboardingPage.CONCEPT -> R.string.onboarding_concept_title
            OnboardingPage.PRIVACY -> R.string.onboarding_privacy_title
        }
    val description =
        when (page) {
            OnboardingPage.WELCOME -> R.string.onboarding_welcome_description
            OnboardingPage.CONCEPT -> R.string.onboarding_concept_description
            OnboardingPage.PRIVACY -> R.string.onboarding_privacy_description
        }
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(WherisSpacing.xl),
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        ) {
            Icon(
                imageVector = illustration,
                contentDescription = null,
                modifier = Modifier.padding(WherisSpacing.xl).size(WherisSpacing.xxxxxl),
            )
        }
        Text(
            text = stringResource(title),
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.semantics { heading() },
        )
        Text(
            text = stringResource(description),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun ConceptContent(modifier: Modifier = Modifier) {
    val steps =
        listOf(
            Icons.Default.Place to R.string.onboarding_concept_here,
            Icons.Default.FavoriteBorder to R.string.onboarding_concept_interest,
            Icons.Default.BookmarkBorder to R.string.onboarding_concept_save,
            Icons.Default.Explore to R.string.onboarding_concept_find,
        )
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(WherisSpacing.sm),
    ) {
        steps.forEachIndexed { index, (icon, text) ->
            InformationRow(icon = icon, text = stringResource(text))
            if (index < steps.lastIndex) {
                Icon(
                    imageVector = Icons.Default.ArrowDownward,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

@Composable
private fun PrivacyContent(modifier: Modifier = Modifier) {
    val facts =
        listOf(
            Icons.Default.Smartphone to R.string.onboarding_privacy_local,
            Icons.Default.PersonOutline to R.string.onboarding_privacy_account,
            Icons.Default.LocationOff to R.string.onboarding_privacy_tracking,
            Icons.Default.CloudOff to R.string.onboarding_privacy_server,
        )
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(WherisSpacing.md),
    ) {
        facts.forEach { (icon, text) ->
            InformationRow(icon = icon, text = stringResource(text))
        }
    }
}

@Composable
private fun InformationRow(
    icon: ImageVector,
    text: String,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(WherisSpacing.lg).semantics(mergeDescendants = true) {},
            horizontalArrangement = Arrangement.spacedBy(WherisSpacing.md),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f),
            )
        }
    }
}
