package com.zoomi.charades.ads

import android.app.Activity
import android.content.Context

class NoOpAdProvider : AdProvider {
    override suspend fun initialize(context: Context) {}

    override suspend fun loadInterstitial(): Boolean = false

    override fun showInterstitial(activity: Activity, onDismissed: () -> Unit) {
        onDismissed()
    }
}
