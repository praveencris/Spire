package com.udacity.project.spire.ui.statistics

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.udacity.project.spire.data.repository.BuildingRepository
import com.udacity.project.spire.domain.model.BuildingStatistics
import com.udacity.project.spire.ui.common.ErrorEvent
import com.udacity.project.spire.ui.common.Event
import kotlinx.coroutines.launch

/**
 * ViewModel for StatisticsFragment.
 * Loads and displays aggregated building statistics.
 *
 * TODO #42: Implement StatisticsViewModel
 *
 * This ViewModel:
 * 1. Loads statistics from repository on init
 * 2. Exposes loading state for progress indicator
 * 3. Handles errors with default statistics
 * 4. Provides retry functionality
 *
 * KEY CONCEPTS:
 * - init block: Code that runs when ViewModel is created
 * - viewModelScope.launch: Coroutine tied to ViewModel lifecycle
 * - try-catch-finally: Error handling with cleanup
 * - Default values: Graceful degradation on error
 */
class StatisticsViewModel(
    private val repository: BuildingRepository
) : ViewModel() {

    private val _statistics = MutableLiveData<BuildingStatistics>()
    val statistics: LiveData<BuildingStatistics> = _statistics

    // Error state exposed to UI
    private val _errorEvent = MutableLiveData<Event<ErrorEvent>>()
    val errorEvent: LiveData<Event<ErrorEvent>> = _errorEvent

    // Loading state
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    /**
     * TODO #42a: Add init block to load statistics on creation
     *
     * HINT: Add init { loadStatistics() } - runs when ViewModel is created
     */
    // Add the init block here (see TODO #42a above)
    init {
        loadStatistics()
    }

    /**
     * TODO #42b: Implement loadStatistics() method
     *
     * Loads statistics from repository with loading state and error handling.
     *
     * STEPS:
     * 1. Use viewModelScope.launch to start a coroutine
     * 2. Set _isLoading.value = true
     * 3. Use try-catch-finally for error handling
     * 4. Call repository.getStatistics() and set _statistics.value
     * 5. On error: Set _errorEvent and default statistics (all zeros)
     * 6. In finally: Set _isLoading.value = false
     *
     * HINTS:
     * - Default statistics prevent blank screen on error
     * - Fragment can call this method for retry functionality
     */
    fun loadStatistics() {
        //TODO("Implement loadStatistics() - see TODO comment above")
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _statistics.value = repository.getStatistics()
            } catch (e: Exception) {
                _errorEvent.value =
                    Event(ErrorEvent(message = e.message ?: "Failed to get Statistics!"))
                _statistics.value = BuildingStatistics(
                    totalBuildings = 0,
                    visitedCount = 0,
                    bucketListCount = 0,
                    totalMetersClimbed = 0,
                    countriesExplored = 0
                )
            } finally {
                _isLoading.value = false
            }
        }
    }
}

/**
 * ViewModelFactory for StatisticsViewModel.
 */
class StatisticsViewModelFactory(
    private val repository: BuildingRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StatisticsViewModel::class.java)) {
            return StatisticsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
