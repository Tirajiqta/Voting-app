package com.example.android.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.android.entity.PermissionEntity

@Dao
interface PermissionDao {
    @Query("SELECT * FROM Permission")
    fun getAll(): List<PermissionEntity>
    @Insert
    fun insertAll(vararg permissions: PermissionEntity)
}
