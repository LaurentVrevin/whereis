package com.laurentvrevin.wheris.core.navigation

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import com.laurentvrevin.wheris.core.model.GeoPoint

enum class ExternalNavigationResult { Launched, Unavailable, Failed }

fun interface ExternalNavigator {
    fun navigate(destination: GeoPoint): ExternalNavigationResult
}

/** User-initiated, coordinates-only handoff. No preferred app is persisted. */
class AndroidExternalNavigator(
    private val context: Context,
    private val canHandle: (Intent) -> Boolean = {
        context.packageManager.queryIntentActivities(it, PackageManager.MATCH_DEFAULT_ONLY).isNotEmpty()
    },
    private val launch: (Intent) -> Unit = context::startActivity,
) : ExternalNavigator {
    override fun navigate(destination: GeoPoint): ExternalNavigationResult =
        try {
            val coordinates = "${destination.latitude},${destination.longitude}"
            val target = Intent(Intent.ACTION_VIEW, Uri.parse("geo:$coordinates?q=$coordinates"))
            if (!canHandle(target)) {
                ExternalNavigationResult.Unavailable
            } else {
                launch(Intent.createChooser(target, context.getString(R.string.external_navigation_chooser)))
                ExternalNavigationResult.Launched
            }
        } catch (_: ActivityNotFoundException) {
            ExternalNavigationResult.Unavailable
        } catch (_: Exception) {
            // Never log the intent, destination, or exception payload.
            ExternalNavigationResult.Failed
        }
}

fun ExternalNavigationResult.message(context: Context): String? =
    when (this) {
        ExternalNavigationResult.Launched -> null
        ExternalNavigationResult.Unavailable -> context.getString(R.string.external_navigation_unavailable)
        ExternalNavigationResult.Failed -> context.getString(R.string.external_navigation_failed)
    }
