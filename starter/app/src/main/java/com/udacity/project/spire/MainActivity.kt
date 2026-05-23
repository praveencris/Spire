package com.udacity.project.spire

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.udacity.project.spire.databinding.ActivityMainBinding

/**
 * Main activity that hosts navigation and manages the app's UI.
 *
 * TODO #46: Implement Navigation Component setup
 *
 * This activity demonstrates:
 * 1. ViewBinding for type-safe view access
 * 2. Navigation Component for fragment navigation
 * 3. BottomNavigationView for tab navigation
 * 4. AppBarConfiguration for top-level destinations
 * 5. Dynamic UI updates based on navigation state
 *
 * KEY CONCEPTS:
 * - NavController: Manages fragment navigation
 * - AppBarConfiguration: Defines top-level destinations (no up button)
 * - BottomNavigationView: Tab bar for switching between top-level screens
 * - OnDestinationChangedListener: React to navigation events
 */
class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    /**
     * Top-level destinations (show bottom nav, no up button).
     * These are the main tabs: Buildings, Countries, My Visits, Statistics.
     */
    private val topLevelDestinations = setOf(
        R.id.buildingsFragment,
        R.id.myVisitsFragment,
        R.id.statisticsFragment
    )

    /**
     * TODO #46a: Implement navController property
     *
     * Get the NavController from NavHostFragment.
     *
     * HINTS:
     * - Use supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
     * - Cast to NavHostFragment
     * - Access navController property
     * - Use 'by lazy' for lazy initialization
     */
    private val navController: NavController by lazy {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navHostFragment.navController
        //TODO("Get NavController from NavHostFragment - see TODO comment above")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        /**
         * TODO #46b: Configure AppBarConfiguration and Navigation
         *
         * STEPS:
         * 1. Create AppBarConfiguration with topLevelDestinations
         * 2. Call setupActionBarWithNavController(navController, appBarConfiguration)
         * 3. Call binding.bottomNavigation.setupWithNavController(navController)
         * 4. Call setupBottomNavVisibility()
         *
         * HINTS:
         * - AppBarConfiguration defines which destinations show/hide the up button
         * - setupActionBarWithNavController links toolbar to navigation
         * - setupWithNavController links bottom nav to navigation (auto-selects tabs)
         */
        // Implement the steps above here
        appBarConfiguration = AppBarConfiguration(topLevelDestinations)
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.bottomNavigation.setupWithNavController(navController)
        setupBottomNavVisibility()
    }

    /**
     * TODO #46c: Implement setupBottomNavVisibility()
     *
     * Show/hide bottom navigation based on current destination.
     * Update toolbar title dynamically.
     *
     * STEPS:
     * 1. Add OnDestinationChangedListener to navController
     * 2. Check if destination.id is in topLevelDestinations
     * 3. Set binding.bottomNavigation.visibility (VISIBLE or GONE)
     * 4. Update binding.collapsingToolbar.title based on destination
     *
     * HINTS:
     * - Use when expression to map destination IDs to titles
     * - Bottom nav should only show on top-level destinations
     * - Detail screens should hide bottom nav
     */
    private fun setupBottomNavVisibility() {
        //TODO("Setup bottom navigation visibility - see TODO comment above")
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (topLevelDestinations.contains(destination.id)) {
                binding.bottomNavigation.visibility = View.VISIBLE
            } else {
                binding.bottomNavigation.visibility = View.GONE
            }
            val title = when (destination.id) {
                R.id.buildingsFragment ->
                    getString(R.string.nav_buildings)

                R.id.myVisitsFragment ->
                    getString(R.string.nav_my_visits)

                R.id.statisticsFragment ->
                    getString(R.string.nav_statistics)

                R.id.buildingDetailFragment ->
                    getString(R.string.nav_building_details)

                else ->
                    getString(R.string.app_name)
            }
            binding.collapsingToolbar.title = title
        }
    }

    /**
     * TODO #46d: Implement onSupportNavigateUp()
     *
     * Handle up button clicks in the toolbar.
     *
     * HINTS:
     * - Use findNavController(R.id.nav_host_fragment)
     * - Call navController.navigateUp(appBarConfiguration)
     * - Chain with || super.onSupportNavigateUp() as fallback
     */
    override fun onSupportNavigateUp(): Boolean {
        //TODO("Implement up navigation - see TODO comment above")
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}