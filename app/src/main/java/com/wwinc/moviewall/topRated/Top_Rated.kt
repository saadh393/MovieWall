package com.wwinc.moviewall.topRated

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.wwinc.moviewall.R
import com.wwinc.moviewall.databinding.TopRatedFragmentBinding
import com.wwinc.moviewall.adapters.GridView_Adapter
import com.wwinc.moviewall.database.DatabaseWallpaper
import com.wwinc.moviewall.networking.Wallpaper_ModelItem
import com.wwinc.moviewall.repository.RepositoryWallpaper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job


class Top_Rated : Fragment() {

    private lateinit var binding : TopRatedFragmentBinding
    private lateinit var topRatedViewModel: TopRatedViewModel
    private lateinit var databaseRef : DatabaseWallpaper
    private lateinit var viewmodelFactory: TopRatedViewmodelFactory
    private lateinit var adapter : GridView_Adapter
    private lateinit var sharedPreferenceseditor : SharedPreferences

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater,R.layout.top__rated_fragment, container, false)
        binding.lifecycleOwner = this
        setHasOptionsMenu(true);
        databaseRef = DatabaseWallpaper.getDatabase(inflater.context)
        viewmodelFactory = TopRatedViewmodelFactory(databaseRef)
        topRatedViewModel = ViewModelProvider(this, viewmodelFactory).get(TopRatedViewModel::class.java)
        binding.topRatedView = topRatedViewModel
        sharedPreferenceseditor = requireContext().getSharedPreferences("COLUMN_NUMBER", Context.MODE_PRIVATE)


        topRatedViewModel.topRatedWallpaper.observe(viewLifecycleOwner, Observer {
            adapter = GridView_Adapter(requireContext(), it as MutableList<Wallpaper_ModelItem>)
            binding.topRatedRecyclerview.adapter = adapter
        })



        return binding.root
    }

    override fun onResume() {
        super.onResume()
        val sharedPreferences : SharedPreferences.Editor = context?.getSharedPreferences("POSITION_TRACKER", Context.MODE_PRIVATE)!!.edit()
        sharedPreferences.putString("POSTITION_2", null).apply()
        sharedPreferences.commit()

        // Column Count
        var columnNo = sharedPreferenceseditor.getInt("COLUMN", 0)
        if(columnNo ==0 ){columnNo =2}
        binding.topRatedRecyclerview.layoutManager = GridLayoutManager(requireContext(), columnNo)


    }


    @SuppressLint("CommitPrefEdits")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.manu_bar, menu);
        val item = menu.findItem(R.id.app_bar_search)
        val searchView = item.actionView as androidx.appcompat.widget.SearchView
        searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                topRatedViewModel.topRatedWallpaper.observe(viewLifecycleOwner, Observer {
                    adapter = GridView_Adapter(requireContext(),
                        it
                    )
                    binding.topRatedRecyclerview.adapter = adapter
                    adapter.filter.filter(newText)
                })

                return false
            }

        })

        val columnItem = menu.findItem(R.id.changeColumn)
        columnItem.setOnMenuItemClickListener {
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
                binding.topRatedRecyclerview.layoutManager = GridLayoutManager(requireContext(), 2)
                alertDialog.dismiss()
            }

            three.setOnClickListener {
                editor.putInt("COLUMN", 3)
                editor.apply()
                editor.commit()
                binding.topRatedRecyclerview.layoutManager = GridLayoutManager(requireContext(), 3)
                alertDialog.dismiss()
            }

            four.setOnClickListener {
                editor.putInt("COLUMN", 4)
                editor.apply()
                editor.commit()
                binding.topRatedRecyclerview.layoutManager = GridLayoutManager(requireContext(), 4)
                alertDialog.dismiss()
            }

            return@setOnMenuItemClickListener true
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        var random = menu.findItem(R.id.random)
        random.setVisible(false)


        super.onPrepareOptionsMenu(menu)
    }

}
