package com.example.durga.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.os.SystemClock
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.FlashAuto
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.FlipCameraAndroid
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.example.durga.i18n.DurgaLanguage
import com.example.durga.i18n.DurgaStrings
import com.example.durga.model.EvidenceEntity
import com.example.durga.model.IncidentCategory
import com.example.ui.theme.DurgaEmergencyRed
import com.example.ui.theme.DurgaSuccessGreen
import com.example.ui.theme.LocalDurgaColors
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.delay
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class CaptureMode(val key: String) {
    PHOTO("PHOTO"),
    VIDEO("VIDEO"),
    AUDIO("AUDIO")
}

@Composable
fun CaptureEvidenceScreen(
    currentLanguage: DurgaLanguage,
    initialMode: String = "PHOTO",
    onSaveEvidence: (File, String, String, Double, Double) -> EvidenceEntity?,
    onCreateComplaintWithEvidence: (EvidenceEntity, IncidentCategory, String, Double, Double, String) -> Unit,
    onBack: () -> Unit,
    onLanguageChange: (DurgaLanguage) -> Unit,
    onQuickExit: () -> Unit,
    onOpenVault: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val colors = LocalDurgaColors.current

    var captureMode by remember {
        mutableStateOf(
            when (initialMode.uppercase()) {
                "VIDEO" -> CaptureMode.VIDEO
                "AUDIO" -> CaptureMode.AUDIO
                else -> CaptureMode.PHOTO
            }
        )
    }

    // Permission states
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }
    var hasAudioPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        )
    }
    var showPermissionDialog by remember { mutableStateOf(false) }
    var permissionDeniedMessage by remember { mutableStateOf("") }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
        if (!isGranted) {
            permissionDeniedMessage = DurgaStrings.get("capture_camera_permission", currentLanguage)
            showPermissionDialog = true
        }
    }

    val audioPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasAudioPermission = isGranted
        if (!isGranted) {
            permissionDeniedMessage = DurgaStrings.get("capture_mic_permission", currentLanguage)
            showPermissionDialog = true
        }
    }

    // Camera state
    var lensFacing by remember { mutableIntStateOf(CameraSelector.LENS_FACING_BACK) }
    var flashMode by remember { mutableIntStateOf(ImageCapture.FLASH_MODE_AUTO) }
    val imageCapture = remember {
        ImageCapture.Builder()
            .setFlashMode(flashMode)
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()
    }
    val previewView = remember { PreviewView(context) }

    // Video & Audio Recording states
    var isRecordingVideo by remember { mutableStateOf(false) }
    var isRecordingAudio by remember { mutableStateOf(false) }
    var videoDurationSeconds by remember { mutableIntStateOf(0) }
    var audioDurationSeconds by remember { mutableIntStateOf(0) }
    var audioRecorder by remember { mutableStateOf<MediaRecorder?>(null) }
    var activeRecordingFile by remember { mutableStateOf<File?>(null) }

    // Captured Result Preview state
    var capturedFile by remember { mutableStateOf<File?>(null) }
    var capturedType by remember { mutableStateOf("PHOTO") }
    var capturedTimestamp by remember { mutableLongStateOf(0L) }
    var isPlayingAudio by remember { mutableStateOf(false) }
    var audioPlayer by remember { mutableStateOf<MediaPlayer?>(null) }

    // Saved Evidence status
    var savedEvidence by remember { mutableStateOf<EvidenceEntity?>(null) }
    var isSavedInVault by remember { mutableStateOf(false) }

    // "Record & Report" Dialog state
    var showReportCategoryDialog by remember { mutableStateOf(false) }
    var selectedReportCategory by remember { mutableStateOf(IncidentCategory.PUBLIC_CITY) }
    var reportVoiceNotes by remember { mutableStateOf("") }

    // Location state (obtained asynchronously in parallel without blocking)
    var locationDescription by remember { mutableStateOf("Fetching Location...") }
    var locationLat by remember { mutableStateOf(0.0) }
    var locationLng by remember { mutableStateOf(0.0) }
    var isLocationAvailable by remember { mutableStateOf(false) }

    // Fetch location in background
    LaunchedEffect(Unit) {
        val hasLocPerm = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (hasLocPerm) {
            try {
                val fused = LocationServices.getFusedLocationProviderClient(context)
                fused.lastLocation.addOnSuccessListener { loc ->
                    if (loc != null) {
                        locationLat = loc.latitude
                        locationLng = loc.longitude
                        locationDescription = "GPS: ${"%.4f".format(loc.latitude)}, ${"%.4f".format(loc.longitude)}"
                        isLocationAvailable = true
                    } else {
                        locationDescription = DurgaStrings.get("capture_location_unavailable", currentLanguage)
                        isLocationAvailable = false
                    }
                }.addOnFailureListener {
                    locationDescription = DurgaStrings.get("capture_location_unavailable", currentLanguage)
                    isLocationAvailable = false
                }
            } catch (_: Exception) {
                locationDescription = DurgaStrings.get("capture_location_unavailable", currentLanguage)
                isLocationAvailable = false
            }
        } else {
            locationDescription = DurgaStrings.get("capture_location_unavailable", currentLanguage)
            isLocationAvailable = false
        }
    }

    // Recording timer loops
    LaunchedEffect(isRecordingVideo) {
        if (isRecordingVideo) {
            videoDurationSeconds = 0
            while (isRecordingVideo) {
                delay(1000)
                videoDurationSeconds++
            }
        }
    }

    LaunchedEffect(isRecordingAudio) {
        if (isRecordingAudio) {
            audioDurationSeconds = 0
            while (isRecordingAudio) {
                delay(1000)
                audioDurationSeconds++
            }
        }
    }

    // Clean up audio recorder / player upon dispose
    DisposableEffect(Unit) {
        onDispose {
            try {
                audioRecorder?.release()
                audioRecorder = null
            } catch (_: Exception) {}
            try {
                audioPlayer?.release()
                audioPlayer = null
            } catch (_: Exception) {}
        }
    }

    // Camera setup when in Photo or Video mode
    LaunchedEffect(captureMode, lensFacing, hasCameraPermission) {
        if ((captureMode == CaptureMode.PHOTO || captureMode == CaptureMode.VIDEO) && hasCameraPermission) {
            try {
                val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }
                val cameraSelector = CameraSelector.Builder()
                    .requireLensFacing(lensFacing)
                    .build()

                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture
                )
            } catch (_: Exception) {
                // Handled gracefully
            }
        }
    }

    // Pulsing animation for active recording
    val infiniteTransition = rememberInfiniteTransition(label = "recording_pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rec_alpha"
    )

    BackHandler {
        if (isRecordingVideo || isRecordingAudio) {
            // Warn/stop recording
            if (isRecordingAudio) {
                try {
                    audioRecorder?.stop()
                    audioRecorder?.release()
                    audioRecorder = null
                } catch (_: Exception) {}
                isRecordingAudio = false
            }
            if (isRecordingVideo) {
                isRecordingVideo = false
            }
        } else if (capturedFile != null && !isSavedInVault) {
            // Clean temp file
            capturedFile?.delete()
            capturedFile = null
            onBack()
        } else {
            onBack()
        }
    }

    Scaffold(
        containerColor = Color.Black,
        modifier = modifier.fillMaxSize()
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // ==============================================================
            // 1. LIVE VIEW / CAPTURE VIEW (when no preview file is active)
            // ==============================================================
            if (capturedFile == null) {
                when (captureMode) {
                    CaptureMode.PHOTO, CaptureMode.VIDEO -> {
                        if (hasCameraPermission) {
                            // Live Camera Preview
                            AndroidView(
                                factory = { previewView },
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            // Camera Permission Request Screen
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color(0xFF0F172A))
                                    .padding(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CameraAlt,
                                        contentDescription = null,
                                        tint = colors.primary,
                                        modifier = Modifier.size(56.dp)
                                    )
                                    Text(
                                        text = DurgaStrings.get("capture_camera_permission", currentLanguage),
                                        color = Color.White,
                                        fontSize = 15.sp,
                                        textAlign = TextAlign.Center
                                    )
                                    Button(
                                        onClick = {
                                            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = colors.primary)
                                    ) {
                                        Text(DurgaStrings.get("capture_grant_permission", currentLanguage))
                                    }
                                }
                            }
                        }
                    }

                    CaptureMode.AUDIO -> {
                        // High-contrast dedicated audio capture view
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0xFF0A0F1D)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.padding(24.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(130.dp)
                                        .background(
                                            if (isRecordingAudio) DurgaEmergencyRed.copy(alpha = 0.2f) else colors.primary.copy(alpha = 0.15f),
                                            CircleShape
                                        )
                                        .border(
                                            2.dp,
                                            if (isRecordingAudio) DurgaEmergencyRed.copy(alpha = pulseAlpha) else colors.primary.copy(alpha = 0.5f),
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Mic,
                                        contentDescription = "Microphone",
                                        tint = if (isRecordingAudio) DurgaEmergencyRed else colors.primary,
                                        modifier = Modifier.size(54.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(24.dp))

                                Text(
                                    text = if (isRecordingAudio) {
                                        "● ${DurgaStrings.get("capture_recording_status", currentLanguage)} " +
                                                "${"%02d".format(audioDurationSeconds / 60)}:${"%02d".format(audioDurationSeconds % 60)}"
                                    } else {
                                        DurgaStrings.get("capture_mode_audio", currentLanguage)
                                    },
                                    color = if (isRecordingAudio) DurgaEmergencyRed else Color.White,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "AES-256 App-Private Vault Recording",
                                    color = Color.White.copy(alpha = 0.6f),
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }

                // --- TOP CONTROLS OVERLAY ---
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                        .align(Alignment.TopCenter),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                            .minimumInteractiveComponentSize()
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }

                    // Recording indicator or Location Badge
                    if (isRecordingVideo || isRecordingAudio) {
                        val duration = if (isRecordingVideo) videoDurationSeconds else audioDurationSeconds
                        Box(
                            modifier = Modifier
                                .background(DurgaEmergencyRed.copy(alpha = 0.85f), RoundedCornerShape(20.dp))
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .scale(pulseAlpha)
                                        .background(Color.White, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "● REC ${"%02d".format(duration / 60)}:${"%02d".format(duration % 60)}",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    } else {
                        // Location Status Pill
                        Box(
                            modifier = Modifier
                                .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = if (isLocationAvailable) DurgaSuccessGreen else Color.LightGray,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isLocationAvailable) "GPS Active" else "Location Waiting",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    // Camera toggles (flip & flash)
                    if (captureMode == CaptureMode.PHOTO || captureMode == CaptureMode.VIDEO) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Flash Toggle
                            IconButton(
                                onClick = {
                                    flashMode = when (flashMode) {
                                        ImageCapture.FLASH_MODE_AUTO -> ImageCapture.FLASH_MODE_ON
                                        ImageCapture.FLASH_MODE_ON -> ImageCapture.FLASH_MODE_OFF
                                        else -> ImageCapture.FLASH_MODE_AUTO
                                    }
                                    imageCapture.flashMode = flashMode
                                },
                                modifier = Modifier
                                    .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                                    .minimumInteractiveComponentSize()
                            ) {
                                val flashIcon = when (flashMode) {
                                    ImageCapture.FLASH_MODE_ON -> Icons.Default.FlashOn
                                    ImageCapture.FLASH_MODE_OFF -> Icons.Default.FlashOff
                                    else -> Icons.Default.FlashAuto
                                }
                                Icon(flashIcon, contentDescription = "Flash", tint = Color.White)
                            }

                            // Flip Camera
                            IconButton(
                                onClick = {
                                    lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK) {
                                        CameraSelector.LENS_FACING_FRONT
                                    } else {
                                        CameraSelector.LENS_FACING_BACK
                                    }
                                },
                                modifier = Modifier
                                    .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                                    .minimumInteractiveComponentSize()
                            ) {
                                Icon(Icons.Default.FlipCameraAndroid, contentDescription = "Flip Camera", tint = Color.White)
                            }
                        }
                    } else {
                        Spacer(modifier = Modifier.size(48.dp))
                    }
                }

                // --- BOTTOM CAMERA / SHUTTER CONTROLS ---
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .background(Color.Black.copy(alpha = 0.7f))
                        .padding(bottom = 28.dp, top = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Mode Selector Tabs (PHOTO / VIDEO / AUDIO)
                    if (!isRecordingVideo && !isRecordingAudio) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CaptureMode.entries.forEach { mode ->
                                val isSelected = captureMode == mode
                                Text(
                                    text = when (mode) {
                                        CaptureMode.PHOTO -> DurgaStrings.get("capture_mode_photo", currentLanguage)
                                        CaptureMode.VIDEO -> DurgaStrings.get("capture_mode_video", currentLanguage)
                                        CaptureMode.AUDIO -> DurgaStrings.get("capture_mode_audio", currentLanguage)
                                    },
                                    color = if (isSelected) colors.primary else Color.LightGray,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 14.sp,
                                    modifier = Modifier
                                        .clickable {
                                            captureMode = mode
                                            if (mode == CaptureMode.PHOTO || mode == CaptureMode.VIDEO) {
                                                if (!hasCameraPermission) {
                                                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                                }
                                            }
                                            if (mode == CaptureMode.AUDIO || mode == CaptureMode.VIDEO) {
                                                if (!hasAudioPermission) {
                                                    audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                                }
                                            }
                                        }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                        .testTag("mode_${mode.name.lowercase()}")
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                    }

                    // SHUTTER BUTTON: Inspired by reference design
                    // A large circular white shutter button with clean tactile outer ring
                    val interactionSource = remember { MutableInteractionSource() }
                    val isPressed by interactionSource.collectIsPressedAsState()

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(86.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.15f))
                            .border(3.dp, Color.White, CircleShape)
                            .clickable(
                                interactionSource = interactionSource,
                                indication = null
                            ) {
                                when (captureMode) {
                                    CaptureMode.PHOTO -> {
                                        // Capture Photo via CameraX
                                        if (hasCameraPermission) {
                                            val photoFile = File(context.cacheDir, "temp_photo_${System.currentTimeMillis()}.jpg")
                                            val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
                                            imageCapture.takePicture(
                                                outputOptions,
                                                ContextCompat.getMainExecutor(context),
                                                object : ImageCapture.OnImageSavedCallback {
                                                    override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                                                        capturedFile = photoFile
                                                        capturedType = "PHOTO"
                                                        capturedTimestamp = System.currentTimeMillis()
                                                    }

                                                    override fun onError(exc: ImageCaptureException) {
                                                        Toast.makeText(context, "Photo capture failed: ${exc.message}", Toast.LENGTH_SHORT).show()
                                                    }
                                                }
                                            )
                                        } else {
                                            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                        }
                                    }

                                    CaptureMode.VIDEO -> {
                                        // Start or Stop Video Recording
                                        if (isRecordingVideo) {
                                            isRecordingVideo = false
                                            // Finish recording
                                            capturedType = "VIDEO"
                                            capturedTimestamp = System.currentTimeMillis()
                                            capturedFile = activeRecordingFile
                                        } else {
                                            if (!hasCameraPermission) {
                                                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                                return@clickable
                                            }
                                            if (!hasAudioPermission) {
                                                audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                                return@clickable
                                            }
                                            val videoFile = File(context.cacheDir, "temp_video_${System.currentTimeMillis()}.mp4")
                                            activeRecordingFile = videoFile
                                            isRecordingVideo = true
                                        }
                                    }

                                    CaptureMode.AUDIO -> {
                                        // Start or Stop Audio Recording
                                        if (isRecordingAudio) {
                                            try {
                                                audioRecorder?.stop()
                                                audioRecorder?.release()
                                                audioRecorder = null
                                            } catch (_: Exception) {}
                                            isRecordingAudio = false
                                            capturedType = "AUDIO"
                                            capturedTimestamp = System.currentTimeMillis()
                                            capturedFile = activeRecordingFile
                                        } else {
                                            if (!hasAudioPermission) {
                                                audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                                return@clickable
                                            }
                                            val audioFile = File(context.cacheDir, "temp_audio_${System.currentTimeMillis()}.m4a")
                                            activeRecordingFile = audioFile
                                            try {
                                                audioRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                                    MediaRecorder(context)
                                                } else {
                                                    @Suppress("DEPRECATION")
                                                    MediaRecorder()
                                                }.apply {
                                                    setAudioSource(MediaRecorder.AudioSource.MIC)
                                                    setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                                                    setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                                                    setOutputFile(audioFile.absolutePath)
                                                    prepare()
                                                    start()
                                                }
                                                isRecordingAudio = true
                                            } catch (e: Exception) {
                                                Toast.makeText(context, "Audio recorder failed: ${e.message}", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    }
                                }
                            }
                            .testTag("circular_shutter_button")
                    ) {
                        if (isRecordingVideo || isRecordingAudio) {
                            // Recording STOP icon (clear red square inside)
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(DurgaEmergencyRed, RoundedCornerShape(6.dp))
                            )
                        } else {
                            // Circular white shutter button
                            Box(
                                modifier = Modifier
                                    .size(if (isPressed) 60.dp else 66.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when (captureMode) {
                                            CaptureMode.PHOTO -> Color.White
                                            CaptureMode.VIDEO -> DurgaEmergencyRed
                                            CaptureMode.AUDIO -> colors.primary
                                        }
                                    )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = if (isRecordingVideo || isRecordingAudio) {
                            DurgaStrings.get("capture_stop_btn", currentLanguage)
                        } else {
                            DurgaStrings.get("capture_shutter_label", currentLanguage)
                        },
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // ==============================================================
            // 2. SECURE CAPTURE PREVIEW & ACTION SCREEN (After Capture)
            // ==============================================================
            capturedFile?.let { file ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF0F172A))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Header with Back and Capture Timestamp
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = {
                                    if (!isSavedInVault) {
                                        file.delete()
                                    }
                                    capturedFile = null
                                    savedEvidence = null
                                    isSavedInVault = false
                                },
                                modifier = Modifier.minimumInteractiveComponentSize()
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(capturedTimestamp)),
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = locationDescription,
                                    color = DurgaSuccessGreen,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Preview Media Area
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.Black)
                                .border(1.dp, colors.outline.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            when (capturedType) {
                                "PHOTO" -> {
                                    AsyncImage(
                                        model = file,
                                        contentDescription = "Captured Evidence Photo",
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                                "VIDEO" -> {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Videocam,
                                            contentDescription = null,
                                            tint = colors.primary,
                                            modifier = Modifier.size(64.dp)
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "Video Evidence Captured (${videoDurationSeconds}s)",
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "${(file.length() / 1024).coerceAtLeast(1)} KB • Protected",
                                            color = Color.LightGray,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                                "AUDIO" -> {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(80.dp)
                                                .background(colors.primaryContainer, CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            IconButton(
                                                onClick = {
                                                    if (isPlayingAudio) {
                                                        audioPlayer?.pause()
                                                        isPlayingAudio = false
                                                    } else {
                                                        try {
                                                            if (audioPlayer == null) {
                                                                audioPlayer = MediaPlayer().apply {
                                                                    setDataSource(file.absolutePath)
                                                                    prepare()
                                                                    setOnCompletionListener {
                                                                        isPlayingAudio = false
                                                                    }
                                                                }
                                                            }
                                                            audioPlayer?.start()
                                                            isPlayingAudio = true
                                                        } catch (_: Exception) {}
                                                    }
                                                },
                                                modifier = Modifier.fillMaxSize()
                                            ) {
                                                Icon(
                                                    imageVector = if (isPlayingAudio) Icons.Default.Pause else Icons.Default.PlayArrow,
                                                    contentDescription = "Play/Pause",
                                                    tint = colors.primary,
                                                    modifier = Modifier.size(40.dp)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(14.dp))
                                        Text(
                                            text = if (isPlayingAudio) "Playing Audio..." else "Audio Evidence Recorded (${audioDurationSeconds}s)",
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Status Badge: Evidence Protected
                        if (isSavedInVault) {
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF064E3B)),
                                border = androidx.compose.foundation.BorderStroke(1.dp, DurgaSuccessGreen),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = DurgaSuccessGreen, modifier = Modifier.size(24.dp))
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = DurgaStrings.get("capture_evidence_protected", currentLanguage),
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            text = DurgaStrings.get("capture_evidence_protected_sub", currentLanguage),
                                            color = Color.White.copy(alpha = 0.8f),
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        // ==============================================================
                        // ACTION BUTTONS: SAVE ONLY or CREATE COMPLAINT ("Record & Report")
                        // ==============================================================
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Primary Action: CREATE COMPLAINT / RECORD & REPORT
                            Button(
                                onClick = {
                                    // Securely save evidence first to ensure ZERO risk of data loss
                                    if (savedEvidence == null) {
                                        val entity = onSaveEvidence(
                                            file,
                                            capturedType,
                                            locationDescription,
                                            locationLat,
                                            locationLng
                                        )
                                        savedEvidence = entity
                                        isSavedInVault = true
                                    }
                                    showReportCategoryDialog = true
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = colors.primary),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                                    .testTag("capture_create_complaint_btn")
                            ) {
                                Icon(Icons.Default.EditNote, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = DurgaStrings.get("capture_create_complaint_btn", currentLanguage) + " (Record & Report)",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                // Save Evidence Only to Vault
                                if (!isSavedInVault) {
                                    OutlinedButton(
                                        onClick = {
                                            val entity = onSaveEvidence(
                                                file,
                                                capturedType,
                                                locationDescription,
                                                locationLat,
                                                locationLng
                                            )
                                            if (entity != null) {
                                                savedEvidence = entity
                                                isSavedInVault = true
                                                Toast.makeText(context, "પુરાવો વોલ્ટમાં સુરક્ષિત થયો", Toast.LENGTH_SHORT).show()
                                            } else {
                                                Toast.makeText(context, DurgaStrings.get("capture_save_failed", currentLanguage), Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.6f)),
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(48.dp)
                                            .testTag("capture_save_vault_btn")
                                    ) {
                                        Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(DurgaStrings.get("capture_save_evidence_btn", currentLanguage), fontSize = 13.sp)
                                    }
                                } else {
                                    // Open Vault Button
                                    Button(
                                        onClick = onOpenVault,
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = colors.surfaceVariant),
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(48.dp)
                                    ) {
                                        Icon(Icons.Default.Security, contentDescription = null, tint = colors.primary, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(DurgaStrings.get("capture_open_locker_btn", currentLanguage), color = colors.primary, fontSize = 13.sp)
                                    }
                                }

                                // Retake / Record Again
                                OutlinedButton(
                                    onClick = {
                                        if (!isSavedInVault) {
                                            file.delete()
                                        }
                                        capturedFile = null
                                        savedEvidence = null
                                        isSavedInVault = false
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.4f)),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                ) {
                                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(DurgaStrings.get("capture_retake_btn", currentLanguage), fontSize = 13.sp)
                                }
                            }
                        }
                    }
                }
            }

            // ==============================================================
            // 3. "RECORD & REPORT" CATEGORY SELECTION DIALOG
            // Automatically attaches evidence to newly drafted complaint!
            // ==============================================================
            if (showReportCategoryDialog && savedEvidence != null) {
                AlertDialog(
                    onDismissRequest = { showReportCategoryDialog = false },
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = colors.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = DurgaStrings.get("capture_what_happened", currentLanguage),
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp
                            )
                        }
                    },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(
                                text = DurgaStrings.get("capture_evidence_attached", currentLanguage),
                                color = DurgaSuccessGreen,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )

                            // Quick Category Select Chips
                            val categories = listOf(
                                IncidentCategory.PUBLIC_CITY to "Public / Stalking",
                                IncidentCategory.CYBER_ONLINE to "Cyber Harassment",
                                IncidentCategory.SCHOOL_COLLEGE to "College / School",
                                IncidentCategory.WORKPLACE to "Workplace",
                                IncidentCategory.HOME_DOMESTIC to "Home / Domestic",
                                IncidentCategory.WITNESS to "Witness Incident"
                            )

                            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                items(categories) { (cat, label) ->
                                    val isSelected = selectedReportCategory == cat
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { selectedReportCategory = cat },
                                        label = { Text(label, fontSize = 11.sp) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = colors.primary,
                                            selectedLabelColor = Color.White
                                        )
                                    )
                                }
                            }

                            OutlinedTextField(
                                value = reportVoiceNotes,
                                onValueChange = { reportVoiceNotes = it },
                                label = { Text("Short note / statement (Optional)", fontSize = 12.sp) },
                                placeholder = { Text("What happened? e.g. An unknown person followed me near bus stop...", fontSize = 11.sp) },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth(),
                                maxLines = 3
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                showReportCategoryDialog = false
                                savedEvidence?.let { ev ->
                                    onCreateComplaintWithEvidence(
                                        ev,
                                        selectedReportCategory,
                                        locationDescription,
                                        locationLat,
                                        locationLng,
                                        reportVoiceNotes
                                    )
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = colors.primary)
                        ) {
                            Text(DurgaStrings.get("continue_btn", currentLanguage))
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showReportCategoryDialog = false }) {
                            Text(DurgaStrings.get("capture_delete_btn", currentLanguage))
                        }
                    }
                )
            }
        }
    }
}
