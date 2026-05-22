package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CorporateFare
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.TaskViewModel
import com.example.ui.theme.glowingNeonBorder

@Composable
fun ProfileScreen(
    viewModel: TaskViewModel,
    onNavigateToHome: () -> Unit,
    onNavigateToTasks: () -> Unit,
    onNavigateToAddProject: () -> Unit
) {
    val allTasks by viewModel.allTasks.collectAsState()
    val completedCount = allTasks.count { it.isCompleted || it.status == "COMPLETED" }
    val totalCount = allTasks.size
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()

    // Dynamic styling colors matching Theme definitions
    val textColor = if (isDarkTheme) Color.White else Color(0xFF1E1A3D)
    val subtextColor = if (isDarkTheme) Color(0xFFB5B3C6) else Color(0xFF8B8A99)
    val cardBgColor = MaterialTheme.colorScheme.surface
    val dividerColor = if (isDarkTheme) Color(0xFFD43DFF).copy(alpha = 0.25f) else Color(0xFFF1F1F5)

    Scaffold(
        bottomBar = {
            HomeBottomNavigation(
                currentRoute = "profile",
                onNavClicked = { route ->
                    when (route) {
                        "home" -> onNavigateToHome()
                        "tasks" -> onNavigateToTasks()
                        "add" -> onNavigateToAddProject()
                        "profile" -> { /* No op */ }
                    }
                },
                isDarkTheme = isDarkTheme,
                primaryColor = if (isDarkTheme) Color(0xFFD43DFF) else Color(0xFF5C53FF),
                subtextColor = subtextColor
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("profile_view"),
            contentPadding = PaddingValues(24.dp)
        ) {
            // 1. Center Avatar / Profile Title
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "My Workspace Profile",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = textColor,
                        modifier = Modifier.padding(bottom = 20.dp)
                    )

                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .shadow(8.dp, CircleShape)
                            .clip(CircleShape)
                            .background(if (isDarkTheme) Color(0xFFD43DFF).copy(alpha = 0.2f) else Color(0xFFEEECFF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "GG",
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDarkTheme) Color(0xFFFF007F) else Color(0xFF5C53FF)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "GraceGate",
                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 22.sp),
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )

                    Text(
                        text = "Lead Creative Designer",
                        style = MaterialTheme.typography.bodyMedium,
                        color = subtextColor
                    )
                }
            }

            // 2. Beautiful Theme Configuration Switch Toggle
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                        .glowingNeonBorder(isDarkTheme, shape = RoundedCornerShape(24.dp)),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBgColor),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isDarkTheme) Color(0xFFFF007F).copy(alpha = 0.15f) else Color(0xFFEEECFF)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (isDarkTheme) "🌌" else "☀️",
                                    fontSize = 20.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = "Workspace Theme",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = textColor
                                )
                                Text(
                                    text = if (isDarkTheme) "Midnight Neon Mode" else "Radiant Light Mode",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isDarkTheme) Color(0xFFFFB2D4) else Color(0xFF8B8A99)
                                )
                            }
                        }

                        Switch(
                            checked = isDarkTheme,
                            onCheckedChange = { viewModel.toggleDarkTheme() },
                            modifier = Modifier.testTag("theme_switch"),
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color(0xFFFF007F),
                                checkedTrackColor = Color(0xFFD43DFF).copy(alpha = 0.5f),
                                uncheckedThumbColor = Color(0xFF8B8A99),
                                uncheckedTrackColor = Color(0xFFECEFF1)
                            )
                        )
                    }
                }
            }

            // 3. Analytic Stats row (Real calculations from SQLite!)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(100.dp)
                            .glowingNeonBorder(isDarkTheme, shape = RoundedCornerShape(20.dp)),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = cardBgColor),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "$completedCount",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDarkTheme) Color(0xFFD43DFF) else Color(0xFF5C53FF)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Tasks Cleared",
                                style = MaterialTheme.typography.labelSmall,
                                color = subtextColor
                            )
                        }
                    }

                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(100.dp)
                            .glowingNeonBorder(isDarkTheme, shape = RoundedCornerShape(20.dp)),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = cardBgColor),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "$totalCount",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDarkTheme) Color(0xFFFF007F) else Color(0xFFFB6F8B)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Total Assigned",
                                style = MaterialTheme.typography.labelSmall,
                                color = subtextColor
                            )
                        }
                    }
                }
            }

            // 4. Info Details rows
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .glowingNeonBorder(isDarkTheme, shape = RoundedCornerShape(24.dp)),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBgColor),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        ProfileInfoRow(
                            icon = Icons.Default.Email,
                            label = "Email Address",
                            value = "grace.gate@design.com",
                            isDarkTheme = isDarkTheme,
                            textColor = textColor,
                            subtextColor = subtextColor
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 14.dp), color = dividerColor)
                        ProfileInfoRow(
                            icon = Icons.Default.CorporateFare,
                            label = "Company Team",
                            value = "Uber Eats & Super Shop Squad",
                            isDarkTheme = isDarkTheme,
                            textColor = textColor,
                            subtextColor = subtextColor
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 14.dp), color = dividerColor)
                        ProfileInfoRow(
                            icon = Icons.Default.Groups,
                            label = "Membership Level",
                            value = "Lead Workspace Moderator",
                            isDarkTheme = isDarkTheme,
                            textColor = textColor,
                            subtextColor = subtextColor
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 14.dp), color = dividerColor)
                        ProfileInfoRow(
                            icon = Icons.Default.WorkspacePremium,
                            label = "Productivity Badge",
                            value = "Elite Sprint Planner 🏆",
                            isDarkTheme = isDarkTheme,
                            textColor = textColor,
                            subtextColor = subtextColor
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileInfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    isDarkTheme: Boolean,
    textColor: Color,
    subtextColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(if (isDarkTheme) Color(0xFFD43DFF).copy(alpha = 0.15f) else Color(0xFFEEECFF)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isDarkTheme) Color(0xFFD43DFF) else Color(0xFF5C53FF),
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = subtextColor
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }
    }
}
