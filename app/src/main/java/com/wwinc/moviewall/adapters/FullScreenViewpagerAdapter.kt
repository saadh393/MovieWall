package com.wwinc.moviewall.adapters

import android.content.Context
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.bumptech.glide.Glide
import com.wwinc.moviewall.R
import com.wwinc.moviewall.database.DatabaseWallpaper
import com.wwinc.moviewall.databinding.FullScreenFragmentBinding
import com.wwinc.moviewall.networking.Wallpaper_ModelItem
import kotlinx.coroutines.*

class FullScreenViewpagerAdapter (val imagesList :  List<Wallpaper_ModelItem>, val context : Context, val bar : LinearLayout, val binding : FullScreenFragmentBinding) : RecyclerView.Adapter<FullScreenViewpagerAdapter.FullScreenViewHolder>(){

    private lateinit var avd : AnimatedVectorDrawableCompat
    private lateinit var avd2 : AnimatedVectorDrawable
    val databaseRef = DatabaseWallpaper.getDatabase(context)

    class FullScreenViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val fullScreenImage : ImageView  = itemView.findViewById(R.id.fullScreenImageViewItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FullScreenViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.full_screen_imageview_item, parent, false)
        return FullScreenViewHolder(view)
    }

    override fun getItemCount(): Int = imagesList.size

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(holder: FullScreenViewHolder, position: Int) {

        var counter  = 0
        holder.itemView.setOnClickListener {
            counter++

//            Hide Animation
            android.os.Handler().postDelayed({
                if(counter == 1){
                    if(bar.alpha == 0.0f){
                        bar.animate().translationY(0f).setDuration(150).start()
                        bar.animate().alpha(1f).setDuration(150).start()
                    }else{
                        bar.animate().translationY(bar.height.toFloat()).setDuration(150).start()
                        bar.animate().alpha(0f).setDuration(150).start()
                    }
                }
                counter = 0
            }, 300)

            // Heart React
            if(counter == 2){
                if (imagesList[position].favOrNot!!){
                    favouriteTask(imagesList[position].imageURL!!, true, binding)
                    imagesList[position].favOrNot = false
                    binding.favBtn.setImageResource(R.drawable.ic_fav_white)
                }else{
                    favouriteTask(imagesList[position].imageURL!!, false, binding)
                    imagesList[position].favOrNot = true
                    binding.favBtn.setImageResource(R.drawable.ic_favorite_selected)
                }
                counter=0
                
            }
        }

        Glide.with(context)
            .load(imagesList[position].imageURL)
            .placeholder(R.drawable.placeholder)
            .into(holder.fullScreenImage)
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun favouriteTask(imageLink: String, favourite : Boolean, binding: FullScreenFragmentBinding){
        // Voting Up
        val redDrawable : Drawable = binding.redHeart.drawable
        val whiteDrawable : Drawable = binding.whiteHeart.drawable
        val scope = CoroutineScope(Job() + Dispatchers.Main)

        scope.launch {
            if (!favourite) {
                withContext(Dispatchers.IO) {databaseRef.getDaoPopular.update_fav(imageLink, true)}
                binding.favBtn.setImageResource(R.drawable.ic_favorite_selected)
                binding.redHeart.alpha = .80f
                if(redDrawable is AnimatedVectorDrawableCompat){
                    avd = redDrawable
                    avd.start()

                }else if(redDrawable is AnimatedVectorDrawable){
                    avd2 = redDrawable
                    avd2.start()
                }

            }else{
                withContext(Dispatchers.IO) {databaseRef.getDaoPopular.update_fav(imageLink, false)}
                binding.favBtn.setImageResource(R.drawable.ic_fav_white)
                binding.whiteHeart.alpha = .80f
                if(whiteDrawable is AnimatedVectorDrawableCompat){
                    avd = whiteDrawable
                    avd.start()

                }else if(whiteDrawable is AnimatedVectorDrawable){
                    avd2 = whiteDrawable
                    avd2.start()
                }
            }
        }

    }
}