package com.wwinc.moviewall.Model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "wallpaper_url_table")
data class EntityWallpaper(
    @SerializedName("imageURL")
    @PrimaryKey
    val imageURL: String = "https://cdn.wallpapersafari.com/34/82/YRzXPk.jpeg",

    @SerializedName("fav")
    val fav : Boolean? = false
)
