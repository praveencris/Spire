package com.udacity.project.spire.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.udacity.project.spire.data.local.converter.VisitStatusConverter
import com.udacity.project.spire.data.local.dao.BuildingDao
import com.udacity.project.spire.data.local.dao.BuildingRemoteKeysDao
import com.udacity.project.spire.data.local.dao.CityDao
import com.udacity.project.spire.data.local.dao.CountryDao
import com.udacity.project.spire.data.local.entity.BuildingEntity
import com.udacity.project.spire.data.local.entity.BuildingRemoteKeys
import com.udacity.project.spire.data.local.entity.CityEntity
import com.udacity.project.spire.data.local.entity.CountryEntity

/**
 * Room Database for the Spire application.
 * Manages local persistence of building, city, and country data.
 *
 * TODO #22: Configure Room Database with annotations
 *
 *  1. Add @Database annotation with:
 *     entities = [
 *         BuildingEntity::class,
 *         CityEntity::class,
 *         CountryEntity::class,
 *         BuildingRemoteKeys::class
 *     ]
 *     version = 1
 *     exportSchema = false
 *
 *  2. Add @TypeConverters(VisitStatusConverter::class)
 *     - This tells Room how to convert VisitStatusEntity enum to/from database
 *     - Without this, Room wouldn't know how to store the enum
 *
 *  3. Ensure all abstract DAO methods are present:
 *     - buildingDao(): BuildingDao
 *     - cityDao(): CityDao
 *     - countryDao(): CountryDao
 *     - buildingRemoteKeysDao(): BuildingRemoteKeysDao
 *
 *  NOTE: The singleton getInstance() pattern is already implemented for you
 *  This ensures only one database instance exists across the app
 *
 *  HINT: version = 1 because this is the first version of the database schema
 *  HINT: exportSchema = false prevents exporting schema to a file (not needed for this project)
 *  HINT: TypeConverters let Room store custom types (enums, dates, etc.)
 *
 *  Required imports:
 *  - androidx.room.Database
 *  - androidx.room.TypeConverters
 */

@Database(
    entities = [BuildingEntity::class, CityEntity::class, CountryEntity::class, BuildingRemoteKeys::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(VisitStatusConverter::class)

abstract class SpireDatabase : RoomDatabase() {

    /**
     * Provides access to BuildingDao for database operations.
     */
    abstract fun buildingDao(): BuildingDao

    /**
     * Provides access to CityDao for database operations.
     */
    abstract fun cityDao(): CityDao

    /**
     * Provides access to CountryDao for database operations.
     */
    abstract fun countryDao(): CountryDao

    /**
     * Provides access to BuildingRemoteKeysDao for pagination operations.
     */
    abstract fun buildingRemoteKeysDao(): BuildingRemoteKeysDao

    companion object Companion {
        private const val DATABASE_NAME = "spire_database"

        @Volatile
        private var INSTANCE: SpireDatabase? = null

        /**
         * Get the singleton instance of BuildingDatabase.
         * Uses double-check locking pattern to ensure thread safety.
         *
         * @param context Application context
         * @return The BuildingDatabase instance
         */
        fun getInstance(context: Context): SpireDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    SpireDatabase::class.java,
                    DATABASE_NAME
                )
                    .fallbackToDestructiveMigration(true)
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}