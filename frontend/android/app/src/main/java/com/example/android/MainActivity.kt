package com.example.android

import ElectionChoiceScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.android.dummymodel.UserProfile
import com.example.android.ui.theme.screens.HomeScreen
import com.example.android.ui.theme.screens.LoginScreen
import com.example.android.ui.theme.screens.ProfileScreen
import com.example.android.ui.theme.screens.RegisterScreen
import com.example.android.ui.theme.screens.SplashScreen
import com.example.compose.AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                AppNavigator()
            }

        }
    }
}

@Composable
fun AppNavigator() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") {
            SplashScreen(navController)
        }
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    // Navigate to home after successful login
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onNavigateToRegister = {
                    // Navigate to the register screen
                    navController.navigate("register")
                }
            )
        }
        composable("register") {
            RegisterScreen(onNavigateToLogin = { navController.popBackStack() })
        }
        composable("home") {
            HomeScreen(
                onNavigateToSetting = { navController.navigate("settings") },
                onNavigateToProfile = { navController.navigate("profile") },
                onNavigateToVoteElection = {navController.navigate("choose_election")}
            )
        }
        composable ("choose_election"){
            ElectionChoiceScreen (
                onNavigateToVote = { electionName ->
                    navController.navigate("vote/$electionName")
                }
            )
        }
//        composable("settings") {
//            //SettingsScreen(onNavigateBack = { navController.popBackStack() })
//        }
        composable("profile") {
                val placeholderProfile: UserProfile? = UserProfile(
                fullName = "Иван Иванов (Пример)",
                egn = "8501011234",
                idCardNumber = "645123456",
                address = "гр. София, ул. Примерна 15",
                idCardFrontUrl = null,
                idCardBackUrl = null
            )
            ProfileScreen(
                userProfile = placeholderProfile,
                onNavigateBack = { navController.popBackStack()
                }
            )
        }
        /*composable(
            route = "vote/{electionId}",
            arguments = listOf(navArgument("electionId") { type = NavType.StringType })
        ) { backStackEntry ->
            val electionId = backStackEntry.arguments?.getString("electionId")
            VoteScreen(
                electionId = electionId ?: "Unknown", // Handle null case
                onNavigateBack = { navController.popBackStack() }
            ) // Example
        }*/
    }
}

