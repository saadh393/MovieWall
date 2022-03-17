package com.wwinc.moviewall.adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wwinc.moviewall.R
import com.wwinc.moviewall.detailsView.DetailsView
import com.wwinc.moviewall.networking.Wallpaper_ModelItem

class GridView_Adapter(val context : Context, var dataList : List<Wallpaper_ModelItem>) :
    RecyclerView.Adapter<GridView_Adapter.GridViewViewHolder>(), Filterable  {

    val datalist2 : List<Wallpaper_ModelItem> = (dataList)

    class GridViewViewHolder (itemView : View ) : RecyclerView.ViewHolder(itemView){
        val thumbnail : ImageView = itemView.findViewById(R.id.gridItemThumb)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridViewViewHolder {
       val view = LayoutInflater.from(parent.context).inflate(R.layout.grid_item, null, false)
        return GridViewViewHolder(view)
    }

    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(holder: GridViewViewHolder, position: Int) {

        Log.d("123as123", "GridView: " + dataList[position])

        Glide
            .with(context)
            .load(dataList[position].imageURL)
            .thumbnail(Glide.with(context).load(R.drawable.placeholder))
            .into(holder.thumbnail)


        holder.thumbnail.setOnClickListener(View.OnClickListener { v ->

            var intent : Intent = Intent(v.context, DetailsView::class.java)
            var listRand = ArrayList<Wallpaper_ModelItem>(dataList)
            intent.putParcelableArrayListExtra("wallpaper_model", listRand)
            intent.putExtra("POSTITION", position)
            v.context.startActivity(intent)

        })



    }

    override fun getFilter(): Filter {
        return object : Filter(){
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                var filteredList  = mutableListOf<Wallpaper_ModelItem>()

                if(constraint.toString().isEmpty()){
                    filteredList = datalist2 as MutableList<Wallpaper_ModelItem>
                }else{
                    for (items in datalist2){
                        if (items.imageURL!!.toLowerCase().contains(constraint.toString().toLowerCase())){
//                            filteredList = listOf(Wallpaper_ModelItem(items.imageURL, items.favOrNot))
                            filteredList.add(Wallpaper_ModelItem(items.imageURL, items.favOrNot))
                        }
                    }
                }
                val filterResult = FilterResults()
                filterResult.values = filteredList
                return filterResult
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {

                dataList = results!!.values as List<Wallpaper_ModelItem>
                notifyDataSetChanged()

            }

        }
    }




}

