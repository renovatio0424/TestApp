package com.herry.libs.app.nav

import androidx.navigation.fragment.NavHostFragment

class BottomNavHostFragment : NavHostFragment() {

    fun isNavScreenStartDestination(): Boolean {
        val currentDestination = navController.currentDestination ?: return false
        val parentDestination = currentDestination.parent ?: return false
        return currentDestination.id == parentDestination.startDestinationId
    }
}