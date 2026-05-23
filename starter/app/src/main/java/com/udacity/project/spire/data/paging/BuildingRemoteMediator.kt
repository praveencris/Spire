package com.udacity.project.spire.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.udacity.project.spire.data.local.database.SpireDatabase
import com.udacity.project.spire.data.local.entity.BuildingEntity
import com.udacity.project.spire.data.local.entity.BuildingRemoteKeys
import com.udacity.project.spire.data.local.entity.BuildingWithDetails
import com.udacity.project.spire.data.local.entity.CityEntity
import com.udacity.project.spire.data.local.entity.CountryEntity
import com.udacity.project.spire.data.local.entity.VisitStatusEntity
import com.udacity.project.spire.data.local.entity.toEntity
import com.udacity.project.spire.data.remote.api.BuildingApiService
import com.udacity.project.spire.data.remote.dto.BuildingDto

/**
 * RemoteMediator for Building data.
 * Manages fetching data from network and saving to local database.
 * Database is the single source of truth, RemoteMediator handles synchronization.
 *
 * Following the RemoteKeys pattern from:
 * https://www.bornfight.com/blog/android-paging-3-library-with-page-and-limit-parameters/
 */
@OptIn(ExperimentalPagingApi::class)
class BuildingRemoteMediator(
    private val apiService: BuildingApiService,
    private val database: SpireDatabase,
    private val initialPage: Int = 1
) : RemoteMediator<Int, BuildingWithDetails>() {

    private val buildingDao = database.buildingDao()
    private val cityDao = database.cityDao()
    private val countryDao = database.countryDao()
    private val remoteKeysDao = database.buildingRemoteKeysDao()

    /**
     * TODO #28: Implement RemoteMediator.load() for Paging3
     *
     * This is the heart of Paging3's network-database synchronization.
     * This method is called by Paging3 to load data from the network and save it to the database.
     *
     * KEY CONCEPTS:
     * - Called in three scenarios (LoadType):
     *   - REFRESH: Initial load or user swipes to refresh
     *   - PREPEND: User scrolls to beginning (return Success(true))
     *   - APPEND: User scrolls to end, need to load next page
     *
     * STEPS TO IMPLEMENT:
     * 1. Determine which page to load based on LoadType
     *    - REFRESH: Use getRemoteKeyClosestToCurrentPosition() or initialPage
     *    - PREPEND: Return MediatorResult.Success(true) - not supported
     *    - APPEND: Use getRemoteKeyForLastItem().nextKey
     * 2. Fetch data from API using try-catch
     * 3. Save to database in transaction with database.withTransaction
     *    - If REFRESH: Clear remoteKeysDao and buildingDao
     *    - Create BuildingRemoteKeys for each building with prevKey/nextKey
     *    - Convert DTOs to entities using buildingDtoToEntity()
     *    - Insert keys and buildings
     * 4. Return MediatorResult.Success or MediatorResult.Error
     *
     * HINTS:
     * - Helper methods provided: buildingDtoToEntity(), getRemoteKeyClosestToCurrentPosition(), getRemoteKeyForLastItem()
     * - RemoteKeys pattern: All items on same page share same prevKey/nextKey
     * - Use database.withTransaction for atomicity
     *
     * RESOURCES:
     * - https://developer.android.com/topic/libraries/architecture/paging/v3-network-db
     * - https://www.bornfight.com/blog/android-paging-3-library-with-page-and-limit-parameters/
     */
    override suspend fun load(
        loadType: LoadType, state: PagingState<Int, BuildingWithDetails>
    ): MediatorResult {
        // TODO("Implement RemoteMediator.load() - see TODO comment above for detailed steps")
        return try {
            val page = when (loadType) {
                LoadType.REFRESH -> {
                    val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                    remoteKeys?.nextKey?.minus(1) ?: initialPage

                }

                LoadType.PREPEND -> {
                    return MediatorResult.Success(true)
                }

                LoadType.APPEND -> {
                    val remoteKeys = getRemoteKeyForLastItem(state)
                    val nextkey = remoteKeys?.nextKey ?: return MediatorResult.Success(
                        endOfPaginationReached = true
                    )
                    nextkey
                }
            }
            //Fetch data from network
            val response = apiService.getBuildingsPaginated(page)
            val buildings = response.buildings
            val endOfPaginationReached = buildings.isEmpty()

            database.withTransaction {

                // Clear old data on refresh
                if (loadType == LoadType.REFRESH) {
                    remoteKeysDao.clearRemoteKeys()
                    buildingDao.clearBuildings()
                }

                val prevKey = if (page == initialPage) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1

                // Create remote keys
                val keys = buildings.map { dto ->
                    BuildingRemoteKeys(
                        buildingId = dto.id, prevKey = prevKey, nextKey = nextKey
                    )
                }

                remoteKeysDao.insertAll(keys)

                // Convert DTOs to entities
                val buildingEntities = buildings.map { dto ->
                    buildingDtoToEntity(dto)
                }

                // Insert buildings
                buildingDao.insertBuildings(buildingEntities)
            }

            MediatorResult.Success(
                endOfPaginationReached = endOfPaginationReached
            )

        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }

    /**
     * Convert BuildingDto to BuildingEntity.
     * Creates or gets the necessary Country and City entities.
     */
    private suspend fun buildingDtoToEntity(dto: BuildingDto): BuildingEntity {
        //val countryId = getOrCreateCountry()
        //val cityId = getOrCreateCity()

        return BuildingEntity(
            id = dto.id,
            name = dto.name,
            imageUrl = dto.imageUrl,
            heightMeters = dto.heightMeters,
            floors = dto.floors,
            yearCompleted = dto.yearCompleted,
            architecturalStyle = dto.architecturalStyle,
            description = dto.description,
            visitStatus = VisitStatusEntity.NOT_VISITED,
            cityId = dto.city.id

            // TODO (Part of #28): Add remaining properties
            // Map all properties from BuildingDto to BuildingEntity
            // Use getOrCreateCountry() and getOrCreateCity() to get foreign key IDs
            // Convert VisitStatus to VisitStatusEntity using .toEntity()

        )
    }

    /**
     * Get or create a country entity.
     * @return The ID of the country entity
     */
    private suspend fun getOrCreateCountry(countryName: String, code: String): Int {
        val existingCountry = countryDao.getCountryByName(countryName)
        return if (existingCountry != null) {
            existingCountry.id
        } else {
            val newCountry = CountryEntity(name = countryName, code = code)
            countryDao.insertCountry(newCountry).toInt()
        }
    }

    /**
     * Get or create a city entity.
     * @return The ID of the city entity
     */
    private suspend fun getOrCreateCity(cityName: String, countryId: Int): Int {
        val existingCity = cityDao.getCityByNameAndCountry(cityName, countryId)
        return if (existingCity != null) {
            existingCity.id
        } else {
            val newCity = CityEntity(name = cityName, countryId = countryId)
            cityDao.insertCity(newCity).toInt()
        }
    }

    /**
     * Get remote keys for the item closest to current scroll position.
     * Used during REFRESH to restore pagination position.
     *
     * This prevents jumping to the beginning when user swipes to refresh
     * in the middle of the list.
     */
    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, BuildingWithDetails>
    ): BuildingRemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.building?.id?.let { buildingId ->
                database.withTransaction {
                    remoteKeysDao.remoteKeysByBuildingId(buildingId)
                }
            }
        }
    }

    /**
     * Get remote keys for the last item in the list.
     * Used during APPEND to determine next page to load.
     */
    private suspend fun getRemoteKeyForLastItem(
        state: PagingState<Int, BuildingWithDetails>
    ): BuildingRemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { building ->
                database.withTransaction {
                    remoteKeysDao.remoteKeysByBuildingId(building.building.id)
                }
            }
    }

    companion object {
        private const val TAG = "BuildingRemoteMediator"
    }
}