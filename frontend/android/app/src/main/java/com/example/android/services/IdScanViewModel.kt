package com.example.android.services // Make sure this package matches your project structure

import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.utils.CurrentUserHolder
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
// Import Cyrillic explicitly if you switch recognizer init below
// Default Latin options (or remove if only using Cyrillic)
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.text.MatchResult
import java.util.regex.Pattern // Keep if needed for other complex patterns, though findAll is often better
import kotlin.text.RegexOption // Import RegexOption

// Data class to hold parsed results
data class IdCardData(
    var rawText: String = "", // Keep raw text for reference/debugging
    var egn: String = "",
    var name: String = "", // Constructed full name
    var documentNumber: String = "",
    var dateOfBirth: String = "",
    var expiryDate: String = "",
    // --- Name Parts ---
    var givenName: String = "", // Explicit Given Name(s)
    var fatherName: String = "", // Explicit Father's Name (Презиме)
    var familyName: String = "", // Explicit Family Name (Фамилия)
    // --- Back Side Fields ---
    var placeOfBirth: String = "",
    var issuingAuthority: String = "",
    var dateOfIssue: String = ""
    // Add others like Sex (Пол), Nationality (Гражданство) if needed
)

// Enum to track which side (front/back) triggered the camera capture
enum class CaptureSide { FRONT, BACK }

class IdScanViewModel(application: Application) : AndroidViewModel(application) {

    // Initialize the Text Recognizer
    // IMPORTANT: For Bulgarian IDs, use Cyrillic options!
    private val textRecognizer = TextRecognition.getClient(TextRecognizerOptions.Builder().build())
    // private val textRecognizer = TextRecognition.getClient(TextRecognizerOptions.Builder().build()) // Original Latin default

    private val context: Context
        get() = getApplication()

    // --- State ---
    val frontImageUri = mutableStateOf<Uri?>(null)
    val backImageUri = mutableStateOf<Uri?>(null)
    val showDialog = mutableStateOf(false)
    val parsedData = mutableStateOf<IdCardData?>(null)
    val isProcessing = mutableStateOf(false)
    val errorMessage = mutableStateOf<String?>(null)

    private var tempUri: Uri? = null // Holds URI between camera launch and result
    private var captureTriggeredFor: CaptureSide? = null // Track which side triggered

    // --- Camera Logic ---

