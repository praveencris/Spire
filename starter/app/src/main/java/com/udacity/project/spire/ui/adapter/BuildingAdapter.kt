package com.udacity.project.spire.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.udacity.project.spire.R
import com.udacity.project.spire.databinding.ItemBuildingBinding
import com.udacity.project.spire.domain.model.Building

/**
 * Simple adapter for displaying a list of buildings in a RecyclerView.
 * Used for filtered lists (MyVisitsFragment) without pagination.
 *
 * TODO #44: Implement BuildingAdapter
 *
 * This adapter:
 * 1. Extends ListAdapter for simple list display
 * 2. Uses DiffUtil for efficient updates
 * 3. Similar to BuildingPagingAdapter but for non-paginated lists
 *
 * KEY DIFFERENCES from PagingDataAdapter:
 * - ListAdapter is simpler, for non-paginated lists
 * - Items are never null (no placeholders)
 * - Use submitList() to update data instead of submitData()
 *
 * @param onItemClick Callback invoked when a building item is clicked
 */
class BuildingAdapter(
    private val onItemClick: (Building) -> Unit
) : ListAdapter<Building, BuildingAdapter.BuildingViewHolder>(BUILDING_COMPARATOR) {

    /**
     * TODO #44a: Implement onCreateViewHolder()
     *
     * HINT: Same implementation as BuildingPagingAdapter - inflate ItemBuildingBinding and return BuildingViewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BuildingViewHolder {
        //TODO("Implement onCreateViewHolder() - see BuildingPagingAdapter for reference")
        val binding = ItemBuildingBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return BuildingViewHolder(binding,onItemClick)
    }

    /**
     * TODO #44b: Implement onBindViewHolder()
     *
     * HINT: Call holder.bind(getItem(position)) - simpler than PagingAdapter since getItem() never returns null
     */
    override fun onBindViewHolder(holder: BuildingViewHolder, position: Int) {
        val building = getItem(position)
        holder.bind(building)

        //TODO("Implement onBindViewHolder() - see TODO comment above")
    }

    /**
     * TODO #44c: Implement ViewHolder bind() method
     *
     * (Same implementation as BuildingPagingAdapter)
     */
    class BuildingViewHolder(
        private val binding: ItemBuildingBinding,
        private val onItemClick: (Building) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(building: Building) {
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
            //TODO("Implement bind() - same as BuildingPagingAdapter.ViewHolder.bind()")
        }
    }

    companion object {
        /**
         * DiffUtil callback (already implemented).
         * Same as BuildingPagingAdapter.
         */
        private val BUILDING_COMPARATOR = object : DiffUtil.ItemCallback<Building>() {
            override fun areItemsTheSame(oldItem: Building, newItem: Building): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Building, newItem: Building): Boolean {
                return oldItem == newItem
            }
        }
    }
}
