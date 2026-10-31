package com.aiagent.assistant.presentation.navigation

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController

/** নিচের বারের ট্যাব বদলানো: back stack জমে না, আগের অবস্থা ফিরে আসে */
fun NavHostController.navigateTopLevel(route: String) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

/** যেকোনো route-এ যাওয়া: ট্যাব হলে navigateTopLevel, না হলে সাধারণ navigate */
fun NavHostController.navigateTo(route: String) {
    if (Screen.bottomNavItems.any { it.route == route }) {
        navigateTopLevel(route)
    } else {
        navigate(route) { launchSingleTop = true }
    }
}
