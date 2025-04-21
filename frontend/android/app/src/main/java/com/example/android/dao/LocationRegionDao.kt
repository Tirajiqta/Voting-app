package com.example.android.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.android.entity.LocationRegionEntity

@Dao
interface LocationRegionDao {
  @Insert
  fun insertAll(locationRegions: List<LocationRegionEntity>)
  @Query("SELECT * FROM locationregion")
  fun getAll(): List<LocationRegionEntity>

  @Insert
  fun insert(locationRegion: LocationRegionEntity)
  @Query("SELECT * FROM locationregion WHERE id = :id")
  fun getById(id: Long): LocationRegionEntity?
  @Delete
  fun delete(locationRegion: LocationRegionEntity)
  @Update
  fun update(locationRegion: LocationRegionEntity)
}
