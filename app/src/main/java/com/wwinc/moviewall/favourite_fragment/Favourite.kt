package com.wwinc.moviewall.favourite_fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.wwinc.moviewall.R
import com.wwinc.moviewall.adapters.GridView_Adapter
import com.wwinc.moviewall.adapters.HorizentalViewpager_Adapter
import com.wwinc.moviewall.database.DatabaseWallpaper
import com.wwinc.moviewall.databinding.FragmentFavouriteBinding
import com.wwinc.moviewall.networking.Wallpaper_ModelItem
import com.wwinc.moviewall.popular.PopularViewmodelFactory
import com.wwinc.moviewall.popular.PopularWallpapersViewModel


class Favourite : Fragment() {

    lateinit var binding : FragmentFavouriteBinding
    lateinit var databaseRef : DatabaseWallpaper
    lateinit var viewmodelFactory : Viewmodel_FavFactory
    lateinit var viewModel : Viewmodel_Fav

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true);
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_favourite, container, false)

        databaseRef = DatabaseWallpaper.getDatabase(inflater.context)
        viewmodelFactory = Viewmodel_FavFactory(databaseRef)
        viewModel = ViewModelProvider(this, viewmodelFactory).get(Viewmodel_Fav::class.java)


        binding.setLifecycleOwner(this)
        binding.viewmodel = viewModel



        //Passing the Adapter
        viewModel.wallpaper_url.observe(viewLifecycleOwner, Observer {
            var adapter = context?.let { it1 -> GridView_Adapter(it1,
                it
            ) }
            binding.recyclerView.adapter = adapter
            if (it.size == 0) {
                binding.recyclerView.visibility = View.GONE
                binding.imageView2.visibility = View.VISIBLE
            }

        })




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