package com.wwinc.moviewall.detailsView

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.databinding.DataBindingUtil
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
//import com.google.android.gms.ads.InterstitialAd
import com.mopub.mobileads.dfp.adapters.MoPubAdapter
import com.wwinc.moviewall.R
import com.wwinc.moviewall.databinding.ActivityDetailsViewBinding



class DetailsView : AppCompatActivity() {

    private var mInterstitialAd: InterstitialAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var binding : ActivityDetailsViewBinding = DataBindingUtil.setContentView(this, R.layout.activity_details_view)
//        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        (supportActionBar as ActionBar).hide()

        var adUnit: String = resources.getString(R.string.interstital_ad)
        var adRequest = AdRequest.Builder().addNetworkExtrasBundle(MoPubAdapter::class.java, MoPubAdapter.BundleBuilder().setPrivacyIconSize(15).build())
            .build()
        InterstitialAd.load(this,adUnit, adRequest, object  : InterstitialAdLoadCallback () {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                mInterstitialAd = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                mInterstitialAd = interstitialAd
            }
        })

    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (mInterstitialAd != null) {
            mInterstitialAd?.show(this)
        }
    }
}