package com.udacity.project.spire.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity representing a country.
 * A country can have many cities.
 *
 * TODO #1: Add Room annotations to this entity
 *  1. Mark this class as a Room @Entity with tableName = "countries"
 *  2. Mark 'id' as @PrimaryKey with autoGenerate = true
 *  3. Add an @Index on 'name' with unique = true
 *     - This ensures country names are unique in the database
 *     - Improves query performance when filtering by country name
 *
 *  HINT: A country can have many cities (one-to-many relationship)
 *
 *  Required imports:
 *  - androidx.room.Entity
 *  - androidx.room.PrimaryKey
 *  - androidx.room.Index
 */
@Entity(tableName = "countries", indices = [Index(value = ["name"], unique = true)])
data class CountryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int  = 0,
    val name: String,
    val code: String  // ISO country code (e.g., "US", "AE", "CN")
)
