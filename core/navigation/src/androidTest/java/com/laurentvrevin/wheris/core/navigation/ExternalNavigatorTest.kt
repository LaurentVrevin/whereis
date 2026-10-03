package com.laurentvrevin.wheris.core.navigation

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.laurentvrevin.wheris.core.model.GeoPoint
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ExternalNavigatorTest {
    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val destination = GeoPoint(-12.5, 135.25)

    @Test fun compatibleTargetReceivesOnlyCoordinatesThroughSystemChooser() {
        var checked: Intent? = null
        var launched: Intent? = null
        val navigator =
            AndroidExternalNavigator(context, {
                checked = it
                true
            }, { launched = it })
        assertEquals(ExternalNavigationResult.Launched, navigator.navigate(destination))
        assertEquals(Intent.ACTION_VIEW, checked!!.action)
        assertEquals("geo:-12.5,135.25?q=-12.5,135.25", checked!!.dataString)
        assertNull(checked!!.extras)
        assertNull(checked!!.clipData)
        assertNull(checked!!.`package`)
        assertNull(checked!!.component)
        assertEquals(0, checked!!.flags)
        assertEquals(Intent.ACTION_CHOOSER, launched!!.action)
        @Suppress("DEPRECATION")
        val target = launched!!.getParcelableExtra<Intent>(Intent.EXTRA_INTENT)!!
        assertEquals(checked!!.data, target.data)
        assertNull(target.extras)
    }

    @Test fun noCompatibleTargetDoesNotLaunch() {
        var calls = 0
        val navigator = AndroidExternalNavigator(context, { false }, { calls++ })
        assertEquals(ExternalNavigationResult.Unavailable, navigator.navigate(destination))
        assertEquals(0, calls)
        assertNotNull(ExternalNavigationResult.Unavailable.message(context))
    }

    @Test fun appRemovedBetweenCheckAndLaunchIsUnavailable() {
        val navigator = AndroidExternalNavigator(context, { true }, { throw ActivityNotFoundException() })
        assertEquals(ExternalNavigationResult.Unavailable, navigator.navigate(destination))
    }

    @Test fun launchSecurityFailureIsRecoverable() {
        val navigator = AndroidExternalNavigator(context, { true }, { throw SecurityException() })
        assertEquals(ExternalNavigationResult.Failed, navigator.navigate(destination))
        assertNotNull(ExternalNavigationResult.Failed.message(context))
    }

    @Test fun resolverFailureIsRecoverableAndSuccessHasNoErrorMessage() {
        val navigator = AndroidExternalNavigator(context, { throw IllegalStateException() }, { error("Must not launch") })
        assertEquals(ExternalNavigationResult.Failed, navigator.navigate(destination))
        assertNull(ExternalNavigationResult.Launched.message(context))
    }
}
