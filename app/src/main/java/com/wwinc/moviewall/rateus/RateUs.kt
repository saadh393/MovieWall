package com.wwinc.moviewall.rateus

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.view.children
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.database.FirebaseDatabase
import com.wwinc.moviewall.BuildConfig
import com.wwinc.moviewall.R
import com.wwinc.moviewall.adapters.GridView_Adapter
import com.wwinc.moviewall.databinding.FragmentRateUsBinding


class RateUs : Fragment() {

    lateinit var binding : FragmentRateUsBinding
    lateinit var sharedPreferencesEditor : SharedPreferences.Editor


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true);
    }

    @SuppressLint("CommitPrefEdits")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_rate_us, container, false)
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference(context?.resources!!.getString(R.string.app_name))
        sharedPreferencesEditor = context?.getSharedPreferences("RATING", Context.MODE_PRIVATE)!!.edit()

        binding.submitRating.setOnClickListener {
            val rate = binding.ratingBar.rating
            sharedPreferencesEditor.putBoolean("checkRating", true).apply()
            if (rate == 5.0f){
                try {
                    context?.startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("market://details?id=" + BuildConfig.APPLICATION_ID.toString() + "&showAllReviews=true")
                        )
                    )
                    Toast.makeText(
                        context,
                        "Your Review on Playstore helps us to improve our apps",
                        Toast.LENGTH_LONG
                    ).show()
                } catch (e: ActivityNotFoundException) {
                    context?.startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID.toString() + "&showAllReviews=true")
                        )
                    )
                }
            }else{

                val data : HashMap<String, Any> = HashMap()
                data.put("Reason", binding.writeReview.text.toString())
                data.put("Star", rate.toString())
                data["Model"] = Build.MODEL
                data["Brand"] = Build.BRAND
                data["Version Code"] = BuildConfig.VERSION_CODE
                data["Version Name"] = BuildConfig.VERSION_NAME
                myRef.child(myRef.push().key.toString()).setValue(data);
                Toast.makeText(context, "Thanks for your valuable feedback ❤", Toast.LENGTH_SHORT).show();
                binding.writeReview.visibility = View.GONE
                binding.ratingBar.visibility = View.GONE
                binding.submitRating.visibility = View.GONE
                binding.first.visibility = View.GONE
                binding.imageView4.visibility = View.GONE
                binding.thanks.visibility = View.VISIBLE


            }
        }

        binding.ratingBar.setOnRatingBarChangeListener { ratingBar, rating, _ ->
            if (rating != 5f){
                binding.writeReview.visibility = View.VISIBLE
            }else if(rating == 5f){
                binding.writeReview.visibility = View.GONE
            }

            binding.submitRating.isEnabled = rating > 0f
        }

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