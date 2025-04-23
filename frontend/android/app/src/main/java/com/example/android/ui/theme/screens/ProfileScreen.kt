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
import androidx.compose.material.icons.outlined.AddCircle
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
import coil.compose.AsyncImage
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
    userProfile: UserProfile?,
    onNavigateBack: () -> Unit,
) {
    var frontImageUri by remember { mutableStateOf<Uri?>(null) }
    var backImageUri by remember { mutableStateOf<Uri?>(null) }

    val frontImagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        frontImageUri = uri
    }

    val backImagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        backImageUri = uri
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Профил",
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 2.sp,) },
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
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                item {
                    Text(
                        "Лични данни",
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

                item {
                    ProfileIdCardSection(
                        existingFrontUrl = userProfile.idCardFrontUrl,
                        existingBackUrl = userProfile.idCardBackUrl
                    )
                }
            }
        }
    }
}

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
            modifier = Modifier.width(110.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Composable
fun ImageSelectorBox(
    modifier: Modifier = Modifier,
    label: String,
    imageUri: Uri?,
    existingImageUrl: String?,
    onSelectClick: () -> Unit
) {
    val displayUriOrUrl: Any? = imageUri ?: existingImageUrl

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(label, style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(bottom = 8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.6f)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                .clickable(onClick = onSelectClick),
            contentAlignment = Alignment.Center
        ) {
            if (displayUriOrUrl != null) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(displayUriOrUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = label,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Outlined.AddCircle,
                        contentDescription = null, // Decorative
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        "Натисни за снимка",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true, widthDp = 380)
@Composable
fun ProfileScreenPreview() {
    val sampleProfile = UserProfile(
        fullName = "Иван Иванов",
        egn = "8501011234",
        idCardNumber = "645123456",
        address = "гр. София, ул. Примерна 15, ет. 3, ап. 10",
        idCardFrontUrl = null,
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
    existingFrontUrl: String?,
    existingBackUrl: String?,
    viewModel: IdScanViewModel = viewModel()
) {
    val context = LocalContext.current

    val frontImageUri by viewModel.frontImageUri
    val backImageUri by viewModel.backImageUri
    val showDialog by viewModel.showDialog
    val parsedData by viewModel.parsedData
    val isProcessing by viewModel.isProcessing
    val errorMessage by viewModel.errorMessage

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
                viewModel.errorMessage.value = "Разрешението за камера е необходимо."
            }
        }
    )
    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            viewModel.handleCameraResult(success)
        }
    )

    fun launchCamera(side: CaptureSide) {
        Log.d("CameraDebug", "launchCamera called for side: $side")
        Log.d("CameraDebug", "Current hasCameraPermission state: $hasCameraPermission")

        if (hasCameraPermission) {
            Log.d("CameraDebug", "Permission check PASSED")
            viewModel.setCaptureSide(side)
            val uri = viewModel.createTempUri()
            Log.d("CameraDebug", "viewModel.createTempUri() result: $uri")
            if (uri != null) {
                Log.d("CameraDebug", "URI is valid. Launching takePictureLauncher...")
                takePictureLauncher.launch(uri)
            } else {
                Log.e("CameraDebug", "URI creation FAILED!")
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        } else {
            Log.d("CameraDebug", "Permission check FAILED. Launching permission request...")
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    Column {
        Text(
            "Лична карта",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ImageSelectorBox(
                modifier = Modifier.weight(1f),
                label = "Предна страна",
                imageUri = frontImageUri,
                existingImageUrl = null,
                onSelectClick = {
                    Log.d("CameraDebug", "Front ImageSelectorBox clicked!")
                    launchCamera(CaptureSide.FRONT) }
            )

            ImageSelectorBox(
                modifier = Modifier.weight(1f),
                label = "Задна страна",
                imageUri = backImageUri,
                existingImageUrl = null,
                onSelectClick = {
                    Log.d("CameraDebug", "Back ImageSelectorBox clicked!")
                    launchCamera(CaptureSide.BACK) }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isProcessing) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
                Text(
                    "Обработка...",
                    modifier = Modifier.padding(top = 60.dp),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        if (showDialog && parsedData != null) {
            ConfirmationDialog(
                initialData = parsedData!!,
                onConfirm = { editedData ->
                    viewModel.onDialogConfirm(editedData)
                },
                onDismiss = {
                    viewModel.onDialogDismiss()
                }
            )
        }
    }
}

@Composable
fun ConfirmationDialog(
    initialData: IdCardData,
    onConfirm: (IdCardData) -> Unit,
    onDismiss: () -> Unit
) {
    var editedEgn by remember { mutableStateOf(initialData.egn) }
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

    var editedGivenName by remember { mutableStateOf(initialData.givenName) }
    var editedFatherName by remember { mutableStateOf(initialData.fatherName) }
    var editedFamilyName by remember { mutableStateOf(initialData.familyName) }

    var editedPlaceOfBirth by remember { mutableStateOf(initialData.placeOfBirth) }
    var editedIssuingAuthority by remember { mutableStateOf(initialData.issuingAuthority) }
    var editedDateOfIssue by remember { mutableStateOf(initialData.dateOfIssue) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Потвърди данните", fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {

                Text("Имена:", style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(bottom = 4.dp))
                OutlinedTextField(
                    value = editedGivenName,
                    onValueChange = { editedGivenName = it },
                    label = { Text("Име / Given Name(s)") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    singleLine = true
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

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                OutlinedTextField(
                    value = editedEgn,
                    onValueChange = { editedEgn = it },
                    label = { Text("ЕГН") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    singleLine = true
                )
                OutlinedTextField(
                    value = editedDocNum,
                    onValueChange = { editedDocNum = it },
                    label = { Text("№ на документ / Карта №") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    singleLine = true
                )
                OutlinedTextField(
                    value = editedPlaceOfBirth,
                    onValueChange = { editedPlaceOfBirth = it },
                    label = { Text("Място на раждане") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                OutlinedTextField(
                    value = editedDob,
                    onValueChange = { editedDob = it },
                    label = { Text("Дата на раждане (ДД.ММ.ГГГГ)") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    singleLine = true
                )
                OutlinedTextField(
                    value = editedDateOfIssue,
                    onValueChange = { editedDateOfIssue = it },
                    label = { Text("Дата на издаване (ДД.ММ.ГГГГ)") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    singleLine = true
                )
                OutlinedTextField(
                    value = editedExpiry,
                    onValueChange = { editedExpiry = it },
                    label = { Text("Валидна до (ДД.ММ.ГГГГ)") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    singleLine = true
                )
                OutlinedTextField(
                    value = editedIssuingAuthority,
                    onValueChange = { editedIssuingAuthority = it },
                    label = { Text("Издаден от / Орган") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "Моля, прегледайте и коригирайте извлечената информация.",
                    style = MaterialTheme.typography.bodySmall
                )

                if (initialData.rawText.isNotBlank()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Суров текст от OCR:", fontWeight = FontWeight.Bold)
                    Text(
                        initialData.rawText,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.heightIn(max = 100.dp).verticalScroll(rememberScrollState())
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val finalConstructedName = listOfNotNull(editedGivenName, editedFatherName, editedFamilyName)
                        .filter { it.isNotBlank() }
                        .joinToString(" ")

                    val confirmedData = initialData.copy(
                        egn = editedEgn.trim(),
                        name = finalConstructedName,
                        documentNumber = editedDocNum.trim(),
                        dateOfBirth = editedDob.trim(),
                        expiryDate = editedExpiry.trim(),
                        givenName = editedGivenName.trim(),
                        fatherName = editedFatherName.trim(),
                        familyName = editedFamilyName.trim(),
                        placeOfBirth = editedPlaceOfBirth.trim(),
                        issuingAuthority = editedIssuingAuthority.trim(),
                        dateOfIssue = editedDateOfIssue.trim()
                    )
                    onConfirm(confirmedData)
                }
            ) {
                Text("Потвърди")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Отказ")
            }
        }
    )
}
@Preview(showBackground = true, widthDp = 380, name = "Profile Screen Loading")
@Composable
fun ProfileScreenLoadingPreview() {
    AppTheme {
        ProfileScreen(
            userProfile = null,
            onNavigateBack = {}
        )
    }
}