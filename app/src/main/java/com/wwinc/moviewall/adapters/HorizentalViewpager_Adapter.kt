package com.wwinc.moviewall.adapters

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.app.ProgressDialog
import android.app.WallpaperManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore.Images
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import androidx.navigation.findNavController
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.wwinc.moviewall.BuildConfig
import com.wwinc.moviewall.R
import com.wwinc.moviewall.database.DatabaseWallpaper
import com.wwinc.moviewall.databinding.ViewPagerFragmentBinding
import com.wwinc.moviewall.detailsView.ViewPager.ViewPagerFragmentDirections
import com.wwinc.moviewall.Model.VoteUp
import com.wwinc.moviewall.networking.WallpaperApi
import com.wwinc.moviewall.networking.Wallpaper_ModelItem
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*
import java.net.HttpURLConnection
import java.net.URL


class HorizentalViewpager_Adapter(var image_list : List<Wallpaper_ModelItem>, val context : Context?, binding : ViewPagerFragmentBinding) : PagerAdapter(){

    val scope = CoroutineScope(Job() + Dispatchers.Main)
    val databaseRef = context?.let { DatabaseWallpaper.getDatabase(it).getDaoPopular }
    private val progressDialog = ProgressDialog(context)
    // Manage Animations
    lateinit var avd : AnimatedVectorDrawableCompat
    lateinit var avd2 : AnimatedVectorDrawable

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return  view == obj
    }

    override fun getCount(): Int = image_list.size

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        var view  = LayoutInflater.from(container.context).inflate(R.layout.details_viewpager_item, container, false)
        val fullImage: ImageView = view.findViewById(R.id.image_viewPager_item)
        val downloadImage : ImageView = view.findViewById(R.id.download_image)
        val setWallpaper  = view.findViewById(R.id.setWallpaper) as ImageView
        val favBtn = view.findViewById(R.id.fav_btn) as ImageView
        val downloadManager = context?.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val favStatus = view.findViewById(R.id.fav_check) as TextView
        val animatedHeart = view.findViewById(R.id.heartAnim) as ImageView
        val whiteHeart = view.findViewById(R.id.heartWhite) as ImageView
        val shareBtn = view.findViewById(R.id.shareBTN_) as ImageView
        val drawable : Drawable = animatedHeart.drawable
        val whiteDrawable : Drawable = whiteHeart.drawable

        //var requestOptions: RequestOptions =RequestOptions().transform(CenterCrop(), RoundedCorners(18))
        // Progress Dialog
        progressDialog.setTitle("Please Wait")
        progressDialog.setMessage("Downloading Image...")
        // Setting Event for User CLick on Wallpaper It will go to FullScreenViwe fragment

        var event = 0;
        fullImage.setOnClickListener { v: View ->
            event++
            Handler().postDelayed({
                if(event == 1){
//                        Single Tap



                    v.findNavController()
                        .navigate(
                            ViewPagerFragmentDirections.actionViewPagerFragment2ToFullScreenFragment2(position, image_list.toTypedArray())                        )

                }
                event = 0
            }, 300)

            if(event == 2){
                favouriteTask(animatedHeart, drawable, position, favStatus, favBtn, whiteHeart, whiteDrawable)
                event = 0
            }

        }

        // Glide is used to Display The Images in the Viewpager
        Glide.with(fullImage.context)
            .load(image_list.get(position).imageURL)
            .diskCacheStrategy(DiskCacheStrategy.DATA)
            .thumbnail(Glide.with(fullImage.context).load(R.drawable.placeholder).centerCrop())
            .into(fullImage)



        // Save Button Click Listener => It will set this image as wallpaper
        setWallpaper.setOnClickListener(){
            // Voting Up
            voteUp(context.packageName, image_list.get(position).imageURL!!)
            progressDialog.show()
            scope.launch {
                withContext(Dispatchers.IO){
//                    val bitmap  = getBitmapFromURL(image_list.get(position).imageURL)
                    // Seting Wallpaper From Screenshort
                    val wallpaperM = WallpaperManager.getInstance(container.context)
                    wallpaperM.setBitmap(wallpaperSetScreenshort(fullImage))

                    // Downloading  and setting wallpaper
                    val bitmap  = getBitmapFromURL(image_list.get(position).imageURL)
                    wallpaperM.setBitmap(bitmap)
                }
            }
            Toast.makeText(container.context, "Wallpaper Set ❤", Toast.LENGTH_SHORT).show();
        }

        // Download Image
        downloadImage.setOnClickListener{
            // Voting Up
            voteUp(context.packageName, image_list.get(position).imageURL!!)

            // Download Process
            Toast.makeText(container.context, "Starting Download 🔥", Toast.LENGTH_SHORT).show();
            val filename_list = image_list.get(position).toString().split("/")
            var filename = filename_list.get(filename_list.size-1).replace(")","")
            filename = filename.split(",")[0].toString()
            Log.d("123as123", "File Name \t\t  :  " + filename);

            val uri = Uri.parse(image_list.get(position).imageURL)
            val request : DownloadManager.Request = DownloadManager.Request(uri)
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DCIM ,context.resources.getString(R.string.app_name) + "/$filename" + ".png")
            downloadManager.enqueue(request)
        }

        // fav status setting
        if (image_list.get(position).favOrNot!!){
            favStatus.text= "TRUE"
            favBtn.setImageResource(R.drawable.ic_favorite_selected)
        }else{
            favStatus.text= "FALSE"
        }



        // Button Action For Fav and UnFav
        favBtn.setOnClickListener {
            favouriteTask(animatedHeart, drawable, position, favStatus, favBtn, whiteHeart, whiteDrawable)
        }


        shareBtn.setOnClickListener {
            share(fullImage)
        }

        container.addView(view)
        return view
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun favouriteTask(animatedHeart : ImageView, drawable : Drawable, position : Int, fav_status : TextView, fav_btn : ImageView, whiteHeart : ImageView, whiteDrawable : Drawable ){
        // Voting Up
        context?.packageName?.let { voteUp(it, image_list.get(position).imageURL!!) }
        scope.launch {
            if (fav_status.text.toString().contains("FALSE")) {
                withContext(Dispatchers.IO) {
                    databaseRef?.update_fav(image_list.get(position).imageURL!!, true)
                }
                fav_btn.setImageResource(R.drawable.ic_favorite_selected)
                fav_status.text= "TRUE"
                image_list.get(position).favOrNot = true

                // TEST
                animatedHeart.alpha = .80f
                if(drawable is AnimatedVectorDrawableCompat){
                    avd = drawable as AnimatedVectorDrawableCompat
                    avd.start()

                }else if(drawable is AnimatedVectorDrawable){
                    avd2 = drawable as AnimatedVectorDrawable
                    avd2.start()
                }

            }else{
                withContext(Dispatchers.IO) {
                    databaseRef?.update_fav(image_list.get(position).imageURL!!, false)
                }
                fav_btn.setImageResource(R.drawable.ic_favorite)
                fav_status.text= "FALSE"

                // Test
                whiteHeart.alpha = .80f
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

    suspend fun getBitmapFromURL(src: String?): Bitmap? {
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

    fun getImageUri(inContext: Context, inImage: Bitmap?): Uri {
        val bytes = ByteArrayOutputStream()
        if (inImage != null) {
            inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        }
        val path = Images.Media.insertImage(inContext.getContentResolver(),inImage, "Title", null)
        return Uri.parse(path)
    }

    fun voteUp(packageName : String, imageURl : String){
        val x= WallpaperApi.retrofitService.voteImage(packageName, imageURl )
        x.enqueue(object : Callback<VoteUp> {
            override fun onFailure(call: Call<VoteUp>, t: Throwable) {
                Log.d("123as123", "onFailure: ")
                progressDialog.dismiss()
            }
            override fun onResponse(call: Call<VoteUp>, response: Response<VoteUp>) {
                Log.d("123as123", "onResponse: ")
                progressDialog.dismiss()
            }
        })
    }


    @SuppressLint("SetWorldReadable")
    fun share(image : ImageView) {
        image.setDrawingCacheEnabled(true)
        val cardBitmap: Bitmap = Bitmap.createBitmap(image.getDrawingCache())
        image.setDrawingCacheEnabled(false)
        try {
            val outputFile =
                File(context?.getCacheDir(), "image_share.png")
            val fileOutputStream = FileOutputStream(outputFile)
            cardBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
            fileOutputStream.flush()
            fileOutputStream.close()
            outputFile.setReadable(true, false)
            val bitmapUri = context?.let { FileProvider.getUriForFile(it,
                BuildConfig.APPLICATION_ID.toString() + ".provider",outputFile) }


            //Sharing
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.putExtra(Intent.EXTRA_STREAM, bitmapUri)
            shareIntent.putExtra("android.intent.extra.STREAM", bitmapUri)
            shareIntent.putExtra(Intent.EXTRA_TEXT, "For More Photo Visit :  https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID)
            shareIntent.type = "*/*"
            context?.startActivity(Intent.createChooser(shareIntent, "Share Via"))

        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


    @SuppressLint("SetWorldReadable")
    fun wallpaperSetScreenshort(image : ImageView) : Bitmap {
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