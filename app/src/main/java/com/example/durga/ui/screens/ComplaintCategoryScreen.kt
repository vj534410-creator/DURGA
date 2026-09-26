package com.example.durga.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.durga.i18n.DurgaLanguage
import com.example.durga.i18n.DurgaStrings
import com.example.durga.model.IncidentCategory
import com.example.durga.ui.components.DurgaBottomNav
import com.example.durga.ui.components.DurgaTopBar
import com.example.ui.theme.LocalDurgaColors

data class CategoryItem(
    val category: IncidentCategory,
    val title: String,
    val examples: String,
    val icon: ImageVector
)

@Composable
fun ComplaintCategoryScreen(
    currentLanguage: DurgaLanguage,
    onSelectCategory: (IncidentCategory) -> Unit,
    onBack: () -> Unit,
    onLanguageChange: (DurgaLanguage) -> Unit,
    onQuickExit: () -> Unit,
    onNavigate: ((String) -> Unit)? = null,
    onOpenFeatureSwitcher: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val categories = listOf(
        CategoryItem(
            category = IncidentCategory.CYBER_ONLINE,
            title = DurgaStrings.get("category_cyber", currentLanguage),
            examples = "Harassment, fake profile, photo/video misuse, blackmail, threats",
            icon = Icons.Default.PhoneAndroid
        ),
        CategoryItem(
            category = IncidentCategory.HOME_DOMESTIC,
            title = DurgaStrings.get("category_domestic", currentLanguage),
            examples = "Domestic violence, abuse, verbal threats, marital cruelty, forced confinement",
            icon = Icons.Default.Home
        ),
        CategoryItem(
            category = IncidentCategory.SCHOOL_COLLEGE,
            title = DurgaStrings.get("category_school", currentLanguage),
            examples = "Bullying, stalking, physical abuse, inappropriate behaviour, ragging",
            icon = Icons.Default.School
        ),
        CategoryItem(
            category = IncidentCategory.WORKPLACE,
            title = DurgaStrings.get("category_workplace", currentLanguage),
            examples = "Sexual harassment (PoSH), workplace bullying, threats, retaliation",
            icon = Icons.Default.Work
        ),
        CategoryItem(
            category = IncidentCategory.PUBLIC_CITY,
            title = DurgaStrings.get("category_public", currentLanguage),
            examples = "Street stalking, eve-teasing, public transport incident, intimidation",
            icon = Icons.Default.LocationCity
        ),
        CategoryItem(
            category = IncidentCategory.CHILD_SAFETY,
            title = DurgaStrings.get("category_child", currentLanguage),
            examples = "Child abuse, exploitation, bullying, someone making a child feel unsafe",
            icon = Icons.Default.ChildCare
        ),
        CategoryItem(
            category = IncidentCategory.FINANCIAL_FRAUD,
            title = DurgaStrings.get("category_fraud", currentLanguage),
            examples = "UPI fraud, QR code scam, sextortion, bank impersonation, suspicious links",
            icon = Icons.Default.CreditCard
        )
    )

    val colors = LocalDurgaColors.current

    Scaffold(
        topBar = {
            DurgaTopBar(
                title = DurgaStrings.get("category_select_title", currentLanguage),
                subtitle = "Step 1 of 7: Select Incident Type",
                currentLang = currentLanguage,
                onBack = onBack,
                onLanguageChange = onLanguageChange,
                onQuickExit = onQuickExit,
                onOpenFeatureSwitcher = onOpenFeatureSwitcher
            )
        },
        bottomBar = {
            if (onNavigate != null) {
                DurgaBottomNav(
                    currentRoute = "complaint",
                    currentLanguage = currentLanguage,
                    onNavigate = onNavigate
                )
            }
        },
        containerColor = colors.background,
        modifier = modifier
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Text(
                    text = "Select what best describes the incident:",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = colors.textSecondary,
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp
                    ),
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }

            items(categories) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectCategory(item.category) }
                        .testTag("cat_card_${item.category.key}"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, colors.outline)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .background(colors.primaryContainer, RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title,
                                tint = colors.primary,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = colors.textPrimary,
                                    fontSize = 15.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = item.examples,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = colors.textSecondary,
                                    fontSize = 12.sp
                                )
                            )
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Select",
                            tint = colors.textMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
