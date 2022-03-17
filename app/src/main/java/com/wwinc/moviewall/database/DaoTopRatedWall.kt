package com.wwinc.moviewall.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.wwinc.moviewall.Model.EntityTopRatedWallpaper
import com.wwinc.moviewall.Model.EntityWallpaper

@Dao
interface DaoTopRatedWall {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun update_toprated_links(links : List<EntityTopRatedWallpaper>)

    @Query("SELECT * FROM toprated_wallpaper_table")
    fun get_toprated_links(): LiveData<List<EntityTopRatedWallpaper>>

    @Query("UPDATE toprated_wallpaper_table SET fav = :fav WHERE imageURL = :imgurl")
    fun update_fav (imgurl : String, fav : Boolean)

    @Query("SELECT * FROM toprated_wallpaper_table WHERE fav = :fav")
    fun get_all_favLinks(fav : Boolean = true) : LiveData<List<EntityWallpaper>>


}