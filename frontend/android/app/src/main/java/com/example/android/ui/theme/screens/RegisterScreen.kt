package com.example.android.ui.theme.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun RegisterScreen(onNavigateToLogin: () -> Unit) {
    val context = LocalContext.current
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var egn by remember { mutableStateOf("") }
    var doc_id by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Регистрация", style = MaterialTheme.typography.headlineLarge)

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Три имена") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            singleLine = true
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Имейл") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            singleLine = true
        )

        OutlinedTextField(
            value = egn,
            onValueChange = { egn = it },
            label = { Text("ЕГН") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            singleLine = true
        )

        OutlinedTextField(
            value = doc_id,
            onValueChange = { doc_id = it },
            label = { Text("Номер на лична карта") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            singleLine = true
        )

        Button(
            onClick = {
                Toast.makeText(context, "Регистриране...", Toast.LENGTH_SHORT).show()
                      onNavigateToLogin},
            modifier = Modifier.width(200.dp).padding(vertical = 8.dp)
        ) {
            Text("Регистрация")
        }

        TextButton(onClick = onNavigateToLogin) {
            Text("Имаш профил? Влез тук.")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    RegisterScreen {}
}
