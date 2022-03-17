package com.wwinc.moviewall.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.wwinc.moviewall.Model.EntityWallpaper

@Dao
interface DaoPopularWallpaper {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun update_all_links(links : List<EntityWallpaper>)

    @Query("SELECT * FROM wallpaper_url_table")
    fun get_all_links(): LiveData<List<EntityWallpaper>>

    @Query("UPDATE wallpaper_url_table SET fav = :fav WHERE imageURL = :imgurl")
    fun update_fav (imgurl : String, fav : Boolean)

    @Query("SELECT * FROM wallpaper_url_table WHERE fav = :fav")
    fun get_all_favLinks(fav : Boolean = true) : LiveData<List<EntityWallpaper>>

}