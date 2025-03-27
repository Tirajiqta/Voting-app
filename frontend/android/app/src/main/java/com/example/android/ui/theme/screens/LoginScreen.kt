package com.example.android.ui.theme.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.android.ui.theme.AndroidTheme

@Composable
fun LoginScreen(onNavigateToRegister: () -> Unit) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Login",
            style = MaterialTheme.typography.headlineLarge
            //color = MaterialTheme.colorScheme.tertiary
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        )

        Button(
            onClick = { Toast.makeText(context, "Logging in...", Toast.LENGTH_SHORT).show() },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF9BC2E8),  // Background color
                contentColor = MaterialTheme.colorScheme.onPrimary   // Text/icon color
            )
        ) {
            Text("Login")
        }

        TextButton(onClick = onNavigateToRegister) {
            Text("Don't have an account? Register here")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen {}
}
