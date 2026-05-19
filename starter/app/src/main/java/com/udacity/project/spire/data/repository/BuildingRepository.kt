package com.udacity.project.spire.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import com.udacity.project.spire.data.local.database.SpireDatabase
import com.udacity.project.spire.data.local.entity.BuildingEntity
import com.udacity.project.spire.data.local.entity.CityEntity
import com.udacity.project.spire.data.local.entity.CountryEntity
import com.udacity.project.spire.data.remote.api.BuildingApiService
import com.udacity.project.spire.data.remote.dto.BuildingDto
import com.udacity.project.spire.data.remote.dto.PaginationMetadata
import com.udacity.project.spire.domain.model.Building
import com.udacity.project.spire.domain.model.BuildingStatistics
import com.udacity.project.spire.domain.model.VisitStatus
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for building data operations.
 * Abstracts data sources (local database and remote API).
 */
interface BuildingRepository {

    companion object {
        /** Default page size for paginated requests */
        const val DEFAULT_PAGE_SIZE = 10
    }

    /**
     * Get paginated buildings using Paging 3.
     * Data is loaded from the remote API incrementally as needed.
     * @return Flow of PagingData containing buildings for use with PagingDataAdapter
     */
    fun getBuildings(): Flow<PagingData<Building>>

    /**
     * Get a specific building by ID.
     * @param id The building ID
     * @return Flow of the building or null if not found
     */
    fun getBuildingById(id: Int): Flow<Building?>

    /**
     * Refresh buildings from the remote API and cache locally.
     * @return Result indicating success or failure
     */
    suspend fun refreshBuildings(): Result<Unit>

    /**
     * Refresh buildings from the remote API with pagination and cache locally.
     * @param page The page number (starting from 1)
     * @param limit The number of items per page (default: 10)
     * @return Result containing pagination metadata or failure
     */
    suspend fun refreshBuildingsPaginated(
        page: Int,
        limit: Int = DEFAULT_PAGE_SIZE
    ): Result<PaginationMetadata?>

    /**
     * Update the visit status of a building.
     * @param buildingId The building ID to update
     * @param status The new visit status
     * @return Result indicating success or failure
     */
    suspend fun updateBuildingVisitStatus(buildingId: Int, status: VisitStatus): Result<Unit>

    /**
     * Get all buildings in a specific country.
     * @param country The country name
     * @return Flow of buildings in that country
     */
    fun getBuildingsByCountry(country: String): Flow<List<Building>>

    /**
     * Get all buildings with a specific visit status.
     * @param status The visit status to filter by
     * @return Flow of buildings with that status
     */
    fun getBuildingsByVisitStatus(status: VisitStatus): Flow<List<Building>>

    /**
     * Get all unique countries from the database.
     * @return Flow of country names
     */
    fun getAllCountries(): Flow<List<String>>

    /**
     * Get statistics about buildings and visits.
     * @return BuildingStatistics with counts and totals
     */
    suspend fun getStatistics(): BuildingStatistics
}


/**
 * Implementation of BuildingRepository.
 * Manages data operations between local database and remote API.
 *
 * This repository is the single source of truth for building data.
 * It coordinates between local database (Room) and remote API (Retrofit).
 *
 * @param database The Room database instance
 * @param apiService Remote API service
 * @param ioDispatcher Coroutine dispatcher for IO operations (default: Dispatchers.IO)
 *
 * TODO #29-37: Implement all 9 repository methods
 *
 * KEY PATTERNS TO FOLLOW:
 *
 * 1. DATABASE IS SINGLE SOURCE OF TRUTH:
 *    - UI always reads from database (via DAO queries)
 *    - API is only used to fetch new data
 *    - RemoteMediator handles synchronization
 *
 * 2. FLOW FOR REACTIVE DATA:
 *    - Use Flow for data that UI observes (getBuildings, getBuildingById, etc.)
 *    - Flow automatically emits new values when database changes
 *    - Don't manually trigger emissions - Room does it automatically
 *
 * 3. RESULT WRAPPER FOR OPERATIONS:
 *    - Use Result<T> for operations that can fail
 *    - Return Result.success(value) on success
 *    - Return Result.failure(exception) on error
 *    - Wrap API/DB calls in try-catch
 *
 * 4. COROUTINES AND DISPATCHERS:
 *    - Use withContext(ioDispatcher) for suspend functions
 *    - This moves work to background thread
 *    - Room and Retrofit already use background threads, but explicit is better
 *
 * 5. MAPPING LAYERS:
 *    - DTO (API) → Entity (Database) → Domain Model (UI)
 *    - Always map at repository boundary
 *    - Use extension functions: .toDomainModel(), .toEntity()
 */
