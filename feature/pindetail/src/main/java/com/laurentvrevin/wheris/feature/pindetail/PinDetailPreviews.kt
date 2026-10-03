package com.laurentvrevin.wheris.feature.pindetail

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.laurentvrevin.wheris.core.designsystem.theme.WherisTheme
import com.laurentvrevin.wheris.core.model.CardinalDirection
import com.laurentvrevin.wheris.core.model.GeoPoint
import com.laurentvrevin.wheris.core.model.PhotoReference
import com.laurentvrevin.wheris.core.model.Pin
import com.laurentvrevin.wheris.core.model.PinId
import com.laurentvrevin.wheris.core.model.SystemCategoryIds
import com.laurentvrevin.wheris.core.ui.photo.SyntheticPhotoPreview

private val previewPin =
    Pin(
        PinId("detail-preview"),
        GeoPoint(48.8, 2.3),
        SystemCategoryIds.CAR,
        null,
        null,
        1_700_000_000_000L,
        1_700_000_001_000L,
    )

@Composable
private fun DetailPreview(
    pin: Pin = previewPin,
    distance: Boolean = false,
    deletion: PinDeletionState = PinDeletionState.None,
) {
    PinDetailScreen(
        PinDetailUiState.Content(
            pin,
            distanceMeters = if (distance) 150.0 else null,
            cardinalDirection = if (distance) CardinalDirection.NORTH_EAST else null,
            deletion = deletion,
        ),
        onBack = {},
        onEdit = {},
        onNavigate = {},
        onDelete = {},
        onConfirmDeletion = {},
        onCancelDeletion = {},
        photoContent = { SyntheticPhotoPreview() },
    )
}

@Preview(showBackground = true, name = "Minimal / sans position actuelle")
@Composable
private fun MinimalDetail() {
    WherisTheme { DetailPreview() }
}

@Preview(showBackground = true, name = "Photo locale synthétique")
@Composable
private fun PhotoDetail() {
    WherisTheme { DetailPreview(previewPin.copy(photoReference = PhotoReference("synthetic"))) }
}

@Preview(showBackground = true, name = "Favori")
@Composable
private fun FavoriteDetail() {
    WherisTheme { DetailPreview(previewPin.copy(name = "Mon lieu", isFavorite = true)) }
}

@Preview(showBackground = true, name = "Note longue")
@Composable
private fun LongNoteDetail() {
    WherisTheme { DetailPreview(previewPin.copy(note = "Retrouver ce lieu en suivant le chemin. ".repeat(25))) }
}

@Preview(showBackground = true, name = "Toutes métadonnées")
@Composable
private fun MetadataDetail() {
    WherisTheme {
        DetailPreview(
            previewPin.copy(
                name = "Point de vue",
                note = "Prendre le sentier à droite.",
                isFavorite = true,
                accuracyMeters = 7f,
                altitudeMeters = 42.0,
                photoReference = PhotoReference("synthetic"),
            ),
            true,
        )
    }
}

@Preview(showBackground = true, name = "Position actuelle indisponible")
@Composable
private fun NoLocationDetail() {
    WherisTheme { DetailPreview(previewPin.copy(name = "Lieu toujours consultable", accuracyMeters = 7f)) }
}

@Preview(showBackground = true, name = "Carte indisponible")
@Composable
private fun UnavailableMapDetail() {
    WherisTheme { DetailPreview(previewPin.copy(name = "Lieu consultable hors ligne")) }
}

@Preview(showBackground = true, name = "Échec suppression")
@Composable
private fun FailedDeleteDetail() {
    WherisTheme { DetailPreview(deletion = PinDeletionState.Failed) }
}

@Preview(showBackground = true, name = "Dark", uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun DarkDetail() {
    WherisTheme(darkTheme = true) { DetailPreview() }
}

@Preview(showBackground = true, name = "Police 1.5", fontScale = 1.5f)
@Composable
private fun LargeFontDetail() {
    WherisTheme { DetailPreview(previewPin.copy(name = "Un lieu avec un nom très long")) }
}
