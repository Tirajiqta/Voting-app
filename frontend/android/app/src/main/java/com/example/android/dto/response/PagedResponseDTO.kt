package com.example.android.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class PagedResponseDTO<T>(
    val content: List<T>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int,
    val last: Boolean
)