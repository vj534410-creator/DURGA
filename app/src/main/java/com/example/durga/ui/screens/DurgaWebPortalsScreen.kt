package com.example.durga.ui.screens

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.durga.i18n.DurgaLanguage
import com.example.durga.ui.components.DurgaTopBar
import com.example.ui.theme.DurgaBackgroundLight
import com.example.ui.theme.DurgaBorderLight
import com.example.ui.theme.DurgaEmergencyRed
import com.example.ui.theme.DurgaPrimaryNavy
import com.example.ui.theme.DurgaSuccessGreen
import com.example.ui.theme.DurgaSurfaceWhite
import com.example.ui.theme.DurgaTextNavy
import com.example.ui.theme.DurgaTextSecondaryMuted

enum class PortalTab(val label: String, val path: String) {
    OFFICER("👮 Police Desk", "/officer"),
    ADMIN("📊 Central Admin", "/admin"),
    HUB("🏠 Portal Hub", "/"),
    HEALTH("⚡ API Health", "/health")
}

enum class PortalHost(val label: String, val baseUrl: String) {
    IN_APP_GATEWAY("⚡ In-App Direct Gateway", "http://10.0.2.2:3000"),
    CLOUD_DEV("🌐 Cloud Live URL", "https://ais-dev-liwlhnbrwlgr2papvvvxvj-123493668199.asia-southeast1.run.app")
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun DurgaWebPortalsScreen(
    currentLanguage: DurgaLanguage,
    onBack: () -> Unit,
    onLanguageChange: (DurgaLanguage) -> Unit,
    onQuickExit: () -> Unit,
    onOpenFeatureSwitcher: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(PortalTab.OFFICER) }
    var selectedHost by remember { mutableStateOf(PortalHost.IN_APP_GATEWAY) }
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }
    var loadProgress by remember { mutableFloatStateOf(0f) }
    var isLoading by remember { mutableStateOf(true) }
    var loadError by remember { mutableStateOf<String?>(null) }
    var showCredentialsSheet by remember { mutableStateOf(false) }

    val currentUrl = "${selectedHost.baseUrl}${selectedTab.path}"

    // Intercept hardware back button to navigate backwards in WebView history
    BackHandler {
        if (webViewInstance?.canGoBack() == true) {
            webViewInstance?.goBack()
        } else {
            onBack()
        }
    }

    Scaffold(
        topBar = {
            DurgaTopBar(
                title = when (currentLanguage) {
                    DurgaLanguage.GUJARATI -> "પોલીસ અને એડમિન વેબ પોર્ટલ"
                    DurgaLanguage.HINDI -> "पुलिस एवं एडमिन वेब पोर्टल"
                    DurgaLanguage.ENGLISH -> "Police & Admin Web Portals"
                },
                subtitle = when (currentLanguage) {
                    DurgaLanguage.GUJARATI -> "DURGA સેન્ટ્રલ ડેટાબેઝ સાથે સીધું જોડાયેલ"
                    DurgaLanguage.HINDI -> "DURGA सेंट्रल डेटाबेस से सीधा जुड़ा हुआ"
                    DurgaLanguage.ENGLISH -> "Directly connected to DURGA Central Cloud"
                },
                currentLang = currentLanguage,
                onBack = onBack,
                onLanguageChange = onLanguageChange,
                onQuickExit = onQuickExit,
                onOpenFeatureSwitcher = onOpenFeatureSwitcher
            )
        },
        containerColor = DurgaBackgroundLight,
        modifier = modifier
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // 1. Host and Credentials Bar
            Surface(
                color = DurgaSurfaceWhite,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    // Host Selector Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PortalHost.entries.forEach { host ->
                            val isSelected = selectedHost == host
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    if (selectedHost != host) {
                                        selectedHost = host
                                        loadError = null
                                        webViewInstance?.loadUrl("${host.baseUrl}${selectedTab.path}")
                                    }
                                },
                                label = { Text(host.label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = DurgaPrimaryNavy,
                                    selectedLabelColor = Color.White,
                                    containerColor = DurgaBackgroundLight,
                                    labelColor = DurgaTextNavy
                                )
                            )
                        }

                        // Credentials Toggle Button
                        Button(
                            onClick = { showCredentialsSheet = !showCredentialsSheet },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (showCredentialsSheet) DurgaEmergencyRed else DurgaBackgroundLight
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Key,
                                contentDescription = null,
                                tint = if (showCredentialsSheet) Color.White else DurgaPrimaryNavy,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (showCredentialsSheet) "Hide Logins" else "🔑 Demo Logins",
                                fontSize = 11.sp,
                                color = if (showCredentialsSheet) Color.White else DurgaPrimaryNavy,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Quick Credentials Expandable Card
                    AnimatedVisibility(visible = showCredentialsSheet) {
                        Card(
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = DurgaBackgroundLight),
                            border = androidx.compose.foundation.BorderStroke(1.dp, DurgaBorderLight),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "Tap to Copy Credentials for One-Click Login:",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = DurgaTextNavy
                                    )
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    // Police Officer Chip
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = DurgaSurfaceWhite),
                                        shape = RoundedCornerShape(6.dp),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, DurgaBorderLight),
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable {
                                                copyToClipboard(context, "Badge: POL-GJ-4029 | Pass: officer123", "Police Credentials")
                                            }
                                    ) {
                                        Column(modifier = Modifier.padding(8.dp)) {
                                            Text("👮 Police Officer", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DurgaPrimaryNavy)
                                            Text("Badge: POL-GJ-4029", fontSize = 10.sp, color = DurgaTextSecondaryMuted, fontFamily = FontFamily.Monospace)
                                            Text("Pass: officer123", fontSize = 10.sp, color = DurgaTextSecondaryMuted, fontFamily = FontFamily.Monospace)
                                        }
                                    }

                                    // Admin Chip
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = DurgaSurfaceWhite),
                                        shape = RoundedCornerShape(6.dp),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, DurgaBorderLight),
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable {
                                                copyToClipboard(context, "admin@durga.gov.in | durgaAdmin2026!", "Admin Credentials")
                                            }
                                    ) {
                                        Column(modifier = Modifier.padding(8.dp)) {
                                            Text("📊 Administrator", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DurgaEmergencyRed)
                                            Text("admin@durga.gov.in", fontSize = 10.sp, color = DurgaTextSecondaryMuted, fontFamily = FontFamily.Monospace)
                                            Text("Pass: durgaAdmin2026!", fontSize = 10.sp, color = DurgaTextSecondaryMuted, fontFamily = FontFamily.Monospace)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // 2. Portal Sub-Route Tabs
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        PortalTab.entries.forEach { tab ->
                            val isTabSelected = selectedTab == tab
                            FilterChip(
                                selected = isTabSelected,
                                onClick = {
                                    selectedTab = tab
                                    loadError = null
                                    webViewInstance?.loadUrl("${selectedHost.baseUrl}${tab.path}")
                                },
                                label = { Text(tab.label, fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = DurgaPrimaryNavy,
                                    selectedLabelColor = Color.White,
                                    containerColor = DurgaSurfaceWhite,
                                    labelColor = DurgaTextNavy
                                )
                            )
                        }
                    }
                }
            }

            // Loading Progress Bar
            if (isLoading && loadProgress < 1f) {
                LinearProgressIndicator(
                    progress = { loadProgress },
                    color = DurgaEmergencyRed,
                    trackColor = DurgaBorderLight,
                    modifier = Modifier.fillMaxWidth().height(3.dp)
                )
            }

            // 3. Main In-App WebView Box
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                AndroidView(
                    factory = { ctx ->
                        WebView(ctx).apply {
                            settings.apply {
                                javaScriptEnabled = true
                                domStorageEnabled = true
                                loadWithOverviewMode = true
                                useWideViewPort = true
                                setSupportZoom(true)
                                builtInZoomControls = true
                                displayZoomControls = false
                                cacheMode = WebSettings.LOAD_DEFAULT
                                mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                            }

                            webViewClient = object : WebViewClient() {
                                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                    super.onPageStarted(view, url, favicon)
                                    isLoading = true
                                    loadError = null
                                }

                                override fun onPageFinished(view: WebView?, url: String?) {
                                    super.onPageFinished(view, url)
                                    isLoading = false
                                }

                                override fun onReceivedError(
                                    view: WebView?,
                                    request: WebResourceRequest?,
                                    error: WebResourceError?
                                ) {
                                    super.onReceivedError(view, request, error)
                                    if (request?.isForMainFrame == true) {
                                        isLoading = false
                                        loadError = error?.description?.toString() ?: "Connection failed"
                                    }
                                }

                                override fun shouldOverrideUrlLoading(
                                    view: WebView?,
                                    request: WebResourceRequest?
                                ): Boolean {
                                    val url = request?.url?.toString() ?: return false
                                    // If URL is within app domain or 10.0.2.2 or localhost, load in WebView directly
                                    return if (url.contains("10.0.2.2") || url.contains("localhost") || url.contains("run.app")) {
                                        false // Allow WebView to load directly without external app redirect!
                                    } else {
                                        try {
                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                            ctx.startActivity(intent)
                                            true
                                        } catch (_: Exception) {
                                            false
                                        }
                                    }
                                }
                            }

                            webChromeClient = object : WebChromeClient() {
                                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                                    super.onProgressChanged(view, newProgress)
                                    loadProgress = newProgress / 100f
                                    if (newProgress >= 100) {
                                        isLoading = false
                                    }
                                }
                            }

                            loadUrl(currentUrl)
                            webViewInstance = this
                        }
                    },
                    update = { view ->
                        // If current URL changed externally
                        if (view.url?.contains(selectedTab.path) != true && !isLoading) {
                            view.loadUrl(currentUrl)
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )

                // Error View if network fail
                if (loadError != null) {
                    Surface(
                        color = DurgaSurfaceWhite,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.WifiOff,
                                contentDescription = null,
                                tint = DurgaEmergencyRed,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "Unable to Reach Portal",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = DurgaTextNavy
                                )
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Address: $currentUrl\nDetails: $loadError",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = DurgaTextSecondaryMuted,
                                    fontFamily = FontFamily.Monospace
                                )
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Button(
                                    onClick = {
                                        loadError = null
                                        isLoading = true
                                        webViewInstance?.reload()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = DurgaPrimaryNavy)
                                ) {
                                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Retry Connection")
                                }

                                Button(
                                    onClick = {
                                        // Switch host
                                        selectedHost = if (selectedHost == PortalHost.IN_APP_GATEWAY) PortalHost.CLOUD_DEV else PortalHost.IN_APP_GATEWAY
                                        loadError = null
                                        webViewInstance?.loadUrl("${selectedHost.baseUrl}${selectedTab.path}")
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = DurgaEmergencyRed)
                                ) {
                                    Text("Switch Gateway")
                                }
                            }
                        }
                    }
                }
            }

            // 4. Bottom Navigation & Action Controls Bar
            Surface(
                color = DurgaSurfaceWhite,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Back / Forward inside WebView
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { webViewInstance?.goBack() },
                            enabled = webViewInstance?.canGoBack() == true
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", modifier = Modifier.size(20.dp))
                        }
                        IconButton(
                            onClick = { webViewInstance?.goForward() },
                            enabled = webViewInstance?.canGoForward() == true
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Forward", modifier = Modifier.size(20.dp))
                        }
                        IconButton(
                            onClick = {
                                loadError = null
                                isLoading = true
                                webViewInstance?.reload()
                            }
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = "Reload", modifier = Modifier.size(20.dp))
                        }
                    }

                    // External Browser & Copy Link Buttons
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                copyToClipboard(context, currentUrl, "Portal URL")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = DurgaBackgroundLight),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, tint = DurgaPrimaryNavy, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Copy Link", fontSize = 11.sp, color = DurgaTextNavy, fontWeight = FontWeight.SemiBold)
                        }

                        Button(
                            onClick = {
                                openInExternalBrowser(context, currentUrl)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = DurgaPrimaryNavy),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Icon(Icons.Default.OpenInBrowser, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Open in Chrome", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

private fun copyToClipboard(context: Context, text: String, label: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText(label, text)
    clipboard.setPrimaryClip(clip)
    Toast.makeText(context, "$label copied to clipboard!", Toast.LENGTH_SHORT).show()
}

private fun openInExternalBrowser(context: Context, url: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "Could not open browser: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}
