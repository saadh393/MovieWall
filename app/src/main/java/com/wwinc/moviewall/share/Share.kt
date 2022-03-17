package com.wwinc.moviewall.share

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.wwinc.moviewall.BuildConfig
import com.wwinc.moviewall.MainActivity
import com.wwinc.moviewall.R
import com.wwinc.moviewall.databinding.FragmentShareBinding

class Share : Fragment() {

    lateinit var binding : FragmentShareBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

   override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?    ): View? {
        binding  = DataBindingUtil.inflate(inflater, R.layout.fragment_share, container, false)
//       setHasOptionsMenu(false);
        binding.shareBtn.setOnClickListener {
            try {
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
                var shareMessage = "Let me recommend you this application\n\nhttps://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
                startActivity(Intent.createChooser(shareIntent, "Choose One ❤"))
            } catch (e: Exception) {
                //e.toString();
            }
        }

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.manu_bar, menu);
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(R.id.random).setVisible(false)
        menu.findItem(R.id.app_bar_search).setVisible(false)
        menu.findItem(R.id.changeColumn).setVisible(false)

        Log.d("123as123", "Share " + menu.findItem(R.id.random).isVisible)
    }

}