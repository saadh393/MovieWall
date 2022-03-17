package com.wwinc.moviewall.adapters

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.wwinc.moviewall.Model.OtherAppListModel
import com.wwinc.moviewall.R
import com.wwinc.moviewall.ourotherapps.OurOtherAppsDirections


class OtherAppsAdapter (val listData : List<OtherAppListModel>, val context : Context) : RecyclerView.Adapter<OtherAppsAdapter.OtherAppsViewholder>() {




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OtherAppsViewholder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.otherapps_item, parent, false)
        return OtherAppsViewholder(view)
    }

    override fun getItemCount(): Int = listData.size

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(holder: OtherAppsViewholder, position: Int) {

        Glide.with(context).load(listData[position].logoUrl).thumbnail(Glide.with(context).load(R.drawable.placeholder)).into(holder.image)

        holder.appName.text = listData[position].appname

       Glide.with(context).load(listData[position].bannerPhoto).into(holder.coverPhoto)

        holder.image.transitionName = "Image"+position
        holder.itemView.setOnClickListener {
            val extras = FragmentNavigatorExtras(
                holder.image to "Image"+position
            )
            val action = OurOtherAppsDirections.actionOurOtherAppsToOtherAppsDetailsView(
                listData[position].logoUrl, "Image"+position, listData[position].bannerPhoto, listData[position].appname, listData[position].description, listData[position].packageName )
            it.findNavController().navigate(action, extras)
        }

    }


    class OtherAppsViewholder(val view : View) : RecyclerView.ViewHolder(view){
        val image = view.findViewById(R.id.itemImageView) as ImageView
        val appName = view.findViewById(R.id.appName) as TextView
        val coverPhoto = view.findViewById(R.id.coverPhoto) as ImageView
    }

}



