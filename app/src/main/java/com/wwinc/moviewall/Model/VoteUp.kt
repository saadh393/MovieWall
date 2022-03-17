package com.wwinc.moviewall.Model

import com.google.gson.annotations.SerializedName

data class VoteUp(
    @SerializedName("pack")
    val pack : String,

    @SerializedName("url")
    val url : String)