package com.wwinc.moviewall.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.wwinc.moviewall.Model.EntityTopRatedWallpaper
import com.wwinc.moviewall.Model.EntityWallpaper
import com.wwinc.moviewall.Model.OtherAppListModel

@Database(entities = [EntityWallpaper::class, EntityTopRatedWallpaper::class, OtherAppListModel::class],  version = 2 , exportSchema = false)
abstract class DatabaseWallpaper : RoomDatabase(){

    abstract val getDaoPopular : DaoPopularWallpaper
    abstract val getDaoTopRated : DaoTopRatedWall
    abstract val getDaoOtherapps : DaoOtherApplist

    companion object{

        private lateinit var INSTENCE : DatabaseWallpaper

        fun  getDatabase(context : Context) : DatabaseWallpaper{
            synchronized(this){
                if (!::INSTENCE.isInitialized){
                    INSTENCE = Room.databaseBuilder(context,DatabaseWallpaper::class.java,"wallpaper_cache_database")
                        .fallbackToDestructiveMigration()
                    .build()
                }
            }
        return INSTENCE
        }
    }
}