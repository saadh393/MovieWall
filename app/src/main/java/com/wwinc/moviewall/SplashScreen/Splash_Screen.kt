package com.wwinc.moviewall.SplashScreen

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
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


//        // Internet Connection
        if (!isNetworkConnected(applicationContext)) {
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

    private fun isNetworkConnected(context: Context): Boolean {
        var result = false
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val networkCapabilities = connectivityManager.activeNetwork ?: return false
            val actNw =
                connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
            result = when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.run {
                connectivityManager.activeNetworkInfo?.run {
                    result = when (type) {
                        ConnectivityManager.TYPE_WIFI -> true
                        ConnectivityManager.TYPE_MOBILE -> true
                        ConnectivityManager.TYPE_ETHERNET -> true
                        else -> false
                    }

                }
            }
        }

        return result
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