package com.example.android.ui.theme.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.android.R
import com.example.compose.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onNavigateToSetting: () -> Unit, onNavigateToProfile: () -> Unit, onNavigateToVoteElection: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "eVote",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 2.sp,
                        modifier = Modifier.padding( start = 8.dp)
                    ) },
                actions = {
                    IconButton(onClick = { onNavigateToProfile() }) {
                        Icon(Icons.Default.Person, contentDescription = "Profile")
                    }
                    IconButton(onClick = { onNavigateToSetting() }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .background(MaterialTheme.colorScheme.surfaceContainerLowest),
            //verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            //Spacer(Modifier.height(100.dp))
            Image(
                painter = painterResource(id = R.drawable.voting), // Your logo image
                contentDescription = "eVote Logo",
                modifier = Modifier.size(130.dp)
            )
            Spacer(Modifier.height(20.dp))
            Text(
                "Започнете своя вот",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(20.dp))
            Button(
                onClick = {
                    onNavigateToVoteElection() },
                modifier = Modifier
                    .width(220.dp)
                    .padding(vertical = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(
                    "ГЛАСУВАНЕ",
                    letterSpacing = 4.sp,
                    fontSize = 24.sp
                )
            }
            Spacer(Modifier.height(30.dp))
            Text(
                "Внимание! След натискане\nна бутона не напускайте\nприложението до" +
                        " завършване\nна гласъването, впротивен\nслучай вотът щебъде\nневалиден",
                fontSize = 18.sp,
                modifier = Modifier.width(320.dp),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 20.sp
            )

//            ElectionCard(
//                title = "Presidential Election",
//                description = "Vote for the next president of the country.",
//                // Pass a lambda that calls the main navigation lambda with a specific ID
//                onClick = { onNavigateToVoteElection("presidential") }
//            )
//            ElectionCard(
//                title = "Local Government Election",
//                description = "Choose your local representatives.",
//                onClick = { onNavigateToVoteElection("local") }
//            )
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
    AppTheme {
        HomeScreen(
            onNavigateToSetting = {},
            onNavigateToProfile = {},
            onNavigateToVoteElection = {}
        )
    }

}
