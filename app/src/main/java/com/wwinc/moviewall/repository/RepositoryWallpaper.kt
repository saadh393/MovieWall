package com.wwinc.moviewall.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.wwinc.moviewall.database.DatabaseWallpaper
import com.wwinc.moviewall.Model.EntityTopRatedWallpaper
import com.wwinc.moviewall.Model.EntityWallpaper
import com.wwinc.moviewall.Model.OtherAppListModel
import com.wwinc.moviewall.networking.WallpaperApi
import com.wwinc.moviewall.networking.Wallpaper_ModelItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RepositoryWallpaper(private val databaseRef: DatabaseWallpaper) {

    // Top Rated Wallpaper Repository | Fetching Urls from Database and making data available for the views
    var topRatedWallPaper: LiveData<List<Wallpaper_ModelItem>> =
        Transformations.map(databaseRef.getDaoTopRated.get_toprated_links()) {
            it.map {
                Wallpaper_ModelItem(
                    imageURL = it.imageURL,
                    favOrNot = false
                )
            }
        }

    // Popular Wallpaper Repository | Fetching Urls from Database and making data available for the views
    var popularWallpaperData: LiveData<List<Wallpaper_ModelItem>> = Transformations.map(databaseRef.getDaoPopular.get_all_links()){ it ->
        it.shuffled().map {
            Wallpaper_ModelItem(
                imageURL = it.imageURL,
                favOrNot = it.fav
            )

        }
    }


    // Fav Wallpapers | Fetching Urls from Database and making data available for the views
    val favouriteWallpapersViewModel : LiveData<List<Wallpaper_ModelItem>> = Transformations.map(databaseRef.getDaoPopular.get_all_favLinks()){
        it.map {
            Wallpaper_ModelItem(
                imageURL = it.imageURL,
                favOrNot = it.fav
            )

        }
    }

    // Fav Wallpapers | Fetching Urls from Database and making data available for the views
    val favouriteWallpapersTopRated : LiveData<List<Wallpaper_ModelItem>> = Transformations.map(databaseRef.getDaoTopRated.get_all_favLinks()){
        it.map {
            Wallpaper_ModelItem(
                imageURL = it.imageURL,
                favOrNot = it.fav
            )

        }
    }


    // Getting Our Other App List | Fetching Urls from Database and making data available for the views
    val getOurOtherApplist : LiveData<List<OtherAppListModel>> = databaseRef.getDaoOtherapps.getOtherApplist()



    /*

    * Fetching Data from Online & Updating the Database asynchronous
    * This Mehod is called from Splash_Screen.kt
    *
    * */
    suspend fun refreshWallpapers() {

        withContext(Dispatchers.IO) {
            // Popular Wallpaper Updating to Database
            val imageUrl = WallpaperApi.retrofitService.getLinksAsync().await(); //  Fetching Images From Server
            databaseRef.getDaoPopular.update_all_links(imageUrl.map { // Updating the Database
                EntityWallpaper(imageURL = it.imageURL!!)
            })

//            Top Rated Wallpaper Updating to Database
            val topRated_imgUrl = WallpaperApi.retrofitService.getTopRated().await()
            databaseRef.getDaoTopRated.update_toprated_links(topRated_imgUrl.map {
                EntityTopRatedWallpaper(
                    imageURL = it.imageURL!!
                )
            })

            // Other Apps List
//            val otherApplist = WallpaperApi.retrofitService.othersApplist2().await()
//            databaseRef.getDaoOtherapps.insertData(otherApplist)

        }


    }

}





