package com.udacity.project.spire.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.udacity.project.spire.data.local.entity.CountryEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Country entity.
 * Provides essential database operations for countries.
 *
 * TODO #7: Implement DAO methods with Room annotations
 *
 * For getAllCountries():
 *   - Add @Query annotation with SQL: "SELECT * FROM countries ORDER BY name ASC"
 *   - Returns Flow<List<CountryEntity>> for reactive updates
 *   - Flow automatically emits new data when the countries table changes
 *
 * For getCountryByName(name: String):
 *   - Add @Query with SQL: "SELECT * FROM countries WHERE name = :name"
 *   - Returns suspend fun CountryEntity?
 *   - Used to check if a country exists before inserting
 *   - :name is a bind parameter that gets replaced with the method argument
 *
 * For insertCountry(country: CountryEntity):
 *   - Add @Insert(onConflict = OnConflictStrategy.REPLACE)
 *   - Returns suspend fun Long (the row ID of the inserted country)
 *   - REPLACE strategy: If country already exists, replace it
 *
 * HINT: Use Flow for queries that need reactive updates
 * HINT: Use suspend for one-time operations (insert, update, delete)
 *
 * Required imports:
 * - androidx.room.Insert
 * - androidx.room.OnConflictStrategy
 * - androidx.room.Query
 */
@Dao
interface CountryDao {

    /**
     * Get all countries.
     * @return Flow of all countries sorted alphabetically by name
     */
    @Query("SELECT * FROM countries ORDER BY name ASC")
    fun getAllCountries(): Flow<List<CountryEntity>>

    /**
     * Find a country by name.
     * Used to check if country exists before inserting.
     * @param name The country name
     * @return CountryEntity if found, null otherwise
     */
    @Query("SELECT * FROM countries WHERE name = :name")
    suspend fun getCountryByName(name: String): CountryEntity?

    /**
     * Insert a country, replacing on conflict.
     * @param country The country to insert
     * @return The row ID of the inserted country
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCountry(country: CountryEntity): Long
}
