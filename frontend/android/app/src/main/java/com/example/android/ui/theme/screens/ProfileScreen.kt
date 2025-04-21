package com.example.android.ui.theme.screens

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.core.content.ContextCompat
import com.example.android.dummymodel.UserProfile
import com.example.android.services.CaptureSide
import com.example.android.services.IdCardData
import com.example.android.services.IdScanViewModel
import com.example.compose.AppTheme
import androidx.lifecycle.viewmodel.compose.viewModel


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
                    ProfileIdCardSection(
                        // Pass existing URLs from userProfile if they exist
                        existingFrontUrl = userProfile.idCardFrontUrl,
                        existingBackUrl = userProfile.idCardBackUrl
                        // ViewModel is handled internally by ProfileIdCardSection
                    )
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
    // Prioritize displaying the newly captured/selected URI
    val displayUriOrUrl: Any? = imageUri ?: existingImageUrl

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
                .clickable(onClick = onSelectClick), // Make the box itself clickable
            contentAlignment = Alignment.Center
        ) {
            if (displayUriOrUrl != null) {
                // Use Coil's AsyncImage to handle both Uri and URL String
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(displayUriOrUrl) // Handles URI or String URL
                        .crossfade(true)
                        .build(),
                    contentDescription = label,
                    contentScale = ContentScale.Crop, // Crop to fill the bounds
                    modifier = Modifier.fillMaxSize() // Image fills the Box
                )
            } else {
                // Placeholder content when no image is available
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Outlined.AddCircle,
                        contentDescription = null, // Decorative
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        "Натисни за снимка", // "Click for photo"
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }
            }
        }
        // The Button below is now redundant as the Box is clickable
        // Spacer(modifier = Modifier.height(8.dp))
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

