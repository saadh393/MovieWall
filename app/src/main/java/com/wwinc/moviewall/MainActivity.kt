package com.wwinc.moviewall

import android.Manifest
import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdViewAdListener
import com.applovin.mediation.MaxError
import com.google.firebase.analytics.FirebaseAnalytics
import com.wwinc.moviewall.databinding.ActivityMainBinding
import java.io.File


class MainActivity : AppCompatActivity(), MaxAdViewAdListener {

    lateinit var binding: ActivityMainBinding
    lateinit var drawerLayout: DrawerLayout
    var permission = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
    lateinit var sharedPrefReviewdorNot : SharedPreferences
    var userEntryCounter : Int = 0
    private var mFirebaseAnalytics: FirebaseAnalytics? = null




    @SuppressLint("CommitPrefEdits")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        supportActionBar?.elevation = 0f


        val sharedPreferencesEditor = getSharedPreferences("RATING", Context.MODE_PRIVATE)!!.edit()
        val sharedPrefTrackingUserActivity = getSharedPreferences("USERACTIVITY", Context.MODE_PRIVATE)
        val editorSharedPrefTrackingUserActivity = getSharedPreferences("USERACTIVITY", Context.MODE_PRIVATE).edit()
        val sharedPreferences : SharedPreferences.Editor = this.getSharedPreferences("POSITION_TRACKER", Context.MODE_PRIVATE).edit()
        sharedPreferences.putString("POSTITION_2", null).apply()

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);





        //Navigation
        drawerLayout = binding.drawerLayout
        val navController = this.findNavController(R.id.containerFragment)
        NavigationUI.setupActionBarWithNavController(this, navController,drawerLayout)
        NavigationUI.setupWithNavController(binding.navView, navController)


        if (!checkPermissionGranted(permission)){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(permission, 123)
            }
        }


        // Rate Us Method
        userEntryCounter = sharedPrefTrackingUserActivity.getInt("ACTIVITY", 0)
        sharedPrefReviewdorNot = getSharedPreferences("RATING", Context.MODE_PRIVATE)

        if(!sharedPrefReviewdorNot.getBoolean("checkRating", false)){
            // Detect User Activity
            editorSharedPrefTrackingUserActivity.putInt("ACTIVITY", userEntryCounter + 1)
            editorSharedPrefTrackingUserActivity.apply()

            if( userEntryCounter == 5 || userEntryCounter == 10 || userEntryCounter == 15 || userEntryCounter == 20){


                Handler().postDelayed({
                    showRatingDialog(sharedPreferencesEditor)
                }, 10000)
            }

        }

    }


    fun showRatingDialog(sharedPreferencesEditor : SharedPreferences.Editor ) {
        val alertDialog = AlertDialog.Builder(this)
        val dialogView = LayoutInflater.from(this).inflate(R.layout.fragment_rate_us, null, false)
        alertDialog.setView(dialogView)

        val dialog = alertDialog.show()

        var selectedRating = 0
        val ratingbar = dialogView.findViewById(R.id.ratingBar) as RatingBar
        val submit_rating = dialogView.findViewById(R.id.submit_rating) as Button
        val cancelDialog = dialogView.findViewById(R.id.cancelDialog) as TextView

        ratingbar.setOnRatingBarChangeListener{ratingBar, rating, _ ->
            submit_rating.isEnabled = rating > 0f

            if(rating == 5.0F){
                selectedRating = 5
            }
        }

        submit_rating.setOnClickListener { v: View? ->
            sharedPreferencesEditor.putBoolean("checkRating", true).apply()
            if(selectedRating == 5){
                try {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("market://details?id=" + util.PACKAGENAME + "&showAllReviews=true"))
                    )
                    Toast.makeText(
                        this,
                        "Your Review on Playstore helps us to improve our apps",
                        Toast.LENGTH_LONG
                    ).show()
                } catch (e: ActivityNotFoundException) {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=" + util.PACKAGENAME + "&showAllReviews=true")
                        )
                    )
                }
                dialog.dismiss()
            }else{
                dialog.dismiss()
                if(selectedRating < 4){
                    showThankYouDialog("Thanks for sharing your feedback. We’re sorry your experience didn’t match your expectations. It was an uncommon instance and we’ll do better in the future.")
                }
            }
        }


        cancelDialog.setOnClickListener{v ->
            dialog.dismiss()
        }

    }

    fun showThankYouDialog(msg :String){
        val alert  = AlertDialog.Builder(this)
        alert.setTitle("Thank You")
        alert.setMessage(msg)


        alert.setPositiveButton("Cancel"){dialog, _ ->
            dialog.dismiss()
        }

        alert.show()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.containerFragment)
        return NavigationUI.navigateUp(navController,drawerLayout)

    }

    fun checkPermissionGranted(permission : Array<String>) : Boolean{
        var allsuccess = true
        for (i in permission.indices){
            if(checkCallingOrSelfPermission(permission[i]) == PackageManager.PERMISSION_DENIED){
                allsuccess = false
            }
        }
        return allsuccess
    }

    override fun onRequestPermissionsResult(requestCode: Int,permissions: Array<out String>,grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 123){
            var allsuccess = true
            for (i in permissions.indices){
                if (grantResults[i] == PackageManager.PERMISSION_DENIED){
                    allsuccess = false
                    var requestAgain = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && shouldShowRequestPermissionRationale(permissions[i])
                    if (requestAgain){
                        Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(this, " Go to Permission and Try Again ", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    val folder = File(Environment.getExternalStorageDirectory(),"DCIM" + File.separator + resources.getString(R.string.app_name))
                    if (!folder.exists()){
                        folder.mkdirs()
                    }
                }
            }
        }

    }



    override fun onAdLoaded(ad: MaxAd?) {
        TODO("Not yet implemented")
        Log.d("123as123", "onAdLoaded: ")
    }

    override fun onAdDisplayed(ad: MaxAd?) {
        TODO("Not yet implemented")
    }

    override fun onAdHidden(ad: MaxAd?) {
        TODO("Not yet implemented")
    }

    override fun onAdClicked(ad: MaxAd?) {
        TODO("Not yet implemented")
    }

    override fun onAdLoadFailed(adUnitId: String?, error: MaxError?) {
        TODO("Not yet implemented")
        Log.d("123as123", "onAdLoadFailed: ")
    }

    override fun onAdDisplayFailed(ad: MaxAd?, error: MaxError?) {
        TODO("Not yet implemented")
    }

    override fun onAdExpanded(ad: MaxAd?) {
        TODO("Not yet implemented")
    }

    override fun onAdCollapsed(ad: MaxAd?) {
        TODO("Not yet implemented")
    }

    override fun onDestroy() {
        super.onDestroy()
    }

}


