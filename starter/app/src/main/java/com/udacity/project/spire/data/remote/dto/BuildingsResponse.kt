package com.udacity.project.spire.data.remote.dto

data class BuildingsResponse(
    val buildings: List<BuildingDto>,
    val pagination: PaginationMetadata?
)
