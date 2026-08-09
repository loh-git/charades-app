package com.zoomi.charades.ads

import android.app.Activity
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull

// Safety-valve only, not an SDK-tuned value: if the consent SDK's callbacks never fire (network
// hang, SDK issue), ad initialization still proceeds after this long instead of stalling app
// launch indefinitely — same "never block the user" principle as GameScreen's ad-dismiss timeout.
private const val CONSENT_TIMEOUT_MS = 8_000L

class ConsentManager {

    // Resolves once the consent flow is settled (form shown and dismissed, not required, or
    // errored) or the timeout elapses — whichever comes first. Callers gate ad initialization on
    // this completing, never on any particular outcome, so a consent failure never blocks ads.
    // Any synchronous SDK exception is swallowed for the same reason — it must never propagate
    // and block app startup.
    suspend fun requestConsentIfNeeded(activity: Activity) {
        withTimeoutOrNull(CONSENT_TIMEOUT_MS) {
            try {
                suspendCancellableCoroutine<Unit> { continuation ->
                    val params = ConsentRequestParameters.Builder()
                        // U8 resolved: Charades is a general-audience app, not directed at children.
                        .setTagForUnderAgeOfConsent(false)
                        .build()
                    val consentInformation = UserMessagingPlatform.getConsentInformation(activity)
                    consentInformation.requestConsentInfoUpdate(
                        activity,
                        params,
                        {
                            UserMessagingPlatform.loadAndShowConsentFormIfRequired(activity) { _ ->
                                if (continuation.isActive) continuation.resume(Unit)
                            }
                        },
                        { _ ->
                            if (continuation.isActive) continuation.resume(Unit)
                        },
                    )
                }
            } catch (e: Exception) {
                // Fall through — ad initialization proceeds regardless of consent SDK failures.
            }
        }
    }
}
