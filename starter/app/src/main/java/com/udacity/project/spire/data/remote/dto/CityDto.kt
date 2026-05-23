package com.udacity.project.spire.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CityDto(
    val id: Int,
    val name: String,
    @SerializedName("country_id")
    val countryId: Int
)
