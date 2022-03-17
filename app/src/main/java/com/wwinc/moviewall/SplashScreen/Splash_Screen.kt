package com.wwinc.moviewall.SplashScreen

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.wwinc.moviewall.MainActivity
import com.wwinc.moviewall.Model.EntityWallpaper
import com.wwinc.moviewall.R
import com.wwinc.moviewall.database.DatabaseWallpaper
import com.wwinc.moviewall.networking.WallpaperApi
import com.wwinc.moviewall.repository.RepositoryWallpaper
import kotlinx.coroutines.*


class Splash_Screen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_splash__screen)
        supportActionBar?.hide()

        // Coroutine
        val coroutineScope = CoroutineScope(Job() + Dispatchers.Main)
        val databaseRef = DatabaseWallpaper.getDatabase(application)
        val repository = RepositoryWallpaper(databaseRef)


//        coroutineScope.launch {
//            val imageUrl = WallpaperApi.retrofitService.getLinksAsync().await()
//        }



//        // Internet Connection
        if (!isNetworkConnected()) {
            showDialog()
        }else{

            coroutineScope.launch {
                withContext(Dispatchers.IO){
                    repository.refreshWallpapers()
                }
            }


            Handler().postDelayed({
                startActivity(Intent(this, MainActivity::class.java))
            }, 1000)
        }




    }

    private fun isNetworkConnected(): Boolean {
        val cm =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo != null && cm.activeNetworkInfo.isConnected
    }

    fun showDialog(){
        MaterialAlertDialogBuilder(this)
            .setTitle("Warrning")
            .setIcon(R.drawable.ic_baseline_warning_24)
            .setMessage("Please Check Your Internet connection and try again")
            .setPositiveButton("Retry", DialogInterface.OnClickListener { dialog, _ ->
                startActivity(Intent(this, Splash_Screen::class.java))
                dialog.dismiss()
            })
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
                finish()
            }
            .show()
    }

}