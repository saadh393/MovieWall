package com.wwinc.moviewall.detailsView.ViewPager

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.jirbo.adcolony.AdColonyAdapter
import com.jirbo.adcolony.AdColonyBundleBuilder
import com.mopub.mobileads.dfp.adapters.MoPubAdapter
import com.wwinc.moviewall.R
import com.wwinc.moviewall.adapters.HorizentalViewpager_Adapter
import com.wwinc.moviewall.database.DatabaseWallpaper
import com.wwinc.moviewall.databinding.ViewPagerFragmentBinding
import com.wwinc.moviewall.networking.Wallpaper_ModelItem
import com.wwinc.moviewall.repository.RepositoryWallpaper
import java.util.*


class ViewPagerFragment : Fragment() {

    lateinit var viewPager_Adapter : HorizentalViewpager_Adapter
    lateinit var sharedPreferences : SharedPreferences
    lateinit var bindig : ViewPagerFragmentBinding
    lateinit var wallpaper_url : LiveData<List<Wallpaper_ModelItem>>
    lateinit var randomWorkout : ArrayList<Wallpaper_ModelItem>
    var position : Int = 0
    private var mAdView: AdView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {

        bindig  = DataBindingUtil.inflate(inflater, R.layout.view_pager_fragment, container, false)

        randomWorkout = activity?.intent?.extras?.getParcelableArrayList<Wallpaper_ModelItem>("wallpaper_model")!!
//        viewPager_Adapter = HorizentalViewpager_Adapter(randomWorkout as ArrayList, context)

        val databaseRef = DatabaseWallpaper.getDatabase(inflater.context)
        val repository = RepositoryWallpaper(databaseRef)
        wallpaper_url = repository.popularWallpaperData
        sharedPreferences  = context?.getSharedPreferences("POSITION_TRACKER", Context.MODE_PRIVATE)!!

//        viewPager_Adapter = HorizentalViewpager_Adapter(randomWorkout, context, bindig)
//        position = activity?.intent?.extras?.getInt("POSTITION") ?:0
//        bindig.horizentalViewPager.adapter = viewPager_Adapter
//        bindig.horizentalViewPager.currentItem = position


        // Ads
        AdColonyBundleBuilder.setShowPrePopup(true)
        AdColonyBundleBuilder.setShowPostPopup(true)

        mAdView = bindig.adView
        val adRequest: AdRequest = AdRequest.Builder()
            .addNetworkExtrasBundle(AdColonyAdapter::class.java, AdColonyBundleBuilder.build())
            .addNetworkExtrasBundle(MoPubAdapter::class.java, MoPubAdapter.BundleBuilder().setMinimumBannerWidth(300).setMinimumBannerHeight(49).setPrivacyIconSize(15).build())
            .build()
        mAdView!!.loadAd(adRequest)



            if(sharedPreferences.getString("POSTITION_2", null) == null){
                viewPager_Adapter = HorizentalViewpager_Adapter(randomWorkout, context, bindig)
                position = activity?.intent?.extras?.getInt("POSTITION") ?:0
                bindig.horizentalViewPager.adapter = viewPager_Adapter
                bindig.horizentalViewPager.currentItem = activity?.intent?.extras?.getInt("POSTITION") ?:0

            }else{

                wallpaper_url.observe(viewLifecycleOwner, Observer {
                        mainItem->
                    for (x in 0 until randomWorkout.size){
                        val singleItem = mainItem.filter { it.imageURL == randomWorkout[x].imageURL }
                        if (singleItem[0].favOrNot != randomWorkout[x].favOrNot){
                            randomWorkout[x].favOrNot = singleItem[0].favOrNot
                        }
                    }
                    viewPager_Adapter = HorizentalViewpager_Adapter(randomWorkout, context, bindig)
                    position = activity?.intent?.extras?.getInt("POSTITION") ?:0
                    bindig.horizentalViewPager.adapter = viewPager_Adapter
                    val newPosition = sharedPreferences.getString("POSTITION_2", null)
                    bindig.horizentalViewPager.currentItem = newPosition!!.toInt()

                })



            }





        return bindig.root
    }

    override fun onResume() {
        super.onResume()
//        wallpaper_url.observe(viewLifecycleOwner, Observer {
//                mainItem->
//                for (x in 0 until randomWorkout.size){
//                    val singleItem = mainItem.filter { it.imageURL == randomWorkout[x].imageURL }
//                    if (singleItem[0].favOrNot != randomWorkout[x].favOrNot){
//                        Log.d("123as123", " Not Match \t\t  :  " + randomWorkout[x]);
//                    }
//                    randomWorkout[x].favOrNot = singleItem[0].favOrNot
//
//                }
//
//            viewPager_Adapter = HorizentalViewpager_Adapter(randomWorkout, context, bindig)
//            position = activity?.intent?.extras?.getInt("POSTITION") ?:0
//            bindig.horizentalViewPager.adapter = viewPager_Adapter
//
//            if(sharedPreferences.getString("POSTITION_2", null) == null){
//                bindig.horizentalViewPager.currentItem = position
//
//            }else{
//                viewPager_Adapter.notifyDataSetChanged()
//                val newPosition = sharedPreferences.getString("POSTITION_2", null)
//                bindig.horizentalViewPager.currentItem = newPosition!!.toInt()
//            }
//
//        })





//        Log.d("123as123", "After  \t\t  :  " + randomWorkout[position]);
//        viewPager_Adapter = HorizentalViewpager_Adapter(randomWorkout, context, bindig)
//        bindig.horizentalViewPager.adapter = viewPager_Adapter
    }

}