package com.example.durga.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.durga.i18n.DurgaLanguage
import com.example.durga.i18n.DurgaStrings
import com.example.ui.theme.DurgaBackgroundLight
import com.example.ui.theme.DurgaBlueAction
import com.example.ui.theme.DurgaBlueLightBg
import com.example.ui.theme.DurgaBorderLight
import com.example.ui.theme.DurgaPrimaryNavy
import com.example.ui.theme.DurgaSectionSoft
import com.example.ui.theme.DurgaSuccessGreen
import com.example.ui.theme.DurgaSurfaceWhite
import com.example.ui.theme.DurgaTextLightMuted
import com.example.ui.theme.DurgaTextNavy
import com.example.ui.theme.DurgaTextSecondaryMuted

@Composable
fun LanguageSelectionScreen(
    currentLanguage: DurgaLanguage,
    onSelectLanguage: (DurgaLanguage) -> Unit,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DurgaBackgroundLight)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // App Emblem
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(DurgaSectionSoft)
                    .border(1.5.dp, DurgaBorderLight, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_durga_logo),
                    contentDescription = "DURGA Emblem",
                    modifier = Modifier.size(46.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "DURGA",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = DurgaTextNavy,
                    letterSpacing = 2.sp,
                    fontSize = 24.sp
                )
            )

            Text(
                text = "Digital Unified Response & Guidance for All",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = DurgaPrimaryNavy,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "\"${currentLanguage.scriptTagline}\"",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = DurgaTextSecondaryMuted,
                    fontSize = 11.sp
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Zero Account Privacy Notice
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = DurgaSurfaceWhite),
                border = androidx.compose.foundation.BorderStroke(1.dp, DurgaBorderLight),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "Privacy Shield",
                        tint = DurgaPrimaryNavy,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = DurgaStrings.get("no_account_notice", currentLanguage),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = DurgaTextNavy,
                            fontWeight = FontWeight.Medium,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = DurgaStrings.get("select_language", currentLanguage),
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = DurgaTextNavy,
                    fontSize = 14.sp
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Language List
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(DurgaLanguage.entries) { lang ->
                    val isSelected = lang == currentLanguage
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectLanguage(lang) }
                            .testTag("lang_item_${lang.code}"),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) DurgaBlueLightBg else DurgaSurfaceWhite
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) DurgaPrimaryNavy else DurgaBorderLight
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = lang.nativeName,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) DurgaPrimaryNavy else DurgaTextNavy,
                                        fontSize = 14.sp
                                    )
                                )
                                Text(
                                    text = lang.englishName,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = DurgaTextSecondaryMuted,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .background(DurgaPrimaryNavy, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = onContinue,
                colors = ButtonDefaults.buttonColors(containerColor = DurgaPrimaryNavy),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .minimumInteractiveComponentSize()
                    .testTag("continue_to_home_button")
            ) {
                Text(
                    text = DurgaStrings.get("continue_btn", currentLanguage),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 14.sp
                    )
                )
            }
        }
    }
}
