package com.wwinc.moviewall.networking

//import retrofit2.converter.moshi.MoshiConverterFactory
import android.icu.util.TimeUnit
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.wwinc.moviewall.Model.OtherAppListModel
import com.wwinc.moviewall.Model.VoteUp
import com.wwinc.moviewall.util.Companion.BASEURL
import com.wwinc.moviewall.util.Companion.NETWORK_KEY
import com.wwinc.moviewall.util.Companion.PACKAGENAME
import kotlinx.coroutines.Deferred
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import javax.xml.datatype.DatatypeConstants.SECONDS


private const val BASE_URL =  BASEURL

var client: OkHttpClient = OkHttpClient.Builder()
    .connectTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
    .writeTimeout(120, java.util.concurrent.TimeUnit.SECONDS)
    .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
    .build()

private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .client(client)
    .addConverterFactory(GsonConverterFactory.create())
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .build();

interface NetworkAPIService {
    @GET("com.wwinc.MovieWall ")
    fun getLinksAsync(): Deferred<List<Wallpaper_ModelItem>>

    @GET("com.wwinc.MovieWall ")
    fun getTopRated(): Deferred<List<Wallpaper_ModelItem>>

    @FormUrlEncoded
    @POST("votingsys.php")
    fun voteImage(@Field("pack") pack: String, @Field("url") url: String) : Call<VoteUp>


    @GET("layouts/moreApps/getdata.php?pacakgeName=$PACKAGENAME")
    fun othersApplist2 () : Deferred<List<OtherAppListModel>>

}

object WallpaperApi{
    val retrofitService : NetworkAPIService by lazy {
        retrofit.create(NetworkAPIService::class.java)
    }
}