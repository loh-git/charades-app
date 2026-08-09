package com.zoomi.charades.ads

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import kotlin.coroutines.resume
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine

class AdMobAdProvider(private val adUnitId: String) : AdProvider {

    // Owns its own scope so a consumed ad is silently replaced in the background (after every
    // show, dismiss, or failure) without the call site ever needing to know to reload one —
    // showInterstitial() only ever acts on whatever is already cached.
    private val providerScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private lateinit var appContext: Context
    private var interstitialAd: InterstitialAd? = null
    private var loadInProgress = false

    override suspend fun initialize(context: Context) {
        appContext = context.applicationContext
        try {
            suspendCancellableCoroutine<Unit> { continuation ->
                MobileAds.initialize(appContext) {
                    if (continuation.isActive) continuation.resume(Unit)
                }
            }
        } catch (e: Exception) {
            return
        }
        providerScope.launch { loadInterstitial() }
    }

    // Guarded against re-entrant calls (e.g. the post-initialize preload racing a
    // reload-after-show triggered moments later) so at most one InterstitialAd.load() is ever
    // in flight — otherwise a slower second call could overwrite/discard a valid ad the first
    // call already cached. Any SDK exception is treated the same as a normal load failure so it
    // never propagates to the caller.
    override suspend fun loadInterstitial(): Boolean {
        if (interstitialAd != null) return true
        if (!::appContext.isInitialized || loadInProgress) return false
        loadInProgress = true
        return try {
            suspendCancellableCoroutine { continuation ->
                InterstitialAd.load(
                    appContext,
                    adUnitId,
                    AdRequest.Builder().build(),
                    object : InterstitialAdLoadCallback() {
                        override fun onAdLoaded(ad: InterstitialAd) {
                            interstitialAd = ad
                            if (continuation.isActive) continuation.resume(true)
                        }

                        override fun onAdFailedToLoad(error: LoadAdError) {
                            interstitialAd = null
                            if (continuation.isActive) continuation.resume(false)
                        }
                    },
                )
            }
        } catch (e: Exception) {
            false
        } finally {
            loadInProgress = false
        }
    }

    override fun showInterstitial(activity: Activity, onDismissed: () -> Unit) {
        val ad = interstitialAd
        interstitialAd = null
        if (ad == null) {
            onDismissed()
            providerScope.launch { loadInterstitial() }
            return
        }
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                onDismissed()
                providerScope.launch { loadInterstitial() }
            }

            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                onDismissed()
                providerScope.launch { loadInterstitial() }
            }
        }
        try {
            ad.show(activity)
        } catch (e: Exception) {
            onDismissed()
            providerScope.launch { loadInterstitial() }
        }
    }
}
