package com.wwinc.moviewall.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.wwinc.moviewall.Model.OtherAppListModel

@Dao
interface DaoOtherApplist {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertData(links : List<OtherAppListModel>)

    @Query("SELECT * FROM otherAppsList")
    fun getOtherApplist(): LiveData<List<OtherAppListModel>>


}