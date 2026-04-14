package com.example.selftraining.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.selftraining.ui.exercise.ExerciseDetailScreen
import com.example.selftraining.ui.exercise.ExerciseListScreen
import com.example.selftraining.ui.exercise.ExerciseViewModel
import com.example.selftraining.ui.home.HomeScreen
import com.example.selftraining.ui.home.HomeViewModel
import com.example.selftraining.ui.plan.PlanScreen
import com.example.selftraining.ui.plan.PlanViewModel

/** ナビゲーションルート定義 */
sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Home : Screen("home", "ホーム", Icons.Default.Home)
    object ExerciseList : Screen("exercise_list", "種目一覧", Icons.Default.FitnessCenter)
    object ExerciseDetail : Screen("exercise_detail/{exerciseId}", "種目詳細", Icons.Default.FitnessCenter)
    object Plan : Screen("plan", "プラン管理", Icons.Default.List)
}

/** BottomNavigationBarに表示するタブ */
val bottomNavItems = listOf(Screen.Home, Screen.ExerciseList, Screen.Plan)

/**
 * アプリのナビゲーション全体
 * BottomNavigationBar + NavHost を組み合わせた3タブ構成
 */
@Composable
fun AppNavigation(
    homeViewModel: HomeViewModel,
    exerciseViewModel: ExerciseViewModel,
    planViewModel: PlanViewModel
) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            AppBottomNavigationBar(navController = navController)
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route
        ) {
            // ホーム画面
            composable(Screen.Home.route) {
                HomeScreen(viewModel = homeViewModel)
            }

            // 種目一覧画面
            composable(Screen.ExerciseList.route) {
                ExerciseListScreen(
                    viewModel = exerciseViewModel,
                    onExerciseClick = { exerciseId ->
                        navController.navigate("exercise_detail/$exerciseId")
                    }
                )
            }

            // 種目詳細画面
            composable(
                route = Screen.ExerciseDetail.route,
                arguments = listOf(
                    navArgument("exerciseId") { type = NavType.LongType }
                )
            ) { backStackEntry ->
                val exerciseId = backStackEntry.arguments?.getLong("exerciseId") ?: return@composable
                ExerciseDetailScreen(
                    exerciseId = exerciseId,
                    viewModel = exerciseViewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            // プラン管理画面
            composable(Screen.Plan.route) {
                PlanScreen(viewModel = planViewModel)
            }
        }
    }
}

/** BottomNavigationBar */
@Composable
private fun AppBottomNavigationBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar {
        bottomNavItems.forEach { screen ->
            NavigationBarItem(
                icon = {
                    Icon(imageVector = screen.icon, contentDescription = screen.label)
                },
                label = { Text(screen.label) },
                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}
