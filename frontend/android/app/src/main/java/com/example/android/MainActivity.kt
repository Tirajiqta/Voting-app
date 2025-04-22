package com.example.android

import ElectionChoiceScreen
import ResultsScreen
import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen
import androidx.navigation.NavType

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.android.dummymodel.UserProfile
import com.example.android.ui.theme.screens.HomeScreen
import com.example.android.ui.theme.screens.LoginScreen
import com.example.android.ui.theme.screens.ProfileScreen
import com.example.android.ui.theme.screens.RegisterScreen
import com.example.android.ui.theme.screens.SplashScreen
import com.example.android.ui.theme.screens.VotingScreen
import com.example.android.ui.theme.screens.VotePreviewScreen
import com.example.android.utils.CurrentUserHolder
import com.example.compose.AppTheme

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                AppNavigator()
                createNotificationChannels()
            }

        }

    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "election_alert"
            val channelName = "Elections Alert"
            val channelDescription = "Notification about upcoming elections"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val electionChannel = NotificationChannel(channelId, channelName, importance).apply {
                description = channelDescription
            }
            val notificationManager: NotificationManager =
               getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(electionChannel)
        }
    }
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ){ isGranted: Boolean ->
        if (isGranted) {
            Log.i("PERMISSION", "POST_NOTIFICATIONS permission granted after request.")
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                sendNotification()

            }
        } else {
            Log.w("PERMISSION", "POST_NOTIFICATIONS permission denied.")
        }
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun requestPermissionAndShowNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // Permission already granted
                    Log.i("PERMISSION", "POST_NOTIFICATIONS permission already granted.")
                    sendNotification()
                }
                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    Log.w("PERMISSION", "Showing rationale for POST_NOTIFICATIONS (or requesting directly).")
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
                else -> {
                    Log.i("PERMISSION", "Requesting POST_NOTIFICATIONS permission.")
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            sendNotification()
        }
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun sendNotification() {
        val context: Context = this

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("notification_action", "simulated_launch")
        }
        val pendingIntentFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, pendingIntentFlag)

        val notificationTitle = "Симулация: Избори Наближават!"
        val notificationBody = "Това е тестово известие. Изборите 'Демо Избори 2024' започват скоро."

        val ELECTION_CHANNEL_ID = ""
        val builder = NotificationCompat.Builder(context, "election_alert")
            .setSmallIcon(R.drawable.elections) // Use your icon resource
            .setContentTitle(notificationTitle)
            .setContentText(notificationBody)
            .setPriority(NotificationCompat.PRIORITY_HIGH) // Make it noticeable
            .setContentIntent(pendingIntent) // Intent to launch on tap
            .setAutoCancel(true) // Dismiss notification on tap

        val notificationManager = NotificationManagerCompat.from(context)

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED &&
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Log.w("NOTIFICATION_SIM", "Cannot send notification - POST_NOTIFICATIONS permission not granted.")
            return // Exit if permission not granted on Android 13+
        }

        notificationManager.notify(1, builder.build())
        Log.i("NOTIFICATION_SIM", "Simulated notification sent.")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
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
                onNavigateToVoteElection = {navController.navigate("choose_election")},
                onNavigateToResults = {
                    val electionIdForResults = 1L
                    navController.navigate("results/$electionIdForResults")
                }
            )
        }
        composable(
            route = "results/{electionId}", // Matches "results/1", "results/2", etc.
            arguments = listOf(navArgument("electionId") { type = NavType.LongType })
        ) { backStackEntry ->
            val electionIdArg = backStackEntry.arguments?.getLong("electionId")
            if (electionIdArg != null && electionIdArg > 0L) {
                ResultsScreen(
                    electionId = electionIdArg,
                    onNavigateBack = { navController.popBackStack() }
                )
            } else {
                Text("Грешка: Невалиден ID на избор за резултати.")
                LaunchedEffect(Unit){ navController.popBackStack() }
            }
        }
        composable("choose_election") {
            ElectionChoiceScreen (
                onNavigateToVote = { selectedIds -> // Receives List<Long>
                    if (selectedIds.isNotEmpty()) {
                        val firstId = selectedIds.first()
                        navController.navigate("vote/$firstId")
                    }
                }
            )
        }
        composable(
            route = "vote/{electionId}",
            arguments = listOf(navArgument("electionId") {
                type = NavType.LongType
            })
        ) { backStackEntry ->
            val electionId = backStackEntry.arguments?.getLong("electionId")
            // TODO: Get the actual userId from your authentication state or wherever it's stored
            val currentUserId =
                CurrentUserHolder.getCurrentProfile()?.user?.id ?: 1

            if (electionId != null) {
                VotingScreen(
                    navController = navController,
                    electionId = electionId,
                    userId = currentUserId
                )
            } else {
                Text("Грешка: Невалиден ID на избор.")
                LaunchedEffect (Unit) { navController.popBackStack() }
            }
        }



        composable("profile") {
                val user = CurrentUserHolder.getCurrentProfile()?.user
                val placeholderProfile = UserProfile(
                fullName = user?.name ?: "",
                egn = user?.egn ?: "",
                idCardNumber = user?.document?.number ?: "",
                address = user?.currentAddress ?: "",
                idCardFrontUrl = null,
                idCardBackUrl = null
            )
            ProfileScreen(
                userProfile = placeholderProfile,
                onNavigateBack = { navController.popBackStack()
                }
            )
        }
        @Composable
        fun ResultsScreenPlaceholder(onNavigateBack: () -> Unit) {
            Scaffold(
                topBar = {
                    TopAppBar(title = { Text("Резултати (Placeholder)") },
                        navigationIcon = {
                            IconButton (onClick = onNavigateBack) {
                                Icon(Icons.Filled.ArrowBack, contentDescription = "Назад")
                            }
                        })
                }
            ) { padding ->
                Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                }
            }
        }
    }

}

