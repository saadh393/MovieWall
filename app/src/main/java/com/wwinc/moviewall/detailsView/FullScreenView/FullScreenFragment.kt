package com.wwinc.moviewall.detailsView.FullScreenView

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.app.ProgressDialog
import android.app.WallpaperManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import androidx.viewpager2.widget.ViewPager2
import com.wwinc.moviewall.BuildConfig
import com.wwinc.moviewall.R
import com.wwinc.moviewall.adapters.FullScreenViewpagerAdapter
import com.wwinc.moviewall.database.DatabaseWallpaper
import com.wwinc.moviewall.databinding.FullScreenFragmentBinding
import com.wwinc.moviewall.Model.VoteUp
import com.wwinc.moviewall.networking.WallpaperApi
import com.wwinc.moviewall.networking.Wallpaper_ModelItem
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*
import java.lang.Runnable
import java.net.HttpURLConnection
import java.net.URL

class FullScreenFragment : Fragment() {

    private lateinit var progressDialog: ProgressDialog
    lateinit var binding : FullScreenFragmentBinding
    private lateinit var onpageChangeCallback : ViewPager2.OnPageChangeCallback
    lateinit var listOfWallpapers : List<Wallpaper_ModelItem>
    private lateinit var scope : CoroutineScope
    private lateinit var databaseRef : DatabaseWallpaper
    private lateinit var avd : AnimatedVectorDrawableCompat
    private lateinit var avd2 : AnimatedVectorDrawable
    lateinit var bar : LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    @SuppressLint("NewApi")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding  = DataBindingUtil.inflate(inflater, R.layout.full_screen_fragment,container,false)
        bar = binding.barLinearlayout

        //Variable Initiate
        val args  = arguments?.let { FullScreenFragmentArgs.fromBundle(it) }
        val position = args!!.position
        val downloadManager = context?.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager


        scope = CoroutineScope(Job() + Dispatchers.Main)
        databaseRef = DatabaseWallpaper.getDatabase(inflater.context)
        progressDialog = ProgressDialog(context)
        listOfWallpapers = listOf()
        listOfWallpapers = args.wholelist.toList()

        val adapter = context?.let { FullScreenViewpagerAdapter(args.wholelist.toList(), it, bar, binding) }
        binding.fullScreenViewpager.adapter = adapter
        binding.fullScreenViewpager.currentItem = position

        // Update Favourite Status
        binding.favCheck.text = args.wholelist[position].favOrNot.toString()
        if(listOfWallpapers[position].favOrNot!!){
            binding.favBtn.setImageResource(R.drawable.ic_favorite_selected)
        }


        // Activating Shared Pref
        val sharedPreferences : SharedPreferences.Editor = context?.getSharedPreferences("POSITION_TRACKER", Context.MODE_PRIVATE)!!
            .edit()
        sharedPreferences.putString("POSTITION_2", position.toString()).apply()
        sharedPreferences.commit()


        // Setting as Favourite Image
        binding.favBtn.setOnClickListener {
            val currentItem = binding.fullScreenViewpager.currentItem
            if(listOfWallpapers[currentItem].favOrNot!!){    // Task to Remove from favourite
                favouriteTask(listOfWallpapers[currentItem].imageURL!!, true, binding)
                listOfWallpapers[currentItem].favOrNot = false

            }else{  // Task to Add to favourite
                favouriteTask(listOfWallpapers[currentItem].imageURL!!, false, binding)
                listOfWallpapers[currentItem].favOrNot = true
            }

        }


        // Set Wallpaper Button
        binding.setWallpaper.setOnClickListener {
            loading_dialog(1500, "Please wait for a moment")
            WallpaperSet(binding, scope, args);
        }


        // Share Wallpaper Button
        binding.shareBTN.setOnClickListener {
            loading_dialog(1000, "Please wait for a moment")
            share()
        }


        // Download Btn
        binding.downloadImage.setOnClickListener {
            val position = binding.fullScreenViewpager.currentItem
            val imageurl = args.wholelist[position].imageURL
            downloadProcess(imageurl!!, downloadManager)
        }

        // On Page Change Listener
        onpageChangeCallback = object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                val sharedPreferences : SharedPreferences.Editor = context?.getSharedPreferences("POSITION_TRACKER", Context.MODE_PRIVATE)!!
                    .edit()
                sharedPreferences.putString("POSTITION_2", position.toString()).apply()
                sharedPreferences.commit()

                val singleItems = listOfWallpapers.filter {
                    it.imageURL == args.wholelist[position].imageURL
                }