    /**
     * Creates a temporary file URI using FileProvider for the camera output.
     */
    fun createTempUri(): Uri? {
        return try {
            val file = createImageFile()
            tempUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider", // Authority must match AndroidManifest.xml
                file
            )
            tempUri
        } catch (e: IOException) {
            Log.e("IdScanViewModel", "Error creating temp file URI", e)
            errorMessage.value = "Грешка при създаване на временен файл." // Error creating temporary file.
            null
        } catch (e: IllegalArgumentException) {
            Log.e("IdScanViewModel", "Error getting URI for file (FileProvider issue?)", e)
            errorMessage.value = "Грешка при настройка на камерата (FileProvider)." // Error setting up camera (FileProvider).
            null
        }
    }

    /**
     * Sets which side (Front/Back) triggered the camera capture.
     */
    fun setCaptureSide(side: CaptureSide) {
        captureTriggeredFor = side
    }

    /**
     * Handles the result from the camera activity.
     */
    fun handleCameraResult(success: Boolean) {
        Log.d("IdScanViewModel", "handleCameraResult: success=$success, tempUri=$tempUri, side=$captureTriggeredFor")
        if (success && tempUri != null && captureTriggeredFor != null) {
            when (captureTriggeredFor) {
                CaptureSide.FRONT -> frontImageUri.value = tempUri
                CaptureSide.BACK -> backImageUri.value = tempUri
                null -> { /* Should not happen, but handle defensively */ }
            }
            analyzeImage(tempUri!!) // Start ML analysis
        } else {
            if (!success) {
                errorMessage.value = "Заснемането с камера е отказано или неуспешно." // Camera capture cancelled or failed.
            } else if (tempUri == null) {
                errorMessage.value = "Грешка: Не е намерен URI на изображението." // Error: Image URI not found.
            }
            clearTemporaryState() // Clear temp state on failure/cancellation
        }
    }

    // --- ML Kit Analysis ---

    /**
     * Analyzes the image at the given URI using ML Kit Text Recognition.
     */
    private fun analyzeImage(uri: Uri) {
        Log.d("IdScanViewModel", "analyzeImage started for URI: $uri")
        viewModelScope.launch {
            isProcessing.value = true
            errorMessage.value = null // Clear previous errors
            val currentSide = captureTriggeredFor // Capture the side for use in listeners

            try {
                val inputImage = InputImage.fromFilePath(context, uri)
                textRecognizer.process(inputImage)
                    .addOnSuccessListener { visionText ->
                        val rawText = visionText.text
                        Log.d("OCR_Raw_Text", "---- START RAW TEXT (Side: $currentSide) ----")
                        Log.d("OCR_Raw_Text", rawText)
                        Log.d("OCR_Raw_Text", "---- END RAW TEXT ----")

                        // 1. Parse the extracted text
                        val extractedData = parseIdCardInfoInternal(rawText)
                        Log.d("IdScanViewModel", "OCR Parsing finished. Scanned EGN: ${extractedData.egn}")

                        // 2. Get logged-in user's EGN
                        val loggedInProfile = CurrentUserHolder.getCurrentProfile()
                        val loggedInEgn = loggedInProfile?.user?.egn
                        Log.d("IdScanViewModel", "Logged-in EGN from CurrentUserHolder: $loggedInEgn")

                        // 3. Perform the EGN Check (if possible)
                        var egnMismatch = false
                        if (!loggedInEgn.isNullOrBlank() && extractedData.egn.isNotBlank()) {
                            if (loggedInEgn != extractedData.egn) {
                                egnMismatch = true
                                Log.w("IdScanViewModel", "EGN MISMATCH! Logged in: $loggedInEgn, Scanned: ${extractedData.egn}")

                                // --- Handle Mismatch ---
                                // a) Set error message for Snackbar
                                errorMessage.value = "Сканираното ЕГН не съвпада с вашето." // "Scanned EGN does not match yours."

                                // b) Clear the image URI for the scanned side
                                when (currentSide) {
                                    CaptureSide.FRONT -> frontImageUri.value = null
                                    CaptureSide.BACK -> backImageUri.value = null
                                    null -> Log.w("IdScanViewModel", "Capture side was null during mismatch handling.")
                                }

                                // c) Clear temporary state without keeping the URI (which we just nulled)
                                clearTemporaryState(keepUri = false)

                                // d) Stop processing indicator
                                isProcessing.value = false

                                // e) Do NOT proceed to show dialog or update parsedData
                                return@addOnSuccessListener // Exit the success listener early
                            } else {
                                Log.d("IdScanViewModel", "EGN Match successful.")
                            }
                        } else {
                            Log.d("IdScanViewModel", "EGN Check skipped: Logged-in EGN or Scanned EGN is blank/null.")
                            // Allow proceeding if check couldn't be performed (e.g., first scan before login?)
                            // Or you could enforce that the user must be logged in first.
                        }

                        // 4. If EGN matches (or check skipped), proceed normally
                        if (!egnMismatch) {
                            parsedData.value = extractedData.copy(rawText = rawText) // Store parsed data
                            showDialog.value = true // Trigger dialog display
                            Log.d("IdScanViewModel", "ML Kit processing successful and EGN check passed/skipped.")
                            isProcessing.value = false
                            // Temp state is cleared automatically by dialog actions later
                        }
                    }
                    .addOnFailureListener { e ->
                        // --- Handle ML Kit Failure ---
                        Log.e("IdScanViewModel", "ML Text recognition failed", e)
                        errorMessage.value = "Неуспешно разпознаване на текст: ${e.message}"
                        isProcessing.value = false
                        // Keep the potentially captured image displayed? Or clear it on ML failure?
                        // Clearing seems safer if OCR failed badly.
                        when (currentSide) {
                            CaptureSide.FRONT -> frontImageUri.value = null
                            CaptureSide.BACK -> backImageUri.value = null
                            null -> { /* No URI to clear */ }
                        }
                        clearTemporaryState(keepUri = false) // Clear temp state and URI on ML failure
                    }
            } catch (e: IOException) {
                // --- Handle Image Loading Failure ---
                Log.e("IdScanViewModel", "Failed to create InputImage from URI", e)
                errorMessage.value = "Грешка при зареждане на изображението за анализ."
                isProcessing.value = false
                clearTemporaryState() // Clear temp state on image load failure
            } catch (e: Exception) {
                // --- Handle Other Unexpected Failures ---
                Log.e("IdScanViewModel", "Unexpected error during image analysis", e)
                errorMessage.value = "Възникна неочаквана грешка при анализ."
                isProcessing.value = false
                when (currentSide) { // Also clear image on unexpected errors
                    CaptureSide.FRONT -> frontImageUri.value = null
                    CaptureSide.BACK -> backImageUri.value = null
                    null -> { /* No URI to clear */ }
                }
                clearTemporaryState(keepUri = false)
            }
        }
    }

    // --- Parsing Logic ---

    /**
     * Parses the raw text extracted by OCR to find ID card fields.
     * IMPORTANT: This needs significant refinement based on more OCR samples!
     */
    private fun parseIdCardInfoInternal(text: String /*, side: CaptureSide? */): IdCardData {
        Log.d("IdScanViewModel", "Starting parsing...")

        // --- Field Extraction (Adapt HEAVILY based on OCR output!) ---

        // EGN (Usually reliable)
        val egn = extractUsingRegex(text, """ЕГН\s*[^\d]*(\d{10})""") // Allow non-digits between ЕГН and number
            ?: extractUsingRegex(text, """\b(\d{10})\b""") // Fallback

        // Document Number (Look for № or No, often 9 digits)
        val docNum = extractUsingRegex(text, """(?:Карта\s*№|№|No)\s*(\d{9})\b""")
            ?: extractUsingRegex(text, """\b(\d{9})\b""") // Fallback

        // --- Name Parts Extraction (Prioritize Latin based on Sample) ---
        // Family Name (Фамилия / Surname)
        val familyName = extractUsingRegex(text, """Surname\s+([A-Z\-']+)""") // Latin label + value same line
            ?: extractUsingRegex(text, """Фамилия\s*\n?([А-ЯЁA-Z\-']+)""") // Cyr/Latin label, value same/next line
            ?: extractUsingRegex(text, """DaMunuA\s*\n?([А-ЯЁA-Z\-']+)""") // OCR error for Фамилия

        // Given Name(s) (Име / Name)
        // Try capturing multiple lines after "Name" until another known label or just uppercase words
        val givenName = extractUsingRegex(text, """Name\s*\n+([A-Z\s\-']+)""") // Latin label, value on next line(s)
            ?: extractUsingRegex(text, """Име(?:на)?\s*\n?([А-ЯЁA-Z\s\-']+)""") // Cyrillic label

        // Father's Name (Презиме / Father's name)
        val fatherName = extractUsingRegex(text, """Father's\s+name\s*\n?([A-Z\-']+)""") // Latin label
            ?: extractUsingRegex(text, """Презиме\s*\n?([А-ЯЁA-Z\-']+)""") // Cyrillic label
            ?: extractUsingRegex(text, """pessue\s*\n?([А-ЯЁA-Z\-']+)""") // OCR error for Презиме

        // --- Date Processing ---
        val allDates = Regex("""\b(\d{2}\.\d{2}\.\d{4})\b""").findAll(text).map { it.value }.toList()
        var dob: String? = null
        var expiry: String? = null
        var issueDate: String? = null

        // Try finding dates near keywords (requires more robust context analysis)
        // Simple positional/keyword approach (HIGHLY UNRELIABLE - NEEDS IMPROVEMENT)
        dob = extractUsingRegex(text, """Дата\s+на\s+раждане.*?\b(\d{2}\.\d{2}\.\d{4})\b""", RegexOption.DOT_MATCHES_ALL)
            ?: extractUsingRegex(text, """Date\s+of\s+birth.*?\b(\d{2}\.\d{2}\.\d{4})\b""", RegexOption.DOT_MATCHES_ALL)
                    ?: allDates.getOrNull(0) // Fallback to first found date

        expiry = extractUsingRegex(text, """Валидна\s+до.*?\b(\d{2}\.\d{2}\.\d{4})\b""", RegexOption.DOT_MATCHES_ALL)
            ?: extractUsingRegex(text, """Date\s+of\s+expiry.*?\b(\d{2}\.\d{2}\.\d{4})\b""", RegexOption.DOT_MATCHES_ALL)
                    ?: allDates.lastOrNull() // Fallback to last found date (often expiry)

        issueDate = extractUsingRegex(text, """Дата\s+на\s+издаване.*?\b(\d{2}\.\d{2}\.\d{4})\b""", RegexOption.DOT_MATCHES_ALL)
            ?: extractUsingRegex(text, """Date\s+of\s+issue.*?\b(\d{2}\.\d{2}\.\d{4})\b""", RegexOption.DOT_MATCHES_ALL)
        // Avoid simple positional fallback for issue date unless context is clearer

        // --- Back Side Fields (Refine based on back side OCR samples) ---
        // Place of Birth (look for label, capture text possibly including city/country)
        val placeOfBirth = extractUsingRegex(text, """(?:Място\s+на\s+раждане|Place\s+of\s+birth)\s*\n?([А-Яа-яЁёA-Za-z\s,\.\-]+)""")

        // Issuing Authority (look for label, capture potentially multi-line text until next known field)
        val issuingAuthority = extractUsingRegex(text, """(?:Издаден\s+от|Орган,\s+издал\s+документа|Authority)\s*\n?([\s\S]+?)(?=\n\s*(?:Подпис|Signature|$))""")
            ?.replace(Regex("""\s*\n\s*"""), " ") // Replace internal newlines with spaces
            ?.trim()

        // Construct full name for display convenience
        val constructedName = listOfNotNull(givenName, fatherName, familyName)
            .filter { it.isNotBlank() }
            .joinToString(" ")

        val result = IdCardData(
            egn = egn ?: "",
            name = constructedName, // Use combined name
            documentNumber = docNum ?: "",
            dateOfBirth = dob ?: "",
            expiryDate = expiry ?: "",
            // --- Assign extracted parts ---
            givenName = givenName ?: "",
            fatherName = fatherName ?: "",
            familyName = familyName ?: "",
            // --- Assign other fields ---
            placeOfBirth = placeOfBirth ?: "",
            issuingAuthority = issuingAuthority ?: "",
            dateOfIssue = issueDate ?: ""
            // rawText is added back in analyzeImage -> onSuccessListener
        )
        Log.d("IdScanViewModel", "Parsing finished. Result: $result")
        return result
    }

    // --- Regex Helper Functions ---

    /**
     * Helper function to find text using regex and extract a specific group.
     */
    private fun extractUsingRegex(
        text: String,
        pattern: String,
        groupIndex: Int = 1,
        options: Set<RegexOption> = setOf(RegexOption.IGNORE_CASE, RegexOption.MULTILINE),
        transform: ((MatchResult) -> String)? = null
    ): String? {
        return try {
            Regex(pattern, options)
                .find(text)?.let { matchResult ->
                    if (transform != null) {
                        transform(matchResult)
                    } else {
                        // Check if group index is valid before accessing
                        if (groupIndex >= 0 && groupIndex < matchResult.groups.size) {
                            matchResult.groups[groupIndex]?.value?.trim()
                        } else {
                            Log.w("extractUsingRegex", "Invalid groupIndex $groupIndex for pattern: $pattern")
                            null // Invalid group index requested
                        }
                    }
                }?.takeIf { it.isNotBlank() }
        } catch (e: Exception) {
            // Log regex exceptions
            Log.e("extractUsingRegex", "Error processing regex pattern: $pattern", e)
            null
        }
    }

    /**
     * Overload of extractUsingRegex for conveniently passing a single RegexOption.
     */
    private fun extractUsingRegex(
        text: String,
        pattern: String,
        option: RegexOption, // Accepts a single RegexOption as 3rd argument
        groupIndex: Int = 1  // groupIndex is now the 4th argument
    ): String? {
        // Calls the main function, wrapping the single option in a Set along with IGNORE_CASE
        return extractUsingRegex(text, pattern, groupIndex, setOf(option, RegexOption.IGNORE_CASE), null)
    }

    // --- Dialog Actions ---

    /**
     * Called when the user confirms the data in the dialog.
     */
    fun onDialogConfirm(editedData: IdCardData) {
        // TODO: Implement actual saving logic here
        // - Update a local UserProfile state/database
        // - Make an API call to save the data
        Log.i("IdScanViewModel", "Confirmed Data: $editedData") // Use Log.i for info
        // Potentially clear the image URIs after successful confirmation/upload?
        // frontImageUri.value = null
        // backImageUri.value = null
        showDialog.value = false
        clearTemporaryState(keepUri = true) // Keep the image displayed for now
    }

    /**
     * Called when the user dismisses the confirmation dialog.
     */
    fun onDialogDismiss() {
        Log.d("IdScanViewModel", "Confirmation dialog dismissed.")
        showDialog.value = false
        // Decide if you want to clear the captured image on dismiss
        // clearTemporaryState(keepUri = false) // Uncomment to clear image on dismiss
        clearTemporaryState(keepUri = true) // Keep image displayed on dismiss
    }

    // --- File Helper ---

    /**
     * Creates a temporary image file in app-specific external storage.
     */
    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        // Use app-specific external files directory (matches <external-files-path> in file_paths.xml)
        val storageDir = context.getExternalFilesDir("images")
            ?: throw IOException("Cannot get external files directory.") // Throw if null

        // Ensure the directory exists
        if (!storageDir.exists()) {
            if (!storageDir.mkdirs()) {
                throw IOException("Failed to create directory: ${storageDir.absolutePath}")
            }
        }
        Log.d("IdScanViewModel", "Creating temp file in: ${storageDir.absolutePath}")

        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        )
    }

    // --- State Cleanup ---

    /**
     * Clears temporary state variables like the temp URI and capture side.
     * Optionally keeps the displayed image URIs.
     */
    private fun clearTemporaryState(keepUri: Boolean = false) {
        Log.d("IdScanViewModel", "Clearing temporary state. Keep URIs: $keepUri")
        tempUri = null // Always clear the temp URI reference after use/failure
        captureTriggeredFor = null
        // Don't clear parsedData here automatically, dialog actions handle it.
        if (!keepUri) {
            // Optionally clear displayed URIs if the whole process is cancelled or finished
            Log.d("IdScanViewModel", "Clearing displayed image URIs.")
            frontImageUri.value = null
            backImageUri.value = null
            // Also clear parsed data if clearing URIs
            parsedData.value = null
        }
    }
}