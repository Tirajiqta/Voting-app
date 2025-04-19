package com.example.android.ui.theme.screens

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.android.dto.response.DocumentDTO
import com.example.android.dto.response.LocationResponseDTO
import com.example.android.dto.response.MunicipalityResponseDTO
import com.example.android.dto.response.RegionResponseDTO
import com.example.android.dto.response.RoleDTO
import com.example.android.dto.response.UserDTO
import com.example.compose.AppTheme
import java.time.LocalDate

@SuppressLint("NewApi")
@Composable
fun LoginScreen(onLoginSuccess: () -> Unit, onNavigateToRegister: () -> Unit) {
    val context = LocalContext.current
    var egn by remember { mutableStateOf("") }
    var doc_id by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "ВХОД",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 2.sp,
            fontSize = 30.sp
        )

        OutlinedTextField(
            value = egn,
            onValueChange = { egn = it },
            label = { Text("ЕГН") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            singleLine = true
        )

        OutlinedTextField(
            value = doc_id,
            onValueChange = { doc_id = it },
            label = { Text("Номер на лична карта") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            singleLine = true
        )
        /*
        Button(
            onClick = {
                Toast.makeText(context, "Влизане...", Toast.LENGTH_SHORT).show()

                val dto = UserDTO(
                    id = 12345L,
                    name = "Ivan Petrov",
                    email = "ivan.petrov1980@testmail.com",
                    phone = "+359887654321",
                    password = "Str0ngP@ssw0rd!",
                    currentAddress = "бул. Владимир Вазов 10, София, България",
                    locationId = LocationResponseDTO(
                        id = 42,
                        name = "Sofia",
                        municipality = MunicipalityResponseDTO(
                            id = 1,
                            name = "sofia",
                            population = 1234,
                            region = RegionResponseDTO(
                                id = 123,
                                population = 123455,
                                name = "sofia"
                            )
                        )
                    ),
                    egn = "8001010007",
                    document = DocumentDTO(
                        id = 987,
                        permanentAddress = "test",
                        validFrom = LocalDate.of(2018, 5, 20).toString(),
                        dateOfBirth = LocalDate.of(2000, 5, 20).toString(),
                        gender = 1,
                        issuer = "test",
                        number = "1",
                        validTo = LocalDate.of(2028, 5, 20).toString()
                    ),
                    roles = RoleDTO(
                        id = 2,
                        name = "ADMIN"
                    )
                )

                // Пример: изпращане на dto или логване
                println(dto)

                onLoginSuccess()
            },
            modifier = Modifier
                .width(220.dp)
                .padding(vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text(
                "ВЛЕЗ",
                letterSpacing = 3.sp,
                fontSize = 20.sp
            )
        }*/

        Button(
            onClick = {
                Toast.makeText(context, "Влизане...", Toast.LENGTH_SHORT).show() 
                onLoginSuccess()},
            modifier = Modifier
                .width(220.dp)
                .padding(vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,  // Background color
                contentColor = MaterialTheme.colorScheme.onPrimary   // Text/icon color
            )
        ) {
            Text(
                "ВЛЕЗ",
                letterSpacing = 3.sp,
                fontSize = 20.sp)
        }

//        TextButton(onClick = onNavigateToRegister) {
//            Text("Регистрирай се тук!")
//        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    AppTheme {
        LoginScreen(
            onLoginSuccess = {},
            onNavigateToRegister = {}
        )
    }
}
