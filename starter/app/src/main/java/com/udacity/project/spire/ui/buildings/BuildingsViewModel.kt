package com.udacity.project.spire.ui.buildings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.udacity.project.spire.data.repository.BuildingRepository
import com.udacity.project.spire.domain.model.Building
import com.udacity.project.spire.ui.common.ErrorEvent
import com.udacity.project.spire.ui.common.Event
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * ViewModel for BuildingsFragment.
 * Manages paginated building data using Paging3.
 *
 * TODO #38: Implement BuildingsViewModel
 *
 * This ViewModel exposes:
 * 1. A Flow of PagingData for the buildings list
 * 2. Error events for showing error messages to user
 * 3. A refresh method for pull-to-refresh
 *
 * KEY CONCEPTS:
 * - Flow<PagingData<T>>: Used with PagingDataAdapter for efficient pagination
 * - cachedIn(viewModelScope): Prevents reloading data on configuration changes
 * - Event wrapper: Ensures error messages are shown only once
 * - viewModelScope: Coroutine scope tied to ViewModel lifecycle
 */
class BuildingsViewModel(
    private val repository: BuildingRepository
) : ViewModel() {

    /**
     * TODO #38a: Initialize buildings property
     *
     * HINTS:
     * - Call repository.getBuildings() to get Flow<PagingData<Building>>
     * - Use .cachedIn(viewModelScope) to prevent reloading on configuration changes
     * - Fragment will collect this Flow using lifecycleScope
     */
    val buildings: Flow<PagingData<Building>>
        get() = repository.getBuildings().cachedIn(viewModelScope)

    // Error state exposed to UI
    private val _errorEvent = MutableLiveData<Event<ErrorEvent>>()
    val errorEvent: LiveData<Event<ErrorEvent>> = _errorEvent

    /**
     * TODO #38b: Implement refresh() method
     *
     * Called when user swipes to refresh the list.
     *
     * HINTS:
     * - Use viewModelScope.launch to start a coroutine
     * - Call repository.refreshBuildings() which returns Result<Unit>
     * - Use .onFailure() to handle errors and update _errorEvent
     * - On success, Paging3's RemoteMediator automatically updates the list
     */
    fun refresh() {
        //TODO("Implement refresh() - see TODO comment above")
        viewModelScope.launch {
            repository.refreshBuildings().onFailure { exception ->
                _errorEvent.value = Event(
                    ErrorEvent(message = exception.message ?: "Failed to refresh buildings")
                )
            }
        }
    }
}

/**
 * ViewModelFactory for BuildingsViewModel.
 * Provides repository dependency to ViewModel.
 */
class BuildingsViewModelFactory(
    private val repository: BuildingRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BuildingsViewModel::class.java)) {
            return BuildingsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
