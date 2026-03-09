package com.wegielek.katanaflashlight.presentation.ui.views.ad

import android.app.Activity
import android.util.Log
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

object InterstitialAdManager {
    private var interstitialAd: InterstitialAd? = null
    private var isLoading = false // Flag to prevent duplicate loads

    fun loadAd(
        activity: Activity,
        adUnitId: String,
    ) {
        // Early return if already loading
        if (isLoading) {
            Log.d("InterstitialAd", "Ad is already loading, skipping load request")
            return
        }

        isLoading = true // Mark as loading
        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(
            activity,
            adUnitId,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    isLoading = false
                    Log.d("InterstitialAd", "Ad loaded")
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    interstitialAd = null
                    isLoading = false
                    Log.d("InterstitialAd", "Ad failed to load: ${error.message}")
                }
            },
        )
    }

    fun showAd(
        activity: Activity,
        onDismiss: () -> Unit = {},
    ) {
        interstitialAd?.let { ad ->
            ad.fullScreenContentCallback =
                object : com.google.android.gms.ads.FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        Log.d("InterstitialAd", "Ad dismissed")
                        onDismiss()
                        interstitialAd = null // reload next time
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: com.google.android.gms.ads.AdError) {
                        Log.d("InterstitialAd", "Ad failed to show")
                        onDismiss()
                    }

                    override fun onAdShowedFullScreenContent() {
                        Log.d("InterstitialAd", "Ad shown")
                    }
                }
            ad.show(activity)
        } ?: onDismiss()
    }
}