@Composable
fun ProfileIdCardSection(
    existingFrontUrl: String?, // Added parameter
    existingBackUrl: String?,  // Added parameter
    viewModel: IdScanViewModel = viewModel() // Inject or provide ViewModel
) {
    val context = LocalContext.current // Used by AsyncImage implicitly

    // Observe state from the ViewModel
    val frontImageUri by viewModel.frontImageUri
    val backImageUri by viewModel.backImageUri
    val showDialog by viewModel.showDialog
    val parsedData by viewModel.parsedData
    val isProcessing by viewModel.isProcessing
    val errorMessage by viewModel.errorMessage

    // Remember the Snackbar host state
    val snackbarHostState = remember { SnackbarHostState() }

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasCameraPermission = granted
            Log.d("CameraDebug", "Permission result received: granted = $granted")
            if (!granted) {
                // Show a message if permission is denied permanently or temporarily
                viewModel.errorMessage.value = "Разрешението за камера е необходимо." // "Camera permission is required."
            }
        }
    )
    // --- End Permission Handling ---


    // Activity Result Launcher for taking a picture (remains the same)
    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            viewModel.handleCameraResult(success)
        }
    )

    // Function to trigger camera launch safely via ViewModel
    fun launchCamera(side: CaptureSide) {
        // <<< Log Function Entry >>>
        Log.d("CameraDebug", "launchCamera called for side: $side")
        // <<< Log Current Permission State >>>
        Log.d("CameraDebug", "Current hasCameraPermission state: $hasCameraPermission")

        if (hasCameraPermission) {
            // <<< Log Permission Granted Branch >>>
            Log.d("CameraDebug", "Permission check PASSED")
            viewModel.setCaptureSide(side)
            val uri = viewModel.createTempUri()
            // <<< Log URI Creation Result >>>
            Log.d("CameraDebug", "viewModel.createTempUri() result: $uri")
            if (uri != null) {
                // <<< Log Before Launching Camera >>>
                Log.d("CameraDebug", "URI is valid. Launching takePictureLauncher...")
                takePictureLauncher.launch(uri)
            } else {
                // <<< Log URI Failure >>>
                Log.e("CameraDebug", "URI creation FAILED!") // Use Log.e for errors
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                //viewModel.errorMessage.value = "Грешка: Не може да се подготви място за снимка."
            }
        } else {
            // <<< Log Permission Denied Branch >>>
            Log.d("CameraDebug", "Permission check FAILED. Launching permission request...")
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // Effect to request permission on first composition if needed (optional but good practice)
    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // --- UI Layout ---
    Column { // Main container for this section
        Text(
            "Лична карта", // "ID Card" section title
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Row containing the two image selector boxes
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp) // Adds space between the boxes
        ) {
            // Front Side Box
            ImageSelectorBox(
                modifier = Modifier.weight(1f), // Takes half the available width
                label = "Предна страна", // "Front Side"
                imageUri = frontImageUri, // Pass the Uri state from ViewModel
                // Pass existing URL if available, e.g., from userProfile object
                existingImageUrl = null, // Replace with userProfile.idCardFrontUrl if needed
                onSelectClick = {
                    Log.d("CameraDebug", "Front ImageSelectorBox clicked!")
                    launchCamera(CaptureSide.FRONT) } // Trigger camera for front side
            )

            // Back Side Box
            ImageSelectorBox(
                modifier = Modifier.weight(1f), // Takes the other half
                label = "Задна страна", // "Back Side"
                imageUri = backImageUri, // Pass the Uri state from ViewModel
                existingImageUrl = null, // Replace with userProfile.idCardBackUrl if needed
                onSelectClick = {
                    Log.d("CameraDebug", "Back ImageSelectorBox clicked!")
                    launchCamera(CaptureSide.BACK) } // Trigger camera for back side
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Loading Indicator displayed when ML Kit is processing
        if (isProcessing) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
                Text(
                    "Обработка...", // "Processing..."
                    modifier = Modifier.padding(top = 60.dp), // Position below indicator
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        // Snackbar Host for displaying errors
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.CenterHorizontally) // Center snackbar if desired
        )

        // Conditionally display the Confirmation Dialog
        if (showDialog && parsedData != null) {
            ConfirmationDialog(
                initialData = parsedData!!, // Pass the parsed data from VM (non-null asserted)
                onConfirm = { editedData ->
                    viewModel.onDialogConfirm(editedData) // Call VM function on confirm
                },
                onDismiss = {
                    viewModel.onDialogDismiss() // Call VM function on dismiss
                }
            )
        }
        // Optional: Add an overall save/upload button for the profile section if needed
        // Button(onClick = { /* Handle final save action */ }) { Text("Запази профила") }
    }
}

@Composable
fun ConfirmationDialog(
    initialData: IdCardData,
    onConfirm: (IdCardData) -> Unit,
    onDismiss: () -> Unit
) {
    // --- State for Editable Fields ---
    // Initialize state variables with data received from the OCR scan (initialData)
    // Use 'remember' so the state persists within the dialog's lifecycle and recompositions
    var editedEgn by remember { mutableStateOf(initialData.egn) }
    // Use the combined 'name' field from IdCardData for initial display if available,
    // otherwise, try constructing from parts if 'name' is empty but parts are not.
    var editedName by remember { mutableStateOf(
        initialData.name.ifBlank {
            listOfNotNull(initialData.givenName, initialData.fatherName, initialData.familyName)
                .filter { it.isNotBlank() }
                .joinToString(" ")
        }
    )}
    var editedDocNum by remember { mutableStateOf(initialData.documentNumber) }
    var editedDob by remember { mutableStateOf(initialData.dateOfBirth) }
    var editedExpiry by remember { mutableStateOf(initialData.expiryDate) }

    // State for name parts (allow editing individually)
    var editedGivenName by remember { mutableStateOf(initialData.givenName) }
    var editedFatherName by remember { mutableStateOf(initialData.fatherName) }
    var editedFamilyName by remember { mutableStateOf(initialData.familyName) }

    // State for other fields (back side, etc.)
    var editedPlaceOfBirth by remember { mutableStateOf(initialData.placeOfBirth) }
    var editedIssuingAuthority by remember { mutableStateOf(initialData.issuingAuthority) }
    var editedDateOfIssue by remember { mutableStateOf(initialData.dateOfIssue) }

    // --- Dialog UI ---
    AlertDialog(
        onDismissRequest = onDismiss, // Call the onDismiss lambda when user clicks outside or back button
        title = { Text("Потвърди данните", fontWeight = FontWeight.Bold) }, // "Confirm Data"
        text = {
            // Use a Column with verticalScroll to handle potentially long content
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {

                // --- Text Fields for Editing ---
                // Consider grouping fields logically (e.g., Name parts together)

                Text("Имена:", style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(bottom = 4.dp))
                OutlinedTextField(
                    value = editedGivenName,
                    onValueChange = { editedGivenName = it },
                    label = { Text("Име / Given Name(s)") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    singleLine = true // Usually single line
                )
                OutlinedTextField(
                    value = editedFatherName,
                    onValueChange = { editedFatherName = it },
                    label = { Text("Презиме / Father's Name") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    singleLine = true
                )
                OutlinedTextField(
                    value = editedFamilyName,
                    onValueChange = { editedFamilyName = it },
                    label = { Text("Фамилия / Surname") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    singleLine = true
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp)) // Use HorizontalDivider in Material 3

                OutlinedTextField(
                    value = editedEgn,
                    onValueChange = { editedEgn = it },
                    label = { Text("ЕГН") }, // "EGN"
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    singleLine = true
                )
                OutlinedTextField(
                    value = editedDocNum,
                    onValueChange = { editedDocNum = it },
                    label = { Text("№ на документ / Карта №") }, // "Document No / Card No"
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    singleLine = true
                )
                OutlinedTextField(
                    value = editedPlaceOfBirth,
                    onValueChange = { editedPlaceOfBirth = it },
                    label = { Text("Място на раждане") }, // "Place of Birth"
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                OutlinedTextField(
                    value = editedDob,
                    onValueChange = { editedDob = it },
                    label = { Text("Дата на раждане (ДД.ММ.ГГГГ)") }, // "Date of Birth"
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    singleLine = true
                )
                OutlinedTextField(
                    value = editedDateOfIssue,
                    onValueChange = { editedDateOfIssue = it },
                    label = { Text("Дата на издаване (ДД.ММ.ГГГГ)") }, // "Date of Issue"
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    singleLine = true
                )
                OutlinedTextField(
                    value = editedExpiry,
                    onValueChange = { editedExpiry = it },
                    label = { Text("Валидна до (ДД.ММ.ГГГГ)") }, // "Valid Until"
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    singleLine = true
                )
                OutlinedTextField(
                    value = editedIssuingAuthority,
                    onValueChange = { editedIssuingAuthority = it },
                    label = { Text("Издаден от / Орган") }, // "Issued by / Authority"
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    maxLines = 3 // Allow multiple lines for authority name
                )

                Spacer(modifier = Modifier.height(16.dp))

                // --- Instructions / Raw Text (Optional) ---
                Text(
                    "Моля, прегледайте и коригирайте извлечената информация.", // "Please review and correct..."
                    style = MaterialTheme.typography.bodySmall
                )

                // Optional: Show raw text for debugging - useful during development
                if (initialData.rawText.isNotBlank()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Суров текст от OCR:", fontWeight = FontWeight.Bold) // Raw OCR Text:
                    Text(
                        initialData.rawText,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.heightIn(max = 100.dp).verticalScroll(rememberScrollState()) // Limit height & scroll
                    )
                }
            }
        },
        // --- Dialog Action Buttons ---
        confirmButton = {
            Button(
                onClick = {
                    // Construct the final combined name from potentially edited parts
                    val finalConstructedName = listOfNotNull(editedGivenName, editedFatherName, editedFamilyName)
                        .filter { it.isNotBlank() }
                        .joinToString(" ")

                    // Create a new IdCardData object with the potentially edited values
                    val confirmedData = initialData.copy( // Use copy to maintain immutability of original state if needed
                        egn = editedEgn.trim(),
                        name = finalConstructedName, // Use the potentially edited combined name
                        documentNumber = editedDocNum.trim(),
                        dateOfBirth = editedDob.trim(),
                        expiryDate = editedExpiry.trim(),
                        // Assign edited name parts
                        givenName = editedGivenName.trim(),
                        fatherName = editedFatherName.trim(),
                        familyName = editedFamilyName.trim(),
                        // Assign other edited fields
                        placeOfBirth = editedPlaceOfBirth.trim(),
                        issuingAuthority = editedIssuingAuthority.trim(),
                        dateOfIssue = editedDateOfIssue.trim()
                        // Exclude rawText from the final confirmed data unless needed downstream
                    )
                    // Pass the confirmed data back via the callback
                    onConfirm(confirmedData)
                }
            ) {
                Text("Потвърди") // "Confirm"
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) { // Call the onDismiss lambda
                Text("Отказ") // "Cancel"
            }
        }
    )
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