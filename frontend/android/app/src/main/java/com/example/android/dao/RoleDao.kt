package com.example.android.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.android.entity.PermissionEntity
import com.example.android.entity.RoleEntity
import com.example.android.entity.RolePermissionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RoleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(role: RoleEntity)

    @Update
    suspend fun update(role: RoleEntity)

    @Delete
    suspend fun delete(role: RoleEntity)

    @Query("SELECT * FROM Role WHERE id = :id")
    suspend fun getRoleById(id: Long): RoleEntity?

    @Query("SELECT * FROM Role")
    fun getAllRoles(): Flow<List<RoleEntity>>
    @Transaction
    @Query("SELECT * FROM Role WHERE id = :roleId")
    suspend fun getRoleWithPermissions(roleId: Long): RoleWithPermissions?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRolePermissionCrossRef(crossRef: RolePermissionEntity)

    @Delete
    suspend fun deleteRolePermissionCrossRef(crossRef: RolePermissionEntity)
}
data class RoleWithPermissions(
    @androidx.room.Embedded val role: RoleEntity,
    @androidx.room.Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = androidx.room.Junction(RolePermissionEntity::class)
    )
    val permissions: List<PermissionEntity>
)