package com.udacity.project.spire

import android.app.Application
import com.udacity.project.spire.data.local.database.SpireDatabase
import com.udacity.project.spire.data.remote.api.ApiServiceProvider
import com.udacity.project.spire.data.repository.BuildingRepository
import com.udacity.project.spire.data.repository.DefaultBuildingRepository
import com.udacity.project.spire.data.repository.MockBuildingRepository

class SpireApplication : Application() {

    lateinit var buildingRepository: BuildingRepository
        private set

    override fun onCreate() {
        super.onCreate()
        initializeRepository()
    }

    private fun initializeRepository() {
        // TODO #47: Replace MockBuildingRepository with DefaultBuildingRepository
        // Complete this AFTER implementing TODOs #29-37 (Repository methods)
        //
        // Steps:
        // 1. Get database instance: SpireDatabase.getInstance(this)
        // 2. Get API service: ApiServiceProvider.getApiService()
        // 3. Create DefaultBuildingRepository(database, apiService)
        // 4. Assign to buildingRepository property
        //
        // Expected implementation:
        // val database = SpireDatabase.getInstance(this)
        // val apiService = ApiServiceProvider.getApiService()
        // buildingRepository = DefaultBuildingRepository(database, apiService)

        // Temporary mock for app to run while implementing:
       // buildingRepository = MockBuildingRepository()
        val database = SpireDatabase.getInstance(this)
        val apiService = ApiServiceProvider.apiService
        buildingRepository = DefaultBuildingRepository(database,apiService)
    }
}