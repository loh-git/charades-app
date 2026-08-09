package com.zoomi.charades.ads

import android.app.Activity
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NoOpAdProviderTest {

    @Test
    fun `initialize completes without throwing`() = runTest {
        NoOpAdProvider().initialize(Activity())
    }

    @Test
    fun `loadInterstitial always returns false`() = runTest {
        assertFalse(NoOpAdProvider().loadInterstitial())
    }

    @Test
    fun `showInterstitial invokes onDismissed immediately without throwing`() {
        var dismissed = false

        NoOpAdProvider().showInterstitial(Activity()) { dismissed = true }

        assertTrue(dismissed)
    }
}
