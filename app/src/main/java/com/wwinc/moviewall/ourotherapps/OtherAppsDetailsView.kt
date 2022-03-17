package com.wwinc.moviewall.ourotherapps

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.android.material.transition.platform.MaterialContainerTransform
import com.wwinc.moviewall.BuildConfig
import com.wwinc.moviewall.R
import com.wwinc.moviewall.databinding.FragmentOtherAppsDetailsViewBinding

class OtherAppsDetailsView : Fragment() {

    val args : OtherAppsDetailsViewArgs by navArgs()

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        sharedElementEnterTransition = MaterialContainerTransform()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       val binding : FragmentOtherAppsDetailsViewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_other_apps_details_view, container, false)

        val imageURL  = args.imageurl
        val coverPhoto  = args.coverPhoto
        val transName = args.transitionName
        val appName = args.appName
        val appDes = args.appDescription
        val pack = args.packageName


        binding.appLogo.transitionName = transName
        binding.appName.text = appName
        binding.appDescription.text = appDes
        Glide
            .with(requireContext())
            .load(imageURL)
            .thumbnail(Glide.with(requireContext()).load(R.drawable.placeholder))
            .into(binding.appLogo)

        Glide
            .with(requireContext())
            .load(coverPhoto)
            .into(binding.cover)

        binding.button.setOnClickListener {
            try {
                context?.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=" + pack + "&showAllReviews=true")
                    )
                )

            } catch (e: ActivityNotFoundException) {
                context?.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=" + pack + "&showAllReviews=true")
                    )
                )
            }
        }







        return binding.root

    }

}