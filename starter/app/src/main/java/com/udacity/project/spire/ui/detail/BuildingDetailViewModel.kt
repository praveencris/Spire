package com.udacity.project.spire.ui.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.udacity.project.spire.data.repository.BuildingRepository
import com.udacity.project.spire.domain.model.Building
import com.udacity.project.spire.domain.model.VisitStatus
import com.udacity.project.spire.ui.common.ErrorEvent
import com.udacity.project.spire.ui.common.Event
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

/**
 * ViewModel for BuildingDetailFragment.
 * Loads a specific building by ID and handles visit status updates.
 *
 * TODO #39: Implement BuildingDetailViewModel
 *
 * This ViewModel:
 * 1. Gets buildingId from navigation arguments via SavedStateHandle
 * 2. Loads building data reactively from repository
 * 3. Updates building visit status
 * 4. Handles error and success events
 *
 * KEY CONCEPTS:
 * - SavedStateHandle: Access navigation arguments passed via SafeArgs
 * - Flow.asLiveData(): Convert Flow to LiveData for UI observation
 * - Event wrapper: One-time events for success/error messages
 */
class BuildingDetailViewModel(
    private val repository: BuildingRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    // Get buildingId from navigation arguments via SavedStateHandle
    private val buildingId =
        BuildingDetailFragmentArgs.fromSavedStateHandle(savedStateHandle).buildingId


    init {
        Log.d("BuildingDetailViewModel", "buildingIdLiveData: ${buildingId}")
    }

    /**
     * TODO #39a: Initialize building property
     *
     * Load building data from repository based on buildingId.
     *
     * HINTS:
     * - Call repository.getBuildingById(buildingId) to get Flow<Building?>
     * - Use .onEach() for optional debug logging
     * - Use .asLiveData() to convert Flow to LiveData
     * - Fragment observes this LiveData to display building details
     */
    val building: LiveData<Building?>
        get() = repository.getBuildingById(buildingId).onEach {
            Log.d("TAG", "Building Details: $it")
        }.asLiveData()

    // Error state exposed to UI
    private val _errorEvent = MutableLiveData<Event<ErrorEvent>>()
    val errorEvent: LiveData<Event<ErrorEvent>> = _errorEvent

    // Success feedback
    private val _updateSuccess = MutableLiveData<Event<String>>()
    val updateSuccess: LiveData<Event<String>> = _updateSuccess

    /**
     * TODO #39b: Implement updateVisitStatus() method
     *
     * Called when user clicks "Mark as Visited" or "Add to Bucket List" buttons.
     *
     * STEPS:
     * 1. Use viewModelScope.launch to start a coroutine
     * 2. Validate buildingId (check if it's -1)
     * 3. Call repository.updateBuildingVisitStatus(buildingId, status)
     * 4. Use .onSuccess() to set _updateSuccess with appropriate message
     * 5. Use .onFailure() to set _errorEvent with error
     *
     * HINTS:
     * - Return early if buildingId is invalid
     * - Use when expression for status-specific success messages
     * - Fragment shows success message via Snackbar
     */
    fun updateVisitStatus(status: VisitStatus) {
        // TODO("Implement updateVisitStatus() - see TODO comment above")
        viewModelScope.launch {
            if (buildingId == -1) {
                repository.updateBuildingVisitStatus(buildingId, status)
                    .onSuccess {
                        _updateSuccess.value = Event("Status Updated Successfully")
                    }
                    .onFailure { exception ->
                        _errorEvent.value = Event(
                            ErrorEvent(
                                message = exception.message
                                    ?: "Failed to update building visit status!"
                            )
                        )
                    }
            }
        }
    }
}

/**
 * ViewModelFactory for BuildingDetailViewModel.
 * Uses SavedStateHandle to access navigation arguments.
 */
class BuildingDetailViewModelFactory(
    private val repository: BuildingRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if (modelClass.isAssignableFrom(BuildingDetailViewModel::class.java)) {
            val savedStateHandle = extras.createSavedStateHandle()
            return BuildingDetailViewModel(repository, savedStateHandle) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
