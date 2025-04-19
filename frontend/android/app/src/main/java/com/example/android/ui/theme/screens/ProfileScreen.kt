package com.example.android.ui.theme.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.AddCircle // Placeholder icon
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage // Import Coil's AsyncImage
import coil.request.ImageRequest
import androidx.compose.foundation.clickable
import com.example.android.dummymodel.UserProfile
import com.example.compose.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userProfile: UserProfile?, // Make nullable in case data isn't loaded yet
    onNavigateBack: () -> Unit,
    // Add a callback if you want to trigger an upload action from here
    // onUploadImages: (frontUri: Uri?, backUri: Uri?) -> Unit
) {
    // --- State for selected image URIs ---
    // Initialize with existing URLs if available, otherwise null
    // Note: We store newly selected URIs. Existing URLs are just for display.
    var frontImageUri by remember { mutableStateOf<Uri?>(null) }
    var backImageUri by remember { mutableStateOf<Uri?>(null) }

    // --- Activity Result Launchers for Image Selection ---
    val frontImagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        frontImageUri = uri // Update state with the selected front image URI
    }

    val backImagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        backImageUri = uri // Update state with the selected back image URI
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Профил",
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 2.sp,) }, // "Profile"
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
    ) { paddingValues ->
        if (userProfile == null) {
            // Show loading indicator or error message if profile is null
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
                // Or Text("Неуспешно зареждане на профила.")
            }
        } else {
            // Display profile content
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                // --- Profile Details Section ---
                item {
                    Text(
                        "Лични данни", // "Personal Data"
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    ProfileInfoRow(label = "Име:", value = userProfile.fullName)
                    ProfileInfoRow(label = "ЕГН:", value = userProfile.egn)
                    ProfileInfoRow(label = "ЛК номер:", value = userProfile.idCardNumber)
                    ProfileInfoRow(label = "Адрес:", value = userProfile.address)
                    Divider(modifier = Modifier.padding(vertical = 16.dp))
                }

                // --- ID Card Upload Section ---
                item {
                    Text(
                        "Лична карта", // "ID Card"
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp) // Space between boxes
                    ) {
                        // Front Side Box
                        ImageSelectorBox(
                            modifier = Modifier.weight(1f),
                            label = "Предна страна", // "Front Side"
                            // Display newly selected URI first, fallback to existing URL
                            imageUri = frontImageUri,
                            existingImageUrl = userProfile.idCardFrontUrl,
                            onSelectClick = { frontImagePickerLauncher.launch("image/*") }
                        )
                        // Back Side Box
                        ImageSelectorBox(
                            modifier = Modifier.weight(1f),
                            label = "Задна страна", // "Back Side"
                            imageUri = backImageUri,
                            existingImageUrl = userProfile.idCardBackUrl,
                            onSelectClick = { backImagePickerLauncher.launch("image/*") }
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    // Optional: Add an "Upload/Save" button here if needed
                    // Button(onClick = { /* onUploadImages(frontImageUri, backImageUri) */ }) {
                    //    Text("Запази снимките")
                    // }
                }
            }
        }
    }
}

// Helper Composable for displaying Label: Value rows
@Composable
fun ProfileInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.width(110.dp) // Fixed width for labels
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

// Helper Composable for the Image Selection Box
@Composable
fun ImageSelectorBox(
    modifier: Modifier = Modifier,
    label: String,
    imageUri: Uri?, // Newly selected URI
    existingImageUrl: String?, // URL from backend (optional)
    onSelectClick: () -> Unit
) {
    val displayUri: Any? = imageUri ?: existingImageUrl // Prioritize newly selected URI

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(label, style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(bottom = 8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.6f) // Approximate aspect ratio for ID card display
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                .clickable(onClick = onSelectClick), // Allow clicking box to select
            contentAlignment = Alignment.Center
        ) {
            if (displayUri != null) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(displayUri) // Can load URI or URL string
                        .crossfade(true)
                        .build(),
                    contentDescription = label,
                    contentScale = ContentScale.Crop, // Fill the box
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                // Placeholder
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Outlined.AddCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        "Избери снимка", // "Select photo"
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        // Button is optional if the box itself is clickable
        // Button(onClick = onSelectClick, modifier = Modifier.fillMaxWidth()) {
        //     Text("Избери...") // "Select..."
        // }
    }
}


// --- Preview ---
@Preview(showBackground = true, widthDp = 380)
@Composable
fun ProfileScreenPreview() {
    val sampleProfile = UserProfile(
        fullName = "Иван Иванов",
        egn = "8501011234",
        idCardNumber = "645123456",
        address = "гр. София, ул. Примерна 15, ет. 3, ап. 10",
        idCardFrontUrl = null, // Simulate no existing image
        idCardBackUrl = null
    )
    AppTheme {
        ProfileScreen(
            userProfile = sampleProfile,
            onNavigateBack = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 380, name = "Profile Screen Loading")
@Composable
fun ProfileScreenLoadingPreview() {
    AppTheme {
        ProfileScreen(
            userProfile = null, // Simulate loading state
            onNavigateBack = {}
        )
    }
}