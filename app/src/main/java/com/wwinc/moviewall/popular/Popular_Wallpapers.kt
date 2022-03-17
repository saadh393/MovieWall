package com.wwinc.moviewall.popular

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.wwinc.moviewall.R
import com.wwinc.moviewall.adapters.GridView_Adapter
import com.wwinc.moviewall.database.DatabaseWallpaper
import com.wwinc.moviewall.databinding.PopularWallpapersFragmentBinding
import com.wwinc.moviewall.networking.Wallpaper_ModelItem
import com.wwinc.moviewall.repository.RepositoryWallpaper


class Popular_Wallpapers : Fragment() {

    private lateinit var viewModel: PopularWallpapersViewModel
    private lateinit var binding: PopularWallpapersFragmentBinding
    private lateinit var viewmodelFactory: PopularViewmodelFactory
    private lateinit var databaseRef : DatabaseWallpaper
    private lateinit var repositoryWallpaper: RepositoryWallpaper
    private lateinit var adapter : GridView_Adapter
    private lateinit var sharedPreferenceseditor : SharedPreferences
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var randomMenuBtn: MenuItem
    val TAG = "123as123"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.popular__wallpapers_fragment, container, false)
        setHasOptionsMenu(true);

        databaseRef = DatabaseWallpaper.getDatabase(inflater.context)
        viewmodelFactory = PopularViewmodelFactory(databaseRef)
        viewModel = ViewModelProvider(this, viewmodelFactory).get(PopularWallpapersViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        repositoryWallpaper = RepositoryWallpaper(databaseRef)
        sharedPreferenceseditor = requireContext().getSharedPreferences("COLUMN_NUMBER", Context.MODE_PRIVATE)
        firebaseAnalytics = Firebase.analytics


        repositoryWallpaper.popularWallpaperData.observe(viewLifecycleOwner, Observer {
            adapter = GridView_Adapter(requireContext(),
                it.shuffled() as MutableList<Wallpaper_ModelItem>
            )
            binding.recyclerView.adapter = adapter
        })

        val params = Bundle()
        params.putString("invalid_url", "Thsis something")
        firebaseAnalytics.logEvent("CLICK_EVENT", params)

        return binding.root

    }

    override fun onResume() {
        super.onResume()
        val sharedPreferences : SharedPreferences.Editor = context?.getSharedPreferences("POSITION_TRACKER", Context.MODE_PRIVATE)!!.edit()
        sharedPreferences.putString("POSTITION_2", null).apply()
        val getColumnSharedPref = requireContext().getSharedPreferences("POSITION_TRACKER", Context.MODE_PRIVATE)

        var columnNo = sharedPreferenceseditor.getInt("COLUMN", 0)
        if(columnNo ==0 ){columnNo =2}
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), columnNo)

    }


    @SuppressLint("CommitPrefEdits")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.manu_bar, menu);
        val item = menu.findItem(R.id.app_bar_search)
        val searchView = item.actionView as androidx.appcompat.widget.SearchView
        searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                Log.d("123as123", " newText\t\t  : $newText ");
                return true
            }

        })

        randomMenuBtn = menu.findItem(R.id.random)

        val f = childFragmentManager.findFragmentByTag("f0")
        if (f != null) {
            if( f.isVisible ){
                Log.d("123as123", "true " )
                randomMenuBtn.setVisible(true)
            }else{
                randomMenuBtn.setVisible(false)
                Log.d("123as123", "false " )

            }
        }
        randomMenuBtn.setOnMenuItemClickListener {
            binding.recyclerView.removeAllViews()
            repositoryWallpaper.popularWallpaperData.observe(viewLifecycleOwner, Observer {
                adapter = GridView_Adapter(requireContext(),
                    it.shuffled()
                )
                binding.recyclerView.adapter = adapter
            })
            Toast.makeText(context, "Shuffling...", Toast.LENGTH_SHORT).show()
            return@setOnMenuItemClickListener true
        }

        val columnItem = menu.findItem(R.id.changeColumn)
        columnItem.setOnMenuItemClickListener {
            val items = arrayOf("2", "3", "4")
            val icons = arrayOf(R.drawable.ic_baseline_grid_on_24, R.drawable.ic_fav_white, R.drawable.ic_share_white)

            val alert = AlertDialog.Builder(requireContext())
            val view = LayoutInflater.from(requireContext()).inflate(R.layout.column_dialog_item, null, false)
            alert.setView(view)
            val alertDialog = alert.show()
            //alertDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val editor = requireContext().getSharedPreferences("COLUMN_NUMBER", Context.MODE_PRIVATE).edit()
            val two = view.findViewById(R.id.One_R) as RelativeLayout
            val three = view.findViewById(R.id.Two_r) as RelativeLayout
            val four = view.findViewById(R.id.Three_r) as RelativeLayout

            two.setOnClickListener {
                editor.putInt("COLUMN", 2)
                editor.apply()
                editor.commit()
                binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
                alertDialog.dismiss()
            }

            three.setOnClickListener {
                editor.putInt("COLUMN", 3)
                editor.apply()
                editor.commit()
                binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
                alertDialog.dismiss()
            }

            four.setOnClickListener {
                editor.putInt("COLUMN", 4)
                editor.apply()
                editor.commit()
                binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 4)
                alertDialog.dismiss()
            }
            return@setOnMenuItemClickListener true
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

}
