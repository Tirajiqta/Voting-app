package com.example.android.dao

import android.database.sqlite.SQLiteDatabase
import com.example.android.entity.LocationRegionEntity

class LocationRegionDao(db: SQLiteDatabase) : GenericDao<LocationRegionEntity>(db, LocationRegionEntity::class)
