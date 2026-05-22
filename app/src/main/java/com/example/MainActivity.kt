package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.TaskViewModel
import com.example.ui.TaskViewModelFactory
import com.example.ui.screens.AddProjectScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.TasksScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Initialize modern reactive task view model
            val taskViewModel: TaskViewModel = viewModel(
                factory = TaskViewModelFactory(application)
            )
            val isDarkTheme by taskViewModel.isDarkTheme.collectAsState()

            MyApplicationTheme(darkTheme = isDarkTheme) {
                TaskioAppNavigation(viewModel = taskViewModel)
            }
        }
    }
}

@Composable
fun TaskioAppNavigation(viewModel: TaskViewModel) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "onboarding",
        modifier = Modifier.fillMaxSize()
    ) {
        // 1. Splash / Intro Onboarding Screen
        composable("onboarding") {
            OnboardingScreen(
                onStartClicked = {
                    navController.navigate("home") {
                        popUpTo("onboarding") { inclusive = true }
                    }
                }
            )
        }

        // 2. Core Dashboard Screen
        composable("home") {
            HomeScreen(
                viewModel = viewModel,
                onNavigateToTasks = { navController.navigate("tasks") },
                onNavigateToAddProject = { navController.navigate("add_project") },
                onNavigateToProfile = { navController.navigate("profile") }
            )
        }

        // 3. Today's Tasks Screen (Includes Calendar picker and detail filters)
        composable("tasks") {
            TasksScreen(
                viewModel = viewModel,
                onBackClicked = { navController.popBackStack() }
            )
        }

        // 4. Add Project Screen
        composable("add_project") {
            AddProjectScreen(
                viewModel = viewModel,
                onBackClicked = { navController.popBackStack() }
            )
        }

        // 5. User Workspace Profile Screen
        composable("profile") {
            ProfileScreen(
                viewModel = viewModel,
                onNavigateToHome = {
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                    }
                },
                onNavigateToTasks = { navController.navigate("tasks") },
                onNavigateToAddProject = { navController.navigate("add_project") }
            )
        }
    }
}
