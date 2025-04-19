package com.example.android.utils.mappers

import com.example.android.dto.response.DocumentDTO
import com.example.android.entity.DocumentEntity

object DocumentMapper {

    fun DocumentDTO.toEntity(userId: Long): DocumentEntity = DocumentEntity(
        id = this.id,
        number = this.number,
        validFrom = this.validFrom,
        validTo = this.validTo,
        issuer = this.issuer,
        gender = this.gender,
        dateOfBirth = this.dateOfBirth,
        permanentAddress = this.permanentAddress,
        userId = userId
    )

    fun DocumentEntity.toDto(): DocumentDTO = DocumentDTO(
        id = this.id,
        number = this.number,
        validFrom = this.validFrom,
        validTo = this.validTo,
        issuer = this.issuer,
        gender = this.gender,
        dateOfBirth = this.dateOfBirth,
        permanentAddress = this.permanentAddress,
        user = null // You can fetch & assign the UserDTO if needed later
    )
}