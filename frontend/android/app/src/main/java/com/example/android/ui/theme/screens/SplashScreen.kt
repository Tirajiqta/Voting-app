package com.example.android.ui.theme.screens

import android.os.Handler
import android.os.Looper
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay
import com.example.android.R

@Composable
fun SplashScreen(navController: NavController) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isVisible = true
        delay(3000) // Wait for 3 seconds
        navController.navigate("login") {
            popUpTo("splash") { inclusive = true } // Removes Splash from Back Stack
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedVisibility(visible = isVisible) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(id = R.drawable.elections), // Your logo image
                    contentDescription = "eVote Logo",
                    modifier = Modifier.size(130.dp)
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "eVote",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(50.dp))

                LinearProgressIndicator(
                    modifier = Modifier.height(8.dp).clip(RoundedCornerShape(8.dp)),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.primaryContainer
                )


            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    //SplashScreen(rememberNavController())

}
