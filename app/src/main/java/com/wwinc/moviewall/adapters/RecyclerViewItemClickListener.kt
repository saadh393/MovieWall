package com.wwinc.moviewall.adapters

import android.view.View

interface RecyclerViewItemClickListener {
    var getImageurl : String

    fun recyclerViewListClicked(imageurl : String) {
        getImageurl = imageurl
    }

}