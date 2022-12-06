package com.wwinc.moviewall.detailsView

import android.R
import android.app.Fragment
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxInterstitialAd
import com.wwinc.moviewall.databinding.ActivityDetailsViewBinding
import kotlinx.android.synthetic.main.fragment_container.*
import java.util.concurrent.TimeUnit


class DetailsView : AppCompatActivity(), MaxAdListener {

    private lateinit var interstitialAd: MaxInterstitialAd
    private var retryAttempt = 0.0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var binding: ActivityDetailsViewBinding =
            DataBindingUtil.setContentView(this, com.wwinc.moviewall.R.layout.activity_details_view)
        (supportActionBar as ActionBar).hide()


        createInterstitialAd()


    }

    fun createInterstitialAd() {
        interstitialAd = MaxInterstitialAd("e2c8393f17f141a3", this)
        interstitialAd.setListener(this)

        // Load the first ad
        interstitialAd.loadAd()
    }

    // MAX Ad Listener
    override fun onAdLoaded(maxAd: MaxAd) {
        // Interstitial ad is ready to be shown. interstitialAd.isReady() will now return 'true'

        // Reset retry attempt
        retryAttempt = 0.0
    }

    override fun onAdLoadFailed(adUnitId: String?, error: MaxError?) {
        // Interstitial ad failed to load
        // AppLovin recommends that you retry with exponentially higher delays up to a maximum delay (in this case 64 seconds)

        retryAttempt++
        val delayMillis =
            TimeUnit.SECONDS.toMillis(Math.pow(2.0, Math.min(6.0, retryAttempt)).toLong())

        Handler().postDelayed({ interstitialAd.loadAd() }, delayMillis)
    }

    override fun onAdDisplayFailed(ad: MaxAd?, error: MaxError?) {
        // Interstitial ad failed to display. AppLovin recommends that you load the next ad.
        interstitialAd.loadAd()
    }

    override fun onAdDisplayed(maxAd: MaxAd) {}

    override fun onAdClicked(maxAd: MaxAd) {}

    override fun onAdHidden(maxAd: MaxAd) {
        // Interstitial ad is hidden. Pre-load the next ad
        interstitialAd.loadAd()
    }

    override fun onBackPressed() {
        if (interstitialAd.isReady()) {
            interstitialAd.showAd();
        }
        super.onBackPressed()
    }


}