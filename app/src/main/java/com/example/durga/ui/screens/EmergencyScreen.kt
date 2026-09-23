package com.example.durga.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.durga.i18n.DurgaLanguage
import com.example.durga.i18n.DurgaStrings
import com.example.durga.model.OfficialServiceEntity
import com.example.durga.ui.components.DurgaTopBar
import com.example.durga.ui.components.EmergencyCallCard
import com.example.ui.theme.DurgaBackgroundLight
import com.example.ui.theme.DurgaBlueAction
import com.example.ui.theme.DurgaBorderLight
import com.example.ui.theme.DurgaEmergencyRed
import com.example.ui.theme.DurgaEmergencyRedLight
import com.example.ui.theme.DurgaPrimaryNavy
import com.example.ui.theme.DurgaSectionSoft
import com.example.ui.theme.DurgaSuccessGreen
import com.example.ui.theme.DurgaSuccessLight
import com.example.ui.theme.DurgaSurfaceWhite
import com.example.ui.theme.DurgaTextNavy
import com.example.ui.theme.DurgaTextSecondaryMuted

@Composable
fun EmergencyScreen(
    currentLanguage: DurgaLanguage,
    officialServices: List<OfficialServiceEntity>,
    onBack: () -> Unit,
    onCallNumber: (String) -> Unit,
    onLanguageChange: (DurgaLanguage) -> Unit,
    onQuickExit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedCategory by remember { mutableStateOf("ALL") }

    val categories = listOf("ALL", "Police", "Women", "Child", "Medical", "Cyber")

    val filteredServices = if (selectedCategory == "ALL") {
        officialServices
    } else {
        officialServices.filter { it.category.equals(selectedCategory, ignoreCase = true) }
    }

    Scaffold(
        topBar = {
            DurgaTopBar(
                title = DurgaStrings.get("emergency_help", currentLanguage),
                subtitle = "Official 24/7 Verified Helplines",
                currentLang = currentLanguage,
                onBack = onBack,
                onLanguageChange = onLanguageChange,
                onQuickExit = onQuickExit
            )
        },
        containerColor = DurgaBackgroundLight,
        modifier = modifier
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Immediate 112 Primary Emergency Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = DurgaEmergencyRedLight),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, DurgaEmergencyRed)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .background(DurgaEmergencyRed, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Shield,
                                        contentDescription = "Emergency",
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "112 National Emergency",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = DurgaEmergencyRed,
                                            fontSize = 17.sp
                                        )
                                    )
                                    Text(
                                        text = "Immediate police, fire & medical dispatch",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = DurgaTextSecondaryMuted,
                                            fontSize = 12.sp
                                        )
                                    )
                                }
                            }

                            Button(
                                onClick = { onCallNumber("112") },
                                colors = ButtonDefaults.buttonColors(containerColor = DurgaEmergencyRed),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                                modifier = Modifier.minimumInteractiveComponentSize()
                            ) {
                                Text("CALL 112", fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(DurgaSurfaceWhite, RoundedCornerShape(6.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.GpsFixed,
                                contentDescription = "GPS",
                                tint = DurgaSuccessGreen,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Emergency responders receive your device coordinates automatically.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = DurgaTextNavy,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }
            }

            // Quick Category Filters
            item {
                Column {
                    Text(
                        text = "Filter by Department",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = DurgaTextNavy,
                            fontSize = 14.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(categories) { cat ->
                            val isSelected = selectedCategory.equals(cat, ignoreCase = true)
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedCategory = cat },
                                label = {
                                    Text(
                                        text = cat,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 13.sp
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = DurgaPrimaryNavy,
                                    selectedLabelColor = Color.White,
                                    containerColor = DurgaSurfaceWhite,
                                    labelColor = DurgaTextNavy
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = isSelected,
                                    borderColor = DurgaBorderLight,
                                    selectedBorderColor = DurgaPrimaryNavy
                                )
                            )
                        }
                    }
                }
            }

            // Service List Cards
            items(filteredServices) { service ->
                EmergencyCallCard(
                    number = service.phone,
                    title = service.serviceName,
                    subtitle = service.description,
                    onCallClick = { onCallNumber(service.phone) }
                )
            }

            // Official Disclaimer Notice
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = DurgaSectionSoft),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DurgaBorderLight)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = "Verified",
                            tint = DurgaSuccessGreen,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "All helplines listed above are verified government and statutory emergency telephone lines in India.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = DurgaTextSecondaryMuted,
                                fontSize = 12.sp
                            )
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
