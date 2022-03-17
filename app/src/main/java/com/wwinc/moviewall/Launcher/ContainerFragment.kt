package com.wwinc.moviewall.Launcher

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment

import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.analytics.FirebaseAnalytics
import com.jirbo.adcolony.AdColonyAdapter
import com.jirbo.adcolony.AdColonyBundleBuilder
import com.mopub.mobileads.dfp.adapters.MoPubAdapter
import com.wwinc.moviewall.R
import com.wwinc.moviewall.adapters.TabLayout_Adapter
import com.wwinc.moviewall.databinding.FragmentContainerBinding
import com.wwinc.moviewall.popular.Popular_Wallpapers
import com.wwinc.moviewall.repository.RepositoryWallpaper


class ContainerFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    private var mAdView: AdView? = null
    lateinit var binding : FragmentContainerBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_container, container, false)
        binding.viewpager.adapter =TabLayout_Adapter(requireActivity())

        (activity as AppCompatActivity).supportActionBar?.title = "Home"

        // ads
        AdColonyBundleBuilder.setShowPrePopup(true)
        AdColonyBundleBuilder.setShowPostPopup(true)

        mAdView = binding.adView
        val adRequest: AdRequest = AdRequest.Builder()
            .addNetworkExtrasBundle(AdColonyAdapter::class.java, AdColonyBundleBuilder.build())
            .addNetworkExtrasBundle(MoPubAdapter::class.java, MoPubAdapter.BundleBuilder().setMinimumBannerWidth(300).setMinimumBannerHeight(49).setPrivacyIconSize(15).build())
            .build()
        mAdView!!.loadAd(adRequest)


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

        return binding.root
    }


}