                if (singleItems[0].favOrNot!!){
                    binding.favBtn.setImageResource(R.drawable.ic_favorite_selected)
                }else{
                    binding.favBtn.setImageResource(R.drawable.ic_fav_white)
                }
            }
        }

        binding.fullScreenViewpager.registerOnPageChangeCallback(onpageChangeCallback)



        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun favouriteTask(imageLink: String, favourite : Boolean, binding: FullScreenFragmentBinding){
        // Voting Up
        val redDrawable : Drawable = binding.redHeart.drawable
        val whiteDrawable : Drawable = binding.whiteHeart.drawable

        context?.packageName?.let { onlineVoteup(it, imageLink) }
        scope.launch {
            if (!favourite) {
                withContext(Dispatchers.IO) {databaseRef.getDaoPopular.update_fav(imageLink, true)}
                binding.favBtn.setImageResource(R.drawable.ic_favorite_selected)
                binding.redHeart.alpha = .80f
                if(redDrawable is AnimatedVectorDrawableCompat){
                    avd = redDrawable as AnimatedVectorDrawableCompat
                    avd.start()

                }else if(redDrawable is AnimatedVectorDrawable){
                    avd2 = redDrawable as AnimatedVectorDrawable
                    avd2.start()
                }

            }else{
                withContext(Dispatchers.IO) {databaseRef.getDaoPopular.update_fav(imageLink, false)}
                binding.favBtn.setImageResource(R.drawable.ic_fav_white)
                binding.whiteHeart.alpha = .80f
                if(whiteDrawable is AnimatedVectorDrawableCompat){
                    avd = whiteDrawable as AnimatedVectorDrawableCompat
                    avd.start()

                }else if(whiteDrawable is AnimatedVectorDrawable){
                    avd2 = whiteDrawable as AnimatedVectorDrawable
                    avd2.start()
                }
            }
        }

    }


    private fun onlineVoteup(packageName : String, imageURl : String){
        val x= WallpaperApi.retrofitService.voteImage(packageName, imageURl )
        x.enqueue(object : Callback<VoteUp> {
            override fun onFailure(call: Call<VoteUp>, t: Throwable) {
                Log.d("123as123", "onFailure: ")
            }
            override fun onResponse(call: Call<VoteUp>, response: Response<VoteUp>) {
                Log.d("123as123", "onResponse: ")
            }
        })
    }

    private fun WallpaperSet(binding: FullScreenFragmentBinding,scope: CoroutineScope,args: FullScreenFragmentArgs) {
        val position = binding.fullScreenViewpager.currentItem
        scope.launch {
            withContext(Dispatchers.IO) {
//                val bitmap = getBitmapFromURL(args.wholelist[position].imageURL)
                val wallpaperM = WallpaperManager.getInstance(context)
                wallpaperM.setBitmap(wallpaperSetScreenshort(binding.fullScreenViewpager))      // Loading From Cache
//                wallpaperM.setBitmap(bitmap)        // Downloading From Internet
            }
        }
        Toast.makeText(context, "Wallpaper Set ❤ 🔥", Toast.LENGTH_SHORT).show()
    }

    private fun getBitmapFromURL(src: String?): Bitmap? {
        return try {
            val url = URL(src)
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            connection.setDoInput(true)
            connection.connect()
            val input: InputStream = connection.getInputStream()
            BitmapFactory.decodeStream(input)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    private fun downloadProcess(imageUrl : String, downloadManager : DownloadManager){
        Toast.makeText(context, "Starting Download 🔥", Toast.LENGTH_SHORT).show();
        val filename_list = imageUrl.toString().split("/")
        val filename = filename_list.get(filename_list.size-1).replace(")","")

        val uri = Uri.parse(imageUrl)
        val request : DownloadManager.Request = DownloadManager.Request(uri)
        request.setNotificationVisibility(
            DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED).setDestinationInExternalPublicDir(Environment.DIRECTORY_DCIM , context?.resources!!.getString(R.string.app_name) + "/$filename" + ".png")
        downloadManager.enqueue(request)
    }

    @SuppressLint("SetWorldReadable")
    fun share() {
        binding.fullScreenViewpager.setDrawingCacheEnabled(true)
        val cardBitmap: Bitmap = Bitmap.createBitmap(binding.fullScreenViewpager.getDrawingCache())
        binding.fullScreenViewpager.setDrawingCacheEnabled(false)
        try {
            val outputFile =
                File(context?.getCacheDir(), "image_share.png")
            val fileOutputStream = FileOutputStream(outputFile)
            cardBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
            fileOutputStream.flush()
            fileOutputStream.close()
            outputFile.setReadable(true, false)
            val bitmapUri = context?.let { FileProvider.getUriForFile(it,BuildConfig.APPLICATION_ID.toString() + ".provider",outputFile) }


            //Sharing
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.putExtra(Intent.EXTRA_STREAM, bitmapUri)
            shareIntent.putExtra("android.intent.extra.STREAM", bitmapUri)
            shareIntent.type = "image/jepg"
            startActivity(Intent.createChooser(shareIntent, "Share Via"))

        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun loading_dialog(time: Int, x: String) {
        progressDialog.setTitle("Please Wait")
        progressDialog.setMessage(x)
        progressDialog.show()
        Handler().postDelayed(Runnable {
            progressDialog.dismiss()

        }, time.toLong())
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.fullScreenViewpager.unregisterOnPageChangeCallback(onpageChangeCallback)

    }

    fun wallpaperSetScreenshort(image : ViewPager2) : Bitmap {
        image.setDrawingCacheEnabled(true)
        val cardBitmap: Bitmap = Bitmap.createBitmap(image.getDrawingCache())
        image.setDrawingCacheEnabled(false)
        try {
            val outputFile =
                File(context?.getCacheDir(), "image_cache.png")
            val fileOutputStream = FileOutputStream(outputFile)
            cardBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
            fileOutputStream.flush()
            fileOutputStream.close()
            outputFile.setReadable(true, false)
            val bitmapUri = context?.let { FileProvider.getUriForFile(it,
                BuildConfig.APPLICATION_ID.toString() + ".provider",outputFile) }



        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return cardBitmap
    }
}