package com.wwinc.moviewall.popular

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wwinc.moviewall.database.DaoPopularWallpaper
import com.wwinc.moviewall.database.DatabaseWallpaper
import com.wwinc.moviewall.repository.RepositoryWallpaper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class PopularWallpapersViewModel( val databaseRef : DatabaseWallpaper) : ViewModel() {

        val repository = RepositoryWallpaper(databaseRef)
        val wallpaper_url = repository.popularWallpaperData




    fun loadData(){
        val repository = RepositoryWallpaper(databaseRef)
        val wallpaper_url = repository.popularWallpaperData
    }

}

class PopularViewmodelFactory(val databaseRef : DatabaseWallpaper) : ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PopularWallpapersViewModel::class.java)){
            return PopularWallpapersViewModel(databaseRef) as T
        }
        throw IllegalArgumentException("Error In Argument")
    }

}




