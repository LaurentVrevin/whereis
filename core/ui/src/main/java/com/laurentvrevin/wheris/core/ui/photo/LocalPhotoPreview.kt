package com.laurentvrevin.wheris.core.ui.photo

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CancellationException

/** Platform preview slot: the stateless form itself never resolves files. */
@Composable
fun LocalPhotoPreview(
    key: Any,
    load: suspend () -> ImageBitmap,
    description: String,
    failureText: String,
    onFailure: () -> Unit,
) {
    var bitmap by remember(key) { mutableStateOf<ImageBitmap?>(null) }
    var failed by remember(key) { mutableStateOf(false) }
    LaunchedEffect(key) {
        try {
            bitmap = load()
        } catch (exception: CancellationException) {
            throw exception
        } catch (_: Exception) {
            failed = true
            onFailure()
        }
    }
    when {
        bitmap != null ->
            Image(
                bitmap = bitmap!!,
                contentDescription = description,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxWidth().height(200.dp),
            )
        failed -> Text(failureText)
        else -> CircularProgressIndicator()
    }
}

/** Synthetic local landscape; no external media or filesystem in design previews. */
@Composable
fun SyntheticPhotoPreview() {
    val sky = MaterialTheme.colorScheme.primaryContainer
    val landscape = MaterialTheme.colorScheme.primary
    Canvas(Modifier.fillMaxWidth().height(160.dp)) {
        drawRect(sky)
        drawCircle(landscape, radius = size.width * .35f, center = Offset(size.width * .25f, size.height))
        drawCircle(landscape, radius = size.width * .45f, center = Offset(size.width * .85f, size.height * 1.1f))
    }
}
