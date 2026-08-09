package com.zoomi.charades.ads

import android.app.Activity
import android.content.Context

interface AdProvider {
    suspend fun initialize(context: Context)
    suspend fun loadInterstitial(): Boolean
    fun showInterstitial(activity: Activity, onDismissed: () -> Unit)
}
