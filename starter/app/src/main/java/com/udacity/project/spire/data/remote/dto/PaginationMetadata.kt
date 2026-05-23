package com.udacity.project.spire.data.remote.dto

import com.google.gson.annotations.SerializedName

data class PaginationMetadata(
    @SerializedName("current_page")
    val currentPage: Int,

    @SerializedName("page_size")
    val pageSize: Int,

    @SerializedName("total_items")
    val totalItems: Int,

    @SerializedName("total_pages")
    val totalPages: Int,

    @SerializedName("has_next")
    val hasNext: Boolean,

    @SerializedName("has_previous")
    val hasPrevious: Boolean
)
