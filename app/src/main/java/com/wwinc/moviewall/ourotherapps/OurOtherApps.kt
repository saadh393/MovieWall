package com.wwinc.moviewall.ourotherapps

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.android.material.transition.platform.*

import com.wwinc.moviewall.R
import com.wwinc.moviewall.adapters.OtherAppsAdapter
import com.wwinc.moviewall.database.DatabaseWallpaper
import com.wwinc.moviewall.databinding.FragmentOurOtherAppsBinding
import com.wwinc.moviewall.repository.RepositoryWallpaper


class OurOtherApps : Fragment() {

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true);
//        enterTransition = MaterialContainerTransform()
        enterTransition = MaterialContainerTransform()

    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = DataBindingUtil.inflate<FragmentOurOtherAppsBinding>(inflater, R.layout.fragment_our_other_apps, container, false)

        // Handling Database
        val databaseRef = context?.let { DatabaseWallpaper.getDatabase(it) }
        val repository = databaseRef?.let { RepositoryWallpaper(it) }
        val listOfData = repository?.getOurOtherApplist

        // Recyclerview
        val recyclerview = binding.otherAppRecyclerview



        listOfData?.observe(viewLifecycleOwner, Observer {
            val adapter = context?.let { it1 -> OtherAppsAdapter(it, it1) }
            recyclerview.adapter = adapter
        })

//        enterTransition = MaterialElevationScale(true)
        reenterTransition = com.google.android.material.transition.platform.MaterialElevationScale(true)

        return binding.root
    }

    @SuppressLint("CommitPrefEdits")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(R.id.random).setVisible(false)
        menu.findItem(R.id.app_bar_search).setVisible(false)
        menu.findItem(R.id.changeColumn).setVisible(false)
    }

}