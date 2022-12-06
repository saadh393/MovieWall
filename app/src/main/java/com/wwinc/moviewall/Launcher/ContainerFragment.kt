package com.wwinc.moviewall.Launcher

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdViewAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxAdView
import com.applovin.sdk.AppLovinSdk
import com.google.android.material.tabs.TabLayoutMediator
import com.wwinc.moviewall.R
import com.wwinc.moviewall.adapters.TabLayout_Adapter
import com.wwinc.moviewall.databinding.FragmentContainerBinding


class ContainerFragment : Fragment(), MaxAdViewAdListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    lateinit var binding: FragmentContainerBinding

    private var adView: MaxAdView? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_container, container, false)
        binding.viewpager.adapter = TabLayout_Adapter(requireActivity())

        (activity as AppCompatActivity).supportActionBar?.title = "Home"

        val tablayout_mediator: TabLayoutMediator = TabLayoutMediator(
            binding.tabLayout,
            binding.viewpager,
            TabLayoutMediator.TabConfigurationStrategy { tab, position ->
                when (position) {
                    0 -> tab.setText("Popular")
                    1 -> tab.setText("Top Rated")
                }
            }
        )

        tablayout_mediator.attach()


        // ---------------------------------------------------------------------------
        // Make sure to set the mediation provider value to "max" to ensure proper functionality
        // Make sure to set the mediation provider value to "max" to ensure proper functionality
        AppLovinSdk.getInstance(context).mediationProvider = "max"
        AppLovinSdk.initializeSdk(context) {
            createBannerAd();
            adView?.loadAd()
        }
        // ---------------------------------------------------------------------------

        return binding.root

    }

    fun createBannerAd() {
        adView = MaxAdView("927d3397f1348c61", context)
        adView?.setListener(this)

        // Stretch to the width of the screen for banners to be fully functional
        val width = ViewGroup.LayoutParams.MATCH_PARENT

        // Banner height on phones and tablets is 50 and 90, respectively
        val heightPx = resources.getDimensionPixelSize(R.dimen.banner_height)

        adView?.layoutParams = FrameLayout.LayoutParams(width, heightPx)


        val rootView: LinearLayout? = view?.findViewById(R.id.adholder);
        rootView?.gravity = Gravity.TOP
        rootView?.addView(adView)

        // Load the ad
        adView?.loadAd()
    }


    override fun onAdLoaded(ad: MaxAd?) {
        Log.d("123as123", "Banner ad loaded successfully: ")
    }

    override fun onAdDisplayed(ad: MaxAd?) {
        Log.d("123as123", "onAdDisplayed ")
    }


    override fun onAdHidden(ad: MaxAd?) {
        Log.d("123as123", "onAdHidden ")

    }

    override fun onAdClicked(ad: MaxAd?) {
        Log.d("123as123", "onAdClicked")

    }

    override fun onAdLoadFailed(adUnitId: String?, error: MaxError?) {
        Log.d("123as123", "onAdLoadFailed ")
    }

    override fun onAdDisplayFailed(ad: MaxAd?, error: MaxError?) {
        Log.d("123as123", "onAdDisplayFailed")
    }

    override fun onAdExpanded(ad: MaxAd?) {
        Log.d("123as123", "onAdExpanded")
    }

    override fun onAdCollapsed(ad: MaxAd?) {
        Log.d("123as123", "onAdCollapsed")
    }


    override fun onDestroy() {
        super.onDestroy()
    }


}

