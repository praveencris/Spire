package com.udacity.project.spire.ui.visits

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import com.udacity.project.spire.data.repository.BuildingRepository
import com.udacity.project.spire.domain.model.Building
import com.udacity.project.spire.domain.model.VisitStatus
import com.udacity.project.spire.ui.common.ErrorEvent
import com.udacity.project.spire.ui.common.Event
import kotlinx.coroutines.flow.catch

/**
 * ViewModel for MyVisitsFragment.
 * Filters buildings by visit status (Visited, Bucket List, Not Visited).
 *
 * TODO #40: Implement MyVisitsViewModel
 *
 * This ViewModel:
 * 1. Maintains current filter status (which chip is selected)
 * 2. Provides filtered building list based on status
 * 3. Handles errors during data loading
 *
 * KEY CONCEPTS:
 * - switchMap: Transform one LiveData based on another LiveData's value
 * - Flow.catch(): Handle errors in Flow stream
 * - Flow.asLiveData(): Convert Flow to LiveData
 * - Reactive filtering: List updates automatically when status changes
 */
class MyVisitsViewModel(
    private val repository: BuildingRepository
) : ViewModel() {

    /**
     * Current filter status (default: VISITED).
     * When this changes, buildings LiveData automatically updates.
     */
    private val _currentStatus = MutableLiveData(VisitStatus.VISITED)
    val currentStatus: LiveData<VisitStatus> = _currentStatus

    // Error state exposed to UI
    private val _errorEvent = MutableLiveData<Event<ErrorEvent>>()
    val errorEvent: LiveData<Event<ErrorEvent>> = _errorEvent

    /**
     * TODO #40a: Initialize buildings property with switchMap
     *
     * Creates a reactive filter - when currentStatus changes,
     * buildings automatically updates with new filtered list.
     *
     * HINTS:
     * - Use _currentStatus.switchMap { status -> ... }
     * - Call repository.getBuildingsByVisitStatus(status)
     * - Use .catch() to handle errors and emit empty list
     * - Use .asLiveData() to convert Flow to LiveData
     * - Each time _currentStatus changes, the filter re-executes automatically
     */
    val buildings: LiveData<List<Building>>
        get() = _currentStatus.switchMap { status ->
            repository.getBuildingsByVisitStatus(status)
                .catch { exception ->
                    _errorEvent.postValue(
                        Event(
                            ErrorEvent(
                                message = exception.message ?: "Failed to load buildings"
                            )
                        )
                    )
                }
                .asLiveData()
        }

    /**
     * TODO #40b: Implement setFilterStatus() method
     *
     * Called when user clicks a chip (Visited, Bucket List, Not Visited).
     *
     * HINTS:
     * - Check if status changed before updating (avoid unnecessary updates)
     * - Set _currentStatus.value to new status
     * - This triggers switchMap above, which updates buildings LiveData
     */
    fun setFilterStatus(status: VisitStatus) {
        if (_currentStatus.value != status) {
            _currentStatus.value = status
        }
    }
}

/**
 * ViewModelFactory for MyVisitsViewModel.
 */
class MyVisitsViewModelFactory(
    private val repository: BuildingRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MyVisitsViewModel::class.java)) {
            return MyVisitsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
