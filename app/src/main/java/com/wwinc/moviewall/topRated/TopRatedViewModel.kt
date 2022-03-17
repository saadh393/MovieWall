package com.wwinc.moviewall.topRated

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wwinc.moviewall.database.DatabaseWallpaper
import com.wwinc.moviewall.repository.RepositoryWallpaper
import java.lang.IllegalArgumentException

class TopRatedViewModel(databaseRef : DatabaseWallpaper) : ViewModel() {

    val repository = RepositoryWallpaper(databaseRef)
    val topRatedWallpaper = repository.topRatedWallPaper



}

// ViewModel Factory
class TopRatedViewmodelFactory(val databaseRef: DatabaseWallpaper) : ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TopRatedViewModel::class.java)){
            return TopRatedViewModel(databaseRef) as T
        }
        throw IllegalArgumentException("Illegal Argument")
    }

}
