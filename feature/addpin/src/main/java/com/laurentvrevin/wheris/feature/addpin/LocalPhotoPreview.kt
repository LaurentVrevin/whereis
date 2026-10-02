package com.laurentvrevin.wheris.feature.addpin

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.stringResource
import com.laurentvrevin.wheris.core.model.PhotoDraftReference
import com.laurentvrevin.wheris.core.photo.AndroidPhotoStorage

@Composable
internal fun LocalPhotoPreview(
    photo: PhotoDraftReference,
    storage: AndroidPhotoStorage,
    onFailure: () -> Unit,
) = com.laurentvrevin.wheris.core.ui.photo.LocalPhotoPreview(
    photo,
    { storage.preview(photo).asImageBitmap() },
    stringResource(R.string.details_preview),
    stringResource(R.string.details_photo_error),
    onFailure,
)

@Composable
internal fun SyntheticPhotoPreview() = com.laurentvrevin.wheris.core.ui.photo.SyntheticPhotoPreview()
