package com.nexis.edgesync.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.nexis.edgesync.ui.DashboardScreen
import com.nexis.edgesync.ui.DevicesScreen
import com.nexis.edgesync.ui.HistoryScreen
import com.nexis.edgesync.ui.ProtocolsScreen
import com.nexis.edgesync.viewmodel.EdgeSyncViewModel
import com.nexis.edgesync.viewmodel.HistoryViewModel

@Composable
fun MainScreen(
    viewModel: EdgeSyncViewModel = viewModel(),
    historyViewModel: HistoryViewModel = viewModel()
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar {
                navItems.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        label = { Text(item.title) },
                        selected = currentRoute == item.route,
                        onClick = {
                            if (currentRoute != item.route) {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NavItem.Dashboard.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(NavItem.Dashboard.route) {
                DashboardScreen(viewModel = viewModel)
            }
            composable(NavItem.Devices.route) {
                DevicesScreen(viewModel = viewModel)
            }
            composable(NavItem.Protocols.route) {
                ProtocolsScreen()
            }
            composable(NavItem.History.route) {
                HistoryScreen(viewModel = historyViewModel)
            }
        }
    }
}
