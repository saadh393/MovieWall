package com.wwinc.moviewall.Model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "otherAppsList")
data class OtherAppListModel(
    @SerializedName("appname")
    @PrimaryKey
    val appname : String,

    @SerializedName("packageName")
    val packageName : String,

    @SerializedName("description")
    val description : String,

    @SerializedName("logoUrl")
    val logoUrl : String,

    @SerializedName("bannerPhoto")
    val bannerPhoto : String

)