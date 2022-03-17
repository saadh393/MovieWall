package com.wwinc.moviewall.Model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "toprated_wallpaper_table")
data class EntityTopRatedWallpaper(
    @SerializedName("imageURL")
    @PrimaryKey val imageURL: String,

    @SerializedName("fav")
    val fav : Boolean = false
)