package com.udacity.project.spire.ui.detail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import coil.load
import com.google.android.material.snackbar.Snackbar
import com.udacity.project.spire.R
import com.udacity.project.spire.SpireApplication
import com.udacity.project.spire.databinding.FragmentBuildingDetailBinding
import com.udacity.project.spire.domain.model.Building
import com.udacity.project.spire.domain.model.VisitStatus

class BuildingDetailFragment : Fragment() {

    private var _binding: FragmentBuildingDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BuildingDetailViewModel by viewModels {
        BuildingDetailViewModelFactory(
            repository = (requireActivity().application as? SpireApplication)
                ?.buildingRepository
                ?: throw IllegalStateException("Application must be SpireApplication")
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBuildingDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
        setupButtons()
    }

    private fun observeViewModel() {
        viewModel.building.observe(viewLifecycleOwner) { building ->
            building?.let {
                displayBuildingDetails(it)
            }
        }

        // Observe error events
        viewModel.errorEvent.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { errorEvent ->
                Snackbar.make(
                    binding.root,
                    errorEvent.message,
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }

        // Observe success events
        viewModel.updateSuccess.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { message ->
                Snackbar.make(
                    binding.root,
                    message,
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun displayBuildingDetails(building: Building) {
        Log.d("BuildingDetailFragment", "Displaying building details: $building")
        binding.apply {
            // TODO #48: Bind building details, image loading and update buttons
            // 1. Set text views: buildingName, location, height, floors, year, style, description
            // 2. Load image with Coil: buildingImage.load(building.imageUrl)
            // 3. Call updateButtons(building.visitStatus) to set button states
            //updateButtons(building.visitStatus)
            textBuildingName.text = building.name
            textLocation.text =
                getString(R.string.building_location_format, building.city, building.country)
            textHeight.text = getString(R.string.building_height_format, building.heightMeters)
            textFloors.text = getString(R.string.building_floors_format, building.floors)
            textYear.text = getString(R.string.building_year_format, building.yearCompleted)
            textDescription.text = building.description
            textStyle.text = building.architecturalStyle
            imageBuilding.load(building.imageUrl)
            updateButtons(building.visitStatus)
        }
    }

    private fun setupButtons() {
        binding.buttonBucketList.setOnClickListener {
            viewModel.building.value?.let { building ->
                // TODO #49: Toggle between BUCKET_LIST and NOT_VISITED
                // If currently BUCKET_LIST -> change to NOT_VISITED
                // If currently NOT_VISITED -> change to BUCKET_LIST
                // Call viewModel.updateVisitStatus(building.id, newStatus)
                var status = building.visitStatus
                if (status == VisitStatus.BUCKET_LIST) {
                    status = VisitStatus.NOT_VISITED
                } else if (status == VisitStatus.NOT_VISITED) {
                    status = VisitStatus.BUCKET_LIST
                }
                viewModel.updateVisitStatus(status)
            }
        }

        binding.buttonVisited.setOnClickListener {
            viewModel.building.value?.let { building ->
                // TODO #50: Toggle between VISITED and NOT_VISITED
                // If currently VISITED -> change to NOT_VISITED
                // If currently NOT_VISITED or BUCKET_LIST -> change to VISITED
                // Call viewModel.updateVisitStatus(building.id, newStatus)
                var status = building.visitStatus
                if (status == VisitStatus.VISITED) {
                    status = VisitStatus.NOT_VISITED
                } else if (status == VisitStatus.NOT_VISITED || status == VisitStatus.BUCKET_LIST) {
                    status = VisitStatus.VISITED
                }
                viewModel.updateVisitStatus(status)
            }
        }
    }

    private fun updateButtons(status: VisitStatus) {
        binding.apply {
            when (status) {
                VisitStatus.NOT_VISITED -> {
                    buttonBucketList.visibility = View.VISIBLE
                    buttonBucketList.text = getString(R.string.button_add_to_bucket_list)
                    buttonVisited.visibility = View.VISIBLE
                    buttonVisited.text = getString(R.string.button_mark_as_visited)
                }

                VisitStatus.BUCKET_LIST -> {
                    buttonBucketList.visibility = View.VISIBLE
                    buttonBucketList.text = getString(R.string.button_remove_from_bucket_list)
                    buttonVisited.visibility = View.VISIBLE
                    buttonVisited.text = getString(R.string.button_mark_as_visited)
                }

                VisitStatus.VISITED -> {
                    buttonBucketList.visibility = View.GONE
                    buttonVisited.visibility = View.VISIBLE
                    buttonVisited.text = getString(R.string.button_mark_as_unvisited)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}