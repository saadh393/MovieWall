package com.wwinc.moviewall.favourite_fragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wwinc.moviewall.database.DatabaseWallpaper
import com.wwinc.moviewall.popular.PopularWallpapersViewModel
import com.wwinc.moviewall.repository.RepositoryWallpaper
import java.lang.IllegalArgumentException

class Viewmodel_Fav( databaseRef : DatabaseWallpaper) : ViewModel() {

    val repository = RepositoryWallpaper(databaseRef)
    val wallpaper_url = repository.favouriteWallpapersViewModel



}

class Viewmodel_FavFactory(val databaseRef : DatabaseWallpaper) : ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(Viewmodel_Fav::class.java)){
            return Viewmodel_Fav(databaseRef) as T
        }
        throw IllegalArgumentException("Error In Argument")
    }

}
