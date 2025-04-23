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
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.text.MatchResult
import kotlin.text.RegexOption

data class IdCardData(
    var rawText: String = "",
    var egn: String = "",
    var name: String = "",
    var documentNumber: String = "",
    var dateOfBirth: String = "",
    var expiryDate: String = "",
    // --- Name Parts ---
    var givenName: String = "",
    var fatherName: String = "",
    var familyName: String = "",
    // --- Back Side Fields ---
    var placeOfBirth: String = "",
    var issuingAuthority: String = "",
    var dateOfIssue: String = ""
)

enum class CaptureSide { FRONT, BACK }

class IdScanViewModel(application: Application) : AndroidViewModel(application) {

     private val textRecognizer = TextRecognition.getClient(TextRecognizerOptions.Builder().build())

    private val context: Context
        get() = getApplication()

    val frontImageUri = mutableStateOf<Uri?>(null)
    val backImageUri = mutableStateOf<Uri?>(null)
    val showDialog = mutableStateOf(false)
    val parsedData = mutableStateOf<IdCardData?>(null)
    val isProcessing = mutableStateOf(false)
    val errorMessage = mutableStateOf<String?>(null)

    private var tempUri: Uri? = null
    private var captureTriggeredFor: CaptureSide? = null


    /**
     * Creates a temporary file URI using FileProvider for the camera output.
     */
    fun createTempUri(): Uri? {
        return try {
            val file = createImageFile()
            tempUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                file
            )
            tempUri
        } catch (e: IOException) {
            Log.e("IdScanViewModel", "Error creating temp file URI", e)
            errorMessage.value = "Грешка при създаване на временен файл."
            null
        } catch (e: IllegalArgumentException) {
            Log.e("IdScanViewModel", "Error getting URI for file (FileProvider issue?)", e)
            errorMessage.value = "Грешка при настройка на камерата (FileProvider)."
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
                null -> { }
            }
            analyzeImage(tempUri!!)
        } else {
            if (!success) {
                errorMessage.value = "Заснемането с камера е отказано или неуспешно."
            } else if (tempUri == null) {
                errorMessage.value = "Грешка: Не е намерен URI на изображението."
            }
            clearTemporaryState()
        }
    }


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

                        val extractedData = parseIdCardInfoInternal(rawText)
                        Log.d("IdScanViewModel", "OCR Parsing finished. Scanned EGN: ${extractedData.egn}")

                        val loggedInProfile = CurrentUserHolder.getCurrentProfile()
                        val loggedInEgn = loggedInProfile?.user?.egn
                        Log.d("IdScanViewModel", "Logged-in EGN from CurrentUserHolder: $loggedInEgn")

                        var egnMismatch = false
                        if (!loggedInEgn.isNullOrBlank() && extractedData.egn.isNotBlank()) {
                            if (loggedInEgn != extractedData.egn) {
                                egnMismatch = true
                                Log.w("IdScanViewModel", "EGN MISMATCH! Logged in: $loggedInEgn, Scanned: ${extractedData.egn}")

                                errorMessage.value = "Сканираното ЕГН не съвпада с вашето."

                                when (currentSide) {
                                    CaptureSide.FRONT -> frontImageUri.value = null
                                    CaptureSide.BACK -> backImageUri.value = null
                                    null -> Log.w("IdScanViewModel", "Capture side was null during mismatch handling.")
                                }

                                clearTemporaryState(keepUri = false)

                                isProcessing.value = false

                                return@addOnSuccessListener
                            } else {
                                Log.d("IdScanViewModel", "EGN Match successful.")
                            }
                        } else {
                            Log.d("IdScanViewModel", "EGN Check skipped: Logged-in EGN or Scanned EGN is blank/null.")
                        }

                        if (!egnMismatch) {
                            parsedData.value = extractedData.copy(rawText = rawText)
                            showDialog.value = true
                            Log.d("IdScanViewModel", "ML Kit processing successful and EGN check passed/skipped.")
                            isProcessing.value = false
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e("IdScanViewModel", "ML Text recognition failed", e)
                        errorMessage.value = "Неуспешно разпознаване на текст: ${e.message}"
                        isProcessing.value = false
                        when (currentSide) {
                            CaptureSide.FRONT -> frontImageUri.value = null
                            CaptureSide.BACK -> backImageUri.value = null
                            null -> { }
                        }
                        clearTemporaryState(keepUri = false)
                    }
            } catch (e: IOException) {
                Log.e("IdScanViewModel", "Failed to create InputImage from URI", e)
                errorMessage.value = "Грешка при зареждане на изображението за анализ."
                isProcessing.value = false
                clearTemporaryState()
            } catch (e: Exception) {
                Log.e("IdScanViewModel", "Unexpected error during image analysis", e)
                errorMessage.value = "Възникна неочаквана грешка при анализ."
                isProcessing.value = false
                when (currentSide) {
                    CaptureSide.FRONT -> frontImageUri.value = null
                    CaptureSide.BACK -> backImageUri.value = null
                    null -> {  }
                }
                clearTemporaryState(keepUri = false)
            }
        }
    }


    /**
     * Parses the raw text extracted by OCR to find ID card fields.
     * IMPORTANT: This needs significant refinement based on more OCR samples!
     */
    private fun parseIdCardInfoInternal(text: String): IdCardData {
        Log.d("IdScanViewModel", "Starting parsing...")

        val egn = extractUsingRegex(text, """ЕГН\s*[^\d]*(\d{10})""")
            ?: extractUsingRegex(text, """\b(\d{10})\b""")

        val docNum = extractUsingRegex(text, """(?:Карта\s*№|№|No)\s*(\d{9})\b""")
            ?: extractUsingRegex(text, """\b(\d{9})\b""")

        val familyName = extractUsingRegex(text, """Surname\s+([A-Z\-']+)""")
            ?: extractUsingRegex(text, """Фамилия\s*\n?([А-ЯЁA-Z\-']+)""")
            ?: extractUsingRegex(text, """DaMunuA\s*\n?([А-ЯЁA-Z\-']+)""")

        val givenName = extractUsingRegex(text, """Name\s*\n+([A-Z\s\-']+)""")
            ?: extractUsingRegex(text, """Име(?:на)?\s*\n?([А-ЯЁA-Z\s\-']+)""")

        val fatherName = extractUsingRegex(text, """Father's\s+name\s*\n?([A-Z\-']+)""")
            ?: extractUsingRegex(text, """Презиме\s*\n?([А-ЯЁA-Z\-']+)""")
            ?: extractUsingRegex(text, """pessue\s*\n?([А-ЯЁA-Z\-']+)""")

        val allDates = Regex("""\b(\d{2}\.\d{2}\.\d{4})\b""").findAll(text).map { it.value }.toList()
        var dob: String? = null
        var expiry: String? = null
        var issueDate: String? = null

        dob = extractUsingRegex(text, """Дата\s+на\s+раждане.*?\b(\d{2}\.\d{2}\.\d{4})\b""", RegexOption.DOT_MATCHES_ALL)
            ?: extractUsingRegex(text, """Date\s+of\s+birth.*?\b(\d{2}\.\d{2}\.\d{4})\b""", RegexOption.DOT_MATCHES_ALL)
                    ?: allDates.getOrNull(0)

        expiry = extractUsingRegex(text, """Валидна\s+до.*?\b(\d{2}\.\d{2}\.\d{4})\b""", RegexOption.DOT_MATCHES_ALL)
            ?: extractUsingRegex(text, """Date\s+of\s+expiry.*?\b(\d{2}\.\d{2}\.\d{4})\b""", RegexOption.DOT_MATCHES_ALL)
                    ?: allDates.lastOrNull()

        issueDate = extractUsingRegex(text, """Дата\s+на\s+издаване.*?\b(\d{2}\.\d{2}\.\d{4})\b""", RegexOption.DOT_MATCHES_ALL)
            ?: extractUsingRegex(text, """Date\s+of\s+issue.*?\b(\d{2}\.\d{2}\.\d{4})\b""", RegexOption.DOT_MATCHES_ALL)

        val placeOfBirth = extractUsingRegex(text, """(?:Място\s+на\s+раждане|Place\s+of\s+birth)\s*\n?([А-Яа-яЁёA-Za-z\s,\.\-]+)""")

        val issuingAuthority = extractUsingRegex(text, """(?:Издаден\s+от|Орган,\s+издал\s+документа|Authority)\s*\n?([\s\S]+?)(?=\n\s*(?:Подпис|Signature|$))""")
            ?.replace(Regex("""\s*\n\s*"""), " ")
            ?.trim()

        val constructedName = listOfNotNull(givenName, fatherName, familyName)
            .filter { it.isNotBlank() }
            .joinToString(" ")

        val result = IdCardData(
            egn = egn ?: "",
            name = constructedName,
            documentNumber = docNum ?: "",
            dateOfBirth = dob ?: "",
            expiryDate = expiry ?: "",
            givenName = givenName ?: "",
            fatherName = fatherName ?: "",
            familyName = familyName ?: "",
            placeOfBirth = placeOfBirth ?: "",
            issuingAuthority = issuingAuthority ?: "",
            dateOfIssue = issueDate ?: ""
        )
        Log.d("IdScanViewModel", "Parsing finished. Result: $result")
        return result
    }


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
                        if (groupIndex >= 0 && groupIndex < matchResult.groups.size) {
                            matchResult.groups[groupIndex]?.value?.trim()
                        } else {
                            Log.w("extractUsingRegex", "Invalid groupIndex $groupIndex for pattern: $pattern")
                            null
                        }
                    }
                }?.takeIf { it.isNotBlank() }
        } catch (e: Exception) {
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
        option: RegexOption,
        groupIndex: Int = 1
    ): String? {
        return extractUsingRegex(text, pattern, groupIndex, setOf(option, RegexOption.IGNORE_CASE), null)
    }


    /**
     * Called when the user confirms the data in the dialog.
     */
    fun onDialogConfirm(editedData: IdCardData) {
        showDialog.value = false
        clearTemporaryState(keepUri = true)
    }

    /**
     * Called when the user dismisses the confirmation dialog.
     */
    fun onDialogDismiss() {
        Log.d("IdScanViewModel", "Confirmation dialog dismissed.")
        showDialog.value = false
        clearTemporaryState(keepUri = true)
    }

    /**
     * Creates a temporary image file in app-specific external storage.
     */
    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir = context.getExternalFilesDir("images")
            ?: throw IOException("Cannot get external files directory.")

        if (!storageDir.exists()) {
            if (!storageDir.mkdirs()) {
                throw IOException("Failed to create directory: ${storageDir.absolutePath}")
            }
        }
        Log.d("IdScanViewModel", "Creating temp file in: ${storageDir.absolutePath}")

        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        )
    }

    /**
     * Clears temporary state variables like the temp URI and capture side.
     * Optionally keeps the displayed image URIs.
     */
    private fun clearTemporaryState(keepUri: Boolean = false) {
        Log.d("IdScanViewModel", "Clearing temporary state. Keep URIs: $keepUri")
        tempUri = null
        captureTriggeredFor = null
        if (!keepUri) {
            frontImageUri.value = null
            backImageUri.value = null
            parsedData.value = null
        }
    }
}