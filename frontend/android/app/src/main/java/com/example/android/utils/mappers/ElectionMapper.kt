package com.example.android.utils.mappers

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.android.dto.response.elections.ElectionResponseDTO
import com.example.android.entity.election.ElectionEntity
import java.time.LocalDate

object ElectionMapper {
    fun ElectionResponseDTO.toEntity(): ElectionEntity = ElectionEntity(
        id = this.id,
        electionName = this.electionName,
        description = this.description,
        startDate = this.startDate.toString(),
        endDate = this.endDate.toString(),
        electionType = this.electionType,
        status = this.status,
        createdById = this.createdById
    )

    @RequiresApi(Build.VERSION_CODES.O)
    fun ElectionEntity.toDto(): ElectionResponseDTO = ElectionResponseDTO(
        id = this.id!!,
        electionName = this.electionName,
        description = this.description,
        startDate = LocalDate.parse(this.startDate).toString(),
        endDate = LocalDate.parse(this.endDate).toString(),
        electionType = this.electionType,
        status = this.status,
        createdById = this.createdById,
        candidates = emptyList(), // You can populate this separately
        parties = emptyList()
    )
}