package com.udacity.project.spire.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.udacity.project.spire.data.local.entity.BuildingRemoteKeys

/**
 * Data Access Object for BuildingRemoteKeys.
 * Manages pagination state for buildings.
 *
 * TODO #9: Implement RemoteKeysDao for Paging3 with Room annotations
 *
 * For insertAll(remoteKeys: List<BuildingRemoteKeys>):
 *   - Add @Insert(onConflict = OnConflictStrategy.REPLACE)
 *   - Returns suspend fun (no return value)
 *   - Called after fetching a page from the API
 *   - All buildings on the same page get the same prevKey/nextKey values
 *
 * For remoteKeysByBuildingId(buildingId: Int):
 *   - Add @Query with SQL: "SELECT * FROM building_remote_keys WHERE buildingId = :buildingId"
 *   - Returns suspend fun BuildingRemoteKeys?
 *   - Used by RemoteMediator to determine which page to load next
 *   - Returns null if building doesn't exist (database is empty)
 *
 * For clearRemoteKeys():
 *   - Add @Query with SQL: "DELETE FROM building_remote_keys"
 *   - Returns suspend fun (no return value)
 *   - Called during REFRESH to reset pagination state
 *   - Must be called in same transaction as clearBuildings()
 *
 * NOTE: RemoteKeys are crucial for Paging3's RemoteMediator pattern
 * They track which page each building belongs to, enabling proper pagination
 *
 * Required imports:
 * - androidx.room.Insert
 * - androidx.room.OnConflictStrategy
 * - androidx.room.Query
 */
@Dao
interface BuildingRemoteKeysDao {

    /**
     * Insert or replace remote keys for multiple buildings.
     * Called after fetching a page from the API.
     *
     * @param remoteKeys List of remote keys to insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKeys: List<BuildingRemoteKeys>)

    /**
     * Get remote keys for a specific building.
     * Used to determine which page to load next.
     *
     * @param buildingId The building ID
     * @return RemoteKeys for that building, or null if not found
     */
    @Query("SELECT * FROM building_remote_keys WHERE buildingId = :buildingId")
    suspend fun remoteKeysByBuildingId(buildingId: Int): BuildingRemoteKeys?

    /**
     * Clear all remote keys.
     * Called during REFRESH to reset pagination state.
     */
    @Query("DELETE FROM building_remote_keys")
    suspend fun clearRemoteKeys()
}
