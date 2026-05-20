package com.udacity.project.spire.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.udacity.project.spire.data.local.entity.CityEntity

/**
 * Data Access Object for City entity.
 * Provides essential database operations for cities.
 *
 * TODO #8: Implement CityDao queries with Room annotations
 *
 * For getCityByNameAndCountry(name: String, countryId: Int):
 *   - Add @Query with SQL: "SELECT * FROM cities WHERE name = :name AND countryId = :countryId"
 *   - Returns suspend fun CityEntity?
 *   - Used to check if a city exists in a specific country before inserting
 *   - Multiple cities can have the same name in different countries
 *
 * For insertCity(city: CityEntity):
 *   - Add @Insert(onConflict = OnConflictStrategy.REPLACE)
 *   - Returns suspend fun Long (the row ID of the inserted city)
 *   - REPLACE strategy: If city already exists, replace it
 *
 * HINT: This DAO doesn't use Flow because cities are only queried during data import
 * HINT: suspend functions run on background thread automatically
 *
 * Required imports:
 * - androidx.room.Insert
 * - androidx.room.OnConflictStrategy
 * - androidx.room.Query
 */
@Dao
interface CityDao {

    /**
     * Find a city by name and country ID.
     * Used to check if city exists before inserting.
     * @param name The city name
     * @param countryId The country ID this city belongs to
     * @return CityEntity if found, null otherwise
     */
    @Query("SELECT * FROM cities WHERE name = :name AND countryId = :countryId")
    suspend fun getCityByNameAndCountry(name: String, countryId: Int): CityEntity?

    /**
     * Insert a city, replacing on conflict.
     * @param city The city to insert
     * @return The row ID of the inserted city
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCity(city: CityEntity): Long
}