@OptIn(ExperimentalPagingApi::class)
class DefaultBuildingRepository(
    private val database: SpireDatabase,
    private val apiService: BuildingApiService,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : BuildingRepository {

    private val buildingDao = database.buildingDao()
    private val cityDao = database.cityDao()
    private val countryDao = database.countryDao()

    /**
     * Helper function to get or create a country entity.
     * @param countryName The name of the country
     * @return The ID of the country entity
     */
    private suspend fun getOrCreateCountry(countryName: String): Int {
        val existingCountry = countryDao.getCountryByName(countryName)
        return if (existingCountry != null) {
            existingCountry.id
        } else {
            val newCountry = CountryEntity(name = countryName, code = "")
            countryDao.insertCountry(newCountry).toInt()
        }
    }

    /**
     * Helper function to get or create a city entity.
     * @param cityName The name of the city
     * @param countryId The ID of the country this city belongs to
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
     * Helper function to convert BuildingDto to BuildingEntity.
     * Creates or gets the necessary Country and City entities.
     * @param dto The building DTO from the API
     * @return The building entity ready to be inserted into the database
     */
    private suspend fun buildingDtoToEntity(dto: BuildingDto): BuildingEntity {
        //val countryId = getOrCreateCountry(dto.country.name)
        //val cityId = getOrCreateCity(dto.city.name, countryId)

        return BuildingEntity(
            id = dto.id,,
            // TODO (Part of #31-32): Add remaining properties
            // Map all properties from BuildingDto to BuildingEntity
            // Use getOrCreateCountry() and getOrCreateCity() to get foreign key IDs
            // Convert VisitStatus to VisitStatusEntity using .toEntity()
            ,
        )
    }

    /**
     * TODO #29: Implement getBuildings() for Paging3
     *
     * Returns a Flow of PagingData for efficient pagination.
     * This is the core query for the main buildings list screen.
     *
     * STEPS:
     * 1. Create Pager with PagingConfig (pageSize, enablePlaceholders, prefetchDistance)
     * 2. Pass BuildingRemoteMediator for network-database sync
     * 3. Use buildingDao.getBuildingsPagingSource() as pagingSourceFactory
     * 4. Map entities to domain models using .toDomainModel()
     *
     * HINTS:
     * - RemoteMediator handles fetching from API
     * - Database is single source of truth (UI reads from database)
     * - Use .map() to transform PagingData<Entity> to PagingData<Domain>
     */
    override fun getBuildings(): Flow<PagingData<Building>> {
        TODO("Implement getBuildings() - see TODO comment above")
    }

    /**
     * TODO #30: Implement getBuildingById()
     *
     * Get a specific building by ID with reactive updates.
     *
     * HINTS:
     * - Call buildingDao.getBuildingById(id) to get Flow<BuildingWithDetails?>
     * - Use .map() to convert entity to domain model
     * - Flow automatically updates when building changes in database
     * - Used in BuildingDetailScreen
     */
    override fun getBuildingById(id: Int): Flow<Building?> {
        TODO("Implement getBuildingById() - see TODO comment above")
    }

    /**
     * TODO #31: Implement refreshBuildings()
     *
     * Fetch first page of buildings from API and save to database.
     *
     * STEPS:
     * 1. Use withContext(ioDispatcher) for background work
     * 2. Use try-catch for error handling
     * 3. Call apiService.getBuildingsPaginated(1, 10)
     * 4. Convert DTOs to entities using buildingDtoToEntity()
     * 5. Insert entities using buildingDao.insertBuildings()
     * 6. Return Result.success(Unit) or Result.failure(exception)
     *
     * HINTS:
     * - Used for pull-to-refresh functionality
     */
    override suspend fun refreshBuildings(): Result<Unit> {
        TODO("Implement refreshBuildings() - see TODO comment above")
    }

    /**
     * TODO #32: Implement refreshBuildingsPaginated()
     *
     * Fetch specific page of buildings from API with pagination metadata.
     *
     * HINTS:
     * - Similar to refreshBuildings() but returns response.pagination
     * - Use withContext(ioDispatcher), try-catch, and Result wrapper
     * - Call apiService.getBuildingsPaginated(page, limit)
     * - Convert DTOs to entities and insert into database
     * - Return Result.success(response.pagination) or Result.failure(exception)
     */
    override suspend fun refreshBuildingsPaginated(
        page: Int,
        limit: Int
    ): Result<PaginationMetadata?> {
        TODO("Implement refreshBuildingsPaginated() - see TODO comment above")
    }

    /**
     * TODO #33: Implement updateBuildingVisitStatus()
     *
     * Update a building's visit status (Visited, Bucket List, Not Visited).
     *
     * STEPS:
     * 1. Get building using buildingDao.getBuildingById(buildingId).first()
     * 2. If building exists, create updated entity using .copy(visitStatus = status.toEntity())
     * 3. Call buildingDao.updateBuilding(updatedBuilding)
     * 4. Return Result.success(Unit) or Result.failure(exception)
     *
     * HINTS:
     * - Use withContext(ioDispatcher) and try-catch
     * - .first() gets current value from Flow
     * - Handle case where building doesn't exist
     * - Flow observers automatically receive updates
     * - Used in BuildingDetailScreen when user clicks status buttons
     */
    override suspend fun updateBuildingVisitStatus(
        buildingId: Int,
        status: VisitStatus
    ): Result<Unit> {
        TODO("Implement updateBuildingVisitStatus() - see TODO comment above")
    }

    /**
     * TODO #34: Implement getBuildingsByCountry()
     *
     * Get all buildings in a specific country with reactive updates.
     *
     * HINTS:
     * - Call buildingDao.getBuildingsByCountry(country)
     * - Use .map() to convert List<BuildingWithDetails> to List<Building>
     * - Map each entity using .toDomainModel()
     */
    override fun getBuildingsByCountry(country: String): Flow<List<Building>> {
        TODO("Implement getBuildingsByCountry() - see TODO comment above")
    }

    /**
     * TODO #35: Implement getBuildingsByVisitStatus()
     *
     * Get all buildings with a specific visit status.
     *
     * HINTS:
     * - Convert domain VisitStatus to entity using status.toEntity()
     * - Call buildingDao.getBuildingsByVisitStatus(status.toEntity())
     * - Use .map() to convert List<BuildingWithDetails> to List<Building>
     * - Used in MyVisitsScreen for filtering
     */
    override fun getBuildingsByVisitStatus(status: VisitStatus): Flow<List<Building>> {
        TODO("Implement getBuildingsByVisitStatus() - see TODO comment above")
    }

    /**
     * TODO #36: Implement getAllCountries()
     *
     * Get list of all unique countries with reactive updates.
     *
     * HINTS:
     * - Call countryDao.getAllCountries() to get Flow<List<CountryEntity>>
     * - Use .map() to extract just the country names
     * - Return Flow<List<String>>
     * - Used in CountriesScreen
     */
    override fun getAllCountries(): Flow<List<String>> {
        TODO("Implement getAllCountries() - see TODO comment above")
    }

    /**
     * TODO #37: Implement getStatistics()
     *
     * Calculate aggregated statistics about buildings and visits.
     *
     * STEPS:
     * 1. Use withContext(ioDispatcher)
     * 2. Call buildingDao methods:
     *    - getBuildingCount()
     *    - getCountByStatus(VISITED), getCountByStatus(BUCKET_LIST)
     *    - getTotalMetersClimbed(VISITED) - returns Int?, use ?: 0
     *    - getVisitedCountriesCount(VISITED)
     * 3. Construct and return BuildingStatistics with all values
     *
     * HINTS:
     * - Call multiple DAO methods to gather stats
     * - Used in StatisticsScreen
     */
    override suspend fun getStatistics(): BuildingStatistics {
        TODO("Implement getStatistics() - see TODO comment above")
    }
}
