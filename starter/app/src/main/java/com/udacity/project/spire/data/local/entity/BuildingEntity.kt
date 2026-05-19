package com.udacity.project.spire.data.local.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.udacity.project.spire.domain.model.Building
import com.udacity.project.spire.domain.model.VisitStatus

/**
 * Room entity representing a building in the local database.
 * A building belongs to a city, which belongs to a country.
 *
 * Database Relationship:
 * Country (1) ──→ (Many) City (1) ──→ (Many) Building
 * Building → City (Many-to-One)
 * City → Country (Many-to-One)
 * Building → Country (Indirect, through City)
 *
 * TODO #3: Add Room annotations for the Building entity
 *  1. Mark this class as @Entity with tableName = "buildings"
 *  2. Mark 'id' as @PrimaryKey (NOT auto-generated - comes from API)
 *  3. Add @ForeignKey constraint to link building to city:
 *     - entity = CityEntity::class
 *     - parentColumns = ["id"]  (CityEntity's primary key)
 *     - childColumns = ["cityId"]  (BuildingEntity's foreign key)
 *     - onDelete = ForeignKey.RESTRICT  (prevent deleting cities with buildings)
 *     - onUpdate = ForeignKey.CASCADE  (update buildingCityId when city id changes)
 *  4. Add indices for performance:
 *     - Index on "cityId" (for foreign key lookups and JOIN queries)
 *     - Index on "name" (for searching buildings by name)
 *
 *  HINT: A building belongs to a city (many-to-one relationship)
 *  HINT: RESTRICT prevents data loss - you can't delete a city that has buildings
 *
 *  Required imports:
 *  - androidx.room.Entity
 *  - androidx.room.PrimaryKey
 *  - androidx.room.ForeignKey
 *  - androidx.room.Index
 */
@Entity(
    tableName = "buildings", foreignKeys = [ForeignKey(
        entity = CityEntity::class,
        parentColumns = ["id"],
        childColumns = ["cityId"],
        onDelete = ForeignKey.RESTRICT,
        onUpdate = ForeignKey.CASCADE
    )], indices = [Index(value = ["cityId"]), Index(value = ["name"])]
)
data class BuildingEntity(
    @PrimaryKey val id: Int,
    // TODO (Part of #3): Add remaining properties
    val name: String,
    val imageUrl: String,
    val heightMeters: Int,
    val floors: Int,
    val yearCompleted: Int,
    val architecturalStyle: String,
    val description: String,
    val visitStatus: VisitStatusEntity,
    val cityId: Int,
    // Refer to Building domain model for the complete list of properties needed
    // Include: name, imageUrl, heightMeters, floors, yearCompleted, architecturalStyle,
    //          description, visitStatus (as VisitStatusEntity), cityId (foreign key)

)

/**
 * Data class representing a building with its city and country details.
 * Used for JOIN queries to get complete building information.
 *
 * TODO #4: Add Room relation annotations
 *  1. Mark 'building' as @Embedded
 *     - This includes all BuildingEntity fields in the query result
 *  2. Add @Relation annotation to 'city':
 *     - entity = CityEntity::class
 *     - parentColumn = "cityId"  (from BuildingEntity)
 *     - entityColumn = "id"  (from CityEntity)
 *     - This tells Room to JOIN CityEntity where building.cityId = city.id
 *
 *  HINT: Room will automatically load the related CityWithCountry object
 *  HINT: This enables multi-level relationships: Building → City → Country
 *
 *  Required imports:
 *  - androidx.room.Embedded
 *  - androidx.room.Relation
 */

data class BuildingWithDetails(
    @Embedded val building: BuildingEntity,
    @Relation(
        entity = CityEntity::class,
        parentColumn = "cityId",
        entityColumn = "id"
    ) val city: CityWithCountry
)

/**
 * Data class representing a city with its country details.
 *
 * TODO #5: Add Room relation annotations
 *  1. Mark 'city' as @Embedded
 *     - This includes all CityEntity fields in the query result
 *  2. Add @Relation annotation to 'country':
 *     - parentColumn = "countryId"  (from CityEntity)
 *     - entityColumn = "id"  (from CountryEntity)
 *     - This tells Room to JOIN CountryEntity where city.countryId = country.id
 *
 *  HINT: This enables the second level of the relationship chain
 *
 *  Required imports:
 *  - androidx.room.Embedded
 *  - androidx.room.Relation
 */
data class CityWithCountry(
    @Embedded val city: CityEntity,
    @Relation(parentColumn = "countryId", entityColumn = "id") val country: CountryEntity
)

/**
 * Extension function to convert BuildingWithDetails to domain Building model.
 */
fun BuildingWithDetails.toDomainModel(): Building {
    return Building(
        id = building.id,
        // TODO (Part of #4): Add remaining properties
        // Map all properties from building, city, and country to the Building domain model
        // Use city.city.name, city.country.name, building.visitStatus.toDomainModel(), etc.

        name = building.name,
        imageUrl = building.imageUrl,
        heightMeters = building.heightMeters,
        floors = building.floors,
        yearCompleted = building.yearCompleted,
        architecturalStyle = building.architecturalStyle,
        description = building.description,
        visitStatus = building.visitStatus,
        cityId = building.cityId,
        cityName = city.city.name,
        countryId = city.city.countryId,
        countryName = city.country.name,
        countryCode = city.country.code
    )
}

/**
 * Extension function to convert VisitStatusEntity to domain VisitStatus model.
 */
fun VisitStatusEntity.toDomainModel(): VisitStatus {
    return when (this) {
        VisitStatusEntity.NOT_VISITED -> VisitStatus.NOT_VISITED
        VisitStatusEntity.BUCKET_LIST -> VisitStatus.BUCKET_LIST
        VisitStatusEntity.VISITED -> VisitStatus.VISITED
    }
}

/**
 * Extension function to convert domain VisitStatus to VisitStatusEntity.
 */
fun VisitStatus.toEntity(): VisitStatusEntity {
    return when (this) {
        VisitStatus.NOT_VISITED -> VisitStatusEntity.NOT_VISITED
        VisitStatus.BUCKET_LIST -> VisitStatusEntity.BUCKET_LIST
        VisitStatus.VISITED -> VisitStatusEntity.VISITED
    }
}