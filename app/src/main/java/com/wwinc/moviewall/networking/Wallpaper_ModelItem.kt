package com.wwinc.moviewall.networking

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Wallpaper_ModelItem(
    @SerializedName("imageURL")
    val imageURL: String?,
    @SerializedName("favOrNot")
    var favOrNot : Boolean?
) : Parcelable