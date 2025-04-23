package com.example.android.ui.theme.screens

import android.annotation.SuppressLint
import android.util.Log
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
import com.example.compose.AppTheme
import com.example.android.dto.request.LoginRequest
import com.example.android.dto.response.LoginResponse
import com.example.android.api.VotingApi
import com.example.android.dto.response.UserProfileDetailsDTO
import com.example.android.utils.CurrentUserHolder
import com.example.android.utils.InMemoryTokenHolder

import kotlinx.coroutines.launch
import java.io.IOException

@SuppressLint("NewApi")
@Composable
fun LoginScreen(onLoginSuccess: () -> Unit, onNavigateToRegister: () -> Unit) {
    val context = LocalContext.current
    var egn by remember { mutableStateOf("") }
    var doc_id by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    fun fetchAndStoreUserProfile() {

        Log.d("AuthViewModel", "Attempting to fetch user profile details...")

        VotingApi.getUserProfileDetails(object : VotingApi.Callback<UserProfileDetailsDTO> {
            override fun onSuccess(response: UserProfileDetailsDTO) {
                Log.i("AuthViewModel", "Successfully fetched user profile details.")
                // 4. Store profile details globally
                CurrentUserHolder.updateProfile(response)
            }

            override fun onFailure(error: Throwable) {
                Log.e("AuthViewModel", "Failed to fetch user profile details after login", error)
                CurrentUserHolder.clear()
            }
        })
    }

    fun attemptLogin() {
        if (egn.isBlank() || doc_id.isBlank()) {
            error = "Моля, попълнете ЕГН и номер на документ."
            return
        }

        scope.launch {
            isLoading = true
            error = null

            try {
                val dto = LoginRequest(egn = egn, documentNumber = doc_id)
                val response: LoginResponse = VotingApi.loginSuspending(dto)
                InMemoryTokenHolder.saveToken(response.token)
                fetchAndStoreUserProfile()
                onLoginSuccess()

            } catch (e: Throwable) {
                println("Login failed!")
                println("Error Type: ${e::class.java.simpleName}")
                println("Error Message: ${e.message}")
                e.printStackTrace()

                error = when (e) {
                    is IOException -> "Грешка в мрежата. Моля, проверете връзката си."
                    is kotlinx.serialization.SerializationException -> "Грешка при обработка на отговора от сървъра."
                    else -> e.message ?: "Възникна грешка. Моля, опитайте отново."
                }

            } finally {
                isLoading = false
            }
        }
    }




    LaunchedEffect(error) {
        error?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        }
    }


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
            onValueChange = { egn = it; if (error != null) error = null },
            label = { Text("ЕГН") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = TextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black),
            singleLine = true,
            isError = error != null
        )

        OutlinedTextField(
            value = doc_id,
            onValueChange = { doc_id = it; if (error != null) error = null },
            label = { Text("Номер на лична карта") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = TextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black),
            singleLine = true,
            isError = error != null
        )

        Box(contentAlignment = Alignment.Center) {
            Button(
                onClick = { attemptLogin() },
                enabled = !isLoading,
                modifier = Modifier
                    .width(220.dp)
                    .height(60.dp)
                    .padding(vertical = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                if (!isLoading) {
                    Text(
                        "ВЛЕЗ",
                        letterSpacing = 3.sp,
                        fontSize = 20.sp
                    )
                }
            }
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
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
