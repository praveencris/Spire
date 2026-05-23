package com.udacity.project.spire.ui.visits

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.udacity.project.spire.R
import com.udacity.project.spire.SpireApplication
import com.udacity.project.spire.databinding.FragmentMyVisitsBinding
import com.udacity.project.spire.domain.model.VisitStatus
import com.udacity.project.spire.ui.adapter.BuildingAdapter

class MyVisitsFragment : Fragment() {

    private var _binding: FragmentMyVisitsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: BuildingAdapter

    private val viewModel: MyVisitsViewModel by viewModels {
        MyVisitsViewModelFactory(
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
        _binding = FragmentMyVisitsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupChipGroup()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        adapter = BuildingAdapter { building ->
            val action = MyVisitsFragmentDirections
                .actionVisitsToDetail(building.id)
            findNavController().navigate(action)
        }
        binding.recyclerView.adapter = adapter
    }

    private fun setupChipGroup() {
        binding.chipGroup.setOnCheckedStateChangeListener { _, checkedIds ->
            if (checkedIds.isNotEmpty()) {
                val status = when (checkedIds.first()) {
                    R.id.chip_visited -> VisitStatus.VISITED
                    R.id.chip_bucket_list -> VisitStatus.BUCKET_LIST
                    R.id.chip_not_visited -> VisitStatus.NOT_VISITED
                    else -> VisitStatus.VISITED
                }
                viewModel.setFilterStatus(status)
            }
        }
    }

    private fun observeViewModel() {
        viewModel.buildings.observe(viewLifecycleOwner) { buildings ->
            adapter.submitList(buildings)

            // Show/hide empty state
            val isEmpty = buildings.isEmpty()
            binding.emptyState.isVisible = isEmpty
            binding.recyclerView.isVisible = !isEmpty
        }

        // Update empty state message based on current filter
        viewModel.currentStatus.observe(viewLifecycleOwner) { status ->
            updateEmptyStateMessage(status)
        }

        // Observe error events
        viewModel.errorEvent.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { errorEvent ->
                Snackbar.make(
                    binding.root,
                    errorEvent.message,
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun updateEmptyStateMessage(status: VisitStatus) {
        binding.apply {
            when (status) {
                VisitStatus.VISITED -> {
                    textEmptyTitle.text = getString(R.string.empty_visited_title)
                    textEmptyMessage.text = getString(R.string.empty_visited_message)
                }
                VisitStatus.BUCKET_LIST -> {
                    textEmptyTitle.text = getString(R.string.empty_bucket_list_title)
                    textEmptyMessage.text = getString(R.string.empty_bucket_list_message)
                }
                VisitStatus.NOT_VISITED -> {
                    textEmptyTitle.text = getString(R.string.empty_not_visited_title)
                    textEmptyMessage.text = getString(R.string.empty_not_visited_message)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}