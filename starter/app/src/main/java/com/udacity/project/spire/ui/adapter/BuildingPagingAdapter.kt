package com.udacity.project.spire.ui.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.udacity.project.spire.R
import com.udacity.project.spire.databinding.ItemBuildingBinding
import com.udacity.project.spire.domain.model.Building

/**
 * PagingDataAdapter for displaying buildings in a RecyclerView with Paging 3.
 * Automatically handles pagination, loading states, and efficient updates.
 *
 * TODO #43: Implement BuildingPagingAdapter
 *
 * This adapter:
 * 1. Extends PagingDataAdapter for Paging3 support
 * 2. Uses ViewBinding for type-safe view access
 * 3. Uses DiffUtil for efficient RecyclerView updates
 * 4. Loads images with Coil library
 * 5. Handles item click events
 *
 * KEY CONCEPTS:
 * - PagingDataAdapter: RecyclerView adapter that works with PagingData
 * - DiffUtil: Calculates minimal changes between lists
 * - ViewHolder pattern: Reuses views for performance
 * - ViewBinding: Type-safe access to XML layout views
 *
 * @param onItemClick Callback invoked when a building item is clicked
 */
class BuildingPagingAdapter(
    private val onItemClick: (Building) -> Unit
) : PagingDataAdapter<Building, BuildingPagingAdapter.BuildingViewHolder>(BUILDING_COMPARATOR) {

    /**
     * TODO #43a: Implement onCreateViewHolder()
     *
     * Creates a new ViewHolder for building items.
     *
     * HINTS:
     * - Use ItemBuildingBinding.inflate() with LayoutInflater.from(parent.context)
     * - Pass parent and false as parameters (don't attach to parent yet)
     * - Return BuildingViewHolder(binding, onItemClick)
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BuildingViewHolder {
        //TODO("Implement onCreateViewHolder() - see TODO comment above")
        val binding = ItemBuildingBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return BuildingViewHolder(binding, onItemClick)
    }

    /**
     * TODO #43b: Implement onBindViewHolder()
     *
     * Binds data to an existing ViewHolder.
     *
     * HINTS:
     * - Call getItem(position) to get building
     * - Check if building is not null (can be null while loading)
     * - Call holder.bind(building) to update UI
     */
    override fun onBindViewHolder(holder: BuildingViewHolder, position: Int) {
        //TODO("Implement onBindViewHolder() - see TODO comment above")
        val building = getItem(position)
        if (building != null) {
            holder.bind(building)
        }
    }

    /**
     * ViewHolder for building items.
     * Displays building information and handles click events.
     *
     * TODO #43c: Implement bind() method in ViewHolder
     */
    class BuildingViewHolder(
        private val binding: ItemBuildingBinding,
        private val onItemClick: (Building) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        /**
         * TODO #43c: Implement bind() method
         *
         * Binds building data to views.
         *
         * STEPS:
         * 1. Use binding.apply { } to access views
         * 2. Set text fields: name, location, height, floors
         * 3. Load image using imageBuilding.load(building.imageUrl)
         * 4. Set click listener on root
         *
         * HINTS:
         * - Combine city and country for location: "${building.city}, ${building.country}"
         * - Use Coil's load() with crossfade, placeholder, and error options
         * - root is the entire item layout
         */
        @SuppressLint("SetTextI18n")
        fun bind(building: Building) {
           // TODO("Implement bind() - see TODO comment above")
            binding.apply {
                textBuildingName.text = building.name
                textBuildingLocation.text = binding.root.context.getString(R.string.building_location_format,building.city,building.country)
                textBuildingHeight.text = binding.root.context.getString(R.string.building_height_format,building.heightMeters)
                textBuildingFloors.text = binding.root.context.getString(R.string.building_floors_format,building.floors)
                imageBuilding.load(building.imageUrl)
                root.setOnClickListener {
                    onItemClick(building)
                }
            }
        }
    }

    companion object {
        /**
         * DiffUtil callback for efficiently calculating differences between lists.
         * Used by PagingDataAdapter to determine which items have changed.
         *
         * This is already implemented for you - DiffUtil is a key RecyclerView optimization.
         */
        private val BUILDING_COMPARATOR = object : DiffUtil.ItemCallback<Building>() {
            override fun areItemsTheSame(oldItem: Building, newItem: Building): Boolean {
                // Check if items represent the same building (by ID)
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Building, newItem: Building): Boolean {
                // Check if all contents are the same
                return oldItem == newItem
            }
        }
    }
}
