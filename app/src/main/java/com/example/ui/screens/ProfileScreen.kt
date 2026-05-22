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
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.CorporateFare
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
                }
            )
        },
        containerColor = Color(0xFFF9FAFF)
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
                        .padding(vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "My Workspace Profile",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E1A3D),
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .shadow(8.dp, CircleShape)
                            .clip(CircleShape)
                            .background(Color(0xFFEEECFF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "GG",
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF5C53FF)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "GraceGate",
                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 22.sp),
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E1A3D)
                    )

                    Text(
                        text = "Lead Creative Designer",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF8B8A99)
                    )
                }
            }

            // 2. Analytic Stats row (Real calculations from SQLite!)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(100.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
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
                                color = Color(0xFF5C53FF)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Tasks Cleared",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF8B8A99)
                            )
                        }
                    }

                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(100.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
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
                                color = Color(0xFFFB6F8B)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Total Assigned",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF8B8A99)
                            )
                        }
                    }
                }
            }

            // 3. Info Details rows
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        ProfileInfoRow(
                            icon = Icons.Default.Email,
                            label = "Email Address",
                            value = "grace.gate@design.com"
                        )
                        Divider(modifier = Modifier.padding(vertical = 14.dp), color = Color(0xFFF1F1F5))
                        ProfileInfoRow(
                            icon = Icons.Default.CorporateFare,
                            label = "Company Team",
                            value = "Uber Eats & Super Shop Squad"
                        )
                        Divider(modifier = Modifier.padding(vertical = 14.dp), color = Color(0xFFF1F1F5))
                        ProfileInfoRow(
                            icon = Icons.Default.Groups,
                            label = "Membership Level",
                            value = "Lead Workspace Moderator"
                        )
                        Divider(modifier = Modifier.padding(vertical = 14.dp), color = Color(0xFFF1F1F5))
                        ProfileInfoRow(
                            icon = Icons.Default.WorkspacePremium,
                            label = "Productivity Badge",
                            value = "Elite Sprint Planner 🏆"
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
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFEEECFF)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color(0xFF5C53FF),
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF8B8A99)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E1A3D)
            )
        }
    }
}
