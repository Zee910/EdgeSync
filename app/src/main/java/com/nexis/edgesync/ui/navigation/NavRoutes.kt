package com.nexis.edgesync.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Speed
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavItem(val route: String, val title: String, val icon: ImageVector) {
    object Dashboard : NavItem("dashboard", "Benchmark", Icons.Default.Speed)
    object Devices : NavItem("devices", "Devices", Icons.Default.Devices)
    object Protocols : NavItem("protocols", "Protocols", Icons.Default.Info)
    object History : NavItem("history", "History", Icons.Default.History)
}

val navItems = listOf(
    NavItem.Dashboard,
    NavItem.Devices,
    NavItem.Protocols,
    NavItem.History
)
