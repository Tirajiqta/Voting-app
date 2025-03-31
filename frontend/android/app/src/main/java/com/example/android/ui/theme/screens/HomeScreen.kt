package com.example.android.ui.theme.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onNavigateToSetting: () -> Unit,onNavigateToProfile: () -> Unit,onNavigateToVoteElection: (electionId: String) -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("eVote - Elections") },
                actions = {
                    IconButton(onClick = { onNavigateToProfile() }) {
                        Icon(Icons.Default.Person, contentDescription = "Profile")
                    }
                    IconButton(onClick = { onNavigateToSetting() }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Current Elections",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            ElectionCard(
                title = "Presidential Election",
                description = "Vote for the next president of the country.",
                // Pass a lambda that calls the main navigation lambda with a specific ID
                onClick = { onNavigateToVoteElection("presidential") }
            )
            ElectionCard(
                title = "Local Government Election",
                description = "Choose your local representatives.",
                onClick = { onNavigateToVoteElection("local") }
            )
        }
    }
}

@Composable
fun ElectionCard(
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 4.dp), // Adjusted padding slightly
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(description, fontSize = 16.sp)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreen() {
    HomeScreen(
        onNavigateToSetting = {},
        onNavigateToProfile = {},
        onNavigateToVoteElection = {}
    )
}
