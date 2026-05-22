package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.TaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProjectScreen(
    viewModel: TaskViewModel,
    onBackClicked: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Form inputs state
    var selectedGroup by remember { mutableStateOf("Office Project") }
    var projectName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("01 May, 2026") }
    var endDate by remember { mutableStateOf("30 Jun, 2026") }
    var selectedTime by remember { mutableStateOf("09:00 AM") }
    var logoName by remember { mutableStateOf("Grocery shop") }

    val groupOptions = listOf("Office Project", "Personal Project", "Daily Study")
    val logoOptions = listOf("Grocery shop", "Work", "Personal", "Study")

    Scaffold(
        topBar = {
            AddProjectHeader(
                onBackClicked = onBackClicked,
                onNotificationsClicked = {}
            )
        },
        containerColor = Color(0xFFF9FAFF)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(bottom = 90.dp) // Cushion above screen boundaries
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                // 1. Task Group Selector (Custom Cards instead of generic spinner)
                Text(
                    text = "Select Task Group",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFF8B8A99),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    groupOptions.forEach { group ->
                        val isSelected = group == selectedGroup
                        val (primaryColor, softBg) = when (group) {
                            "Office Project" -> Pair(Color(0xFFFB6F8B), Color(0xFFFFF1F4))
                            "Personal Project" -> Pair(Color(0xFF5C53FF), Color(0xFFF0EFFF))
                            else -> Pair(Color(0xFFFFAE34), Color(0xFFFFF7EC))
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(54.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) softBg else Color.White)
                                .clickable { selectedGroup = group }
                                .padding(8.dp)
                                .testTag("group_option_$group"),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = group.substringBefore(" "),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) primaryColor else Color(0xFF1E1A3D)
                                )
                                if (isSelected) {
                                    Box(
                                        modifier = Modifier
                                            .padding(top = 2.dp)
                                            .size(4.dp)
                                            .clip(CircleShape)
                                            .background(primaryColor)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 2. Project Name input
                Text(
                    text = "Project/Task Name",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFF8B8A99),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                OutlinedTextField(
                    value = projectName,
                    onValueChange = { projectName = it },
                    placeholder = { Text("Grocery Shopping App", color = Color(0xFFB0BEC5)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("project_name_input"),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = Color(0xFF5C53FF),
                        unfocusedBorderColor = Color(0xFFE0E0E0),
                        focusedTextColor = Color(0xFF1E1A3D),
                        unfocusedTextColor = Color(0xFF1E1A3D)
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                // 3. Project Description input
                Text(
                    text = "Description",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFF8B8A99),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = {
                        Text(
                            "This application is designed for super shops. Enlist all features perfectly...",
                            color = Color(0xFFB0BEC5),
                            fontSize = 14.sp
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .testTag("project_description_input"),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = Color(0xFF5C53FF),
                        unfocusedBorderColor = Color(0xFFE0E0E0),
                        focusedTextColor = Color(0xFF1E1A3D),
                        unfocusedTextColor = Color(0xFF1E1A3D)
                    ),
                    maxLines = 4
                )

                Spacer(modifier = Modifier.height(20.dp))

                // 4. Date range pickers (Styled as custom text fields for inline picker updates)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Start Date",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color(0xFF8B8A99),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        OutlinedTextField(
                            value = startDate,
                            onValueChange = { startDate = it },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.CalendarToday,
                                    contentDescription = "Calendar",
                                    tint = Color(0xFF5C53FF)
                                )
                            },
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.testTag("start_date_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedBorderColor = Color(0xFF5C53FF),
                                unfocusedBorderColor = Color(0xFFE0E0E0)
                            )
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "End Date",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color(0xFF8B8A99),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        OutlinedTextField(
                            value = endDate,
                            onValueChange = { endDate = it },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.CalendarToday,
                                    contentDescription = "Calendar",
                                    tint = Color(0xFFFB6F8B)
                                )
                            },
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.testTag("end_date_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedBorderColor = Color(0xFF5C53FF),
                                unfocusedBorderColor = Color(0xFFE0E0E0)
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Time picker
                Text(
                    text = "Scheduled Time",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFF8B8A99),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = selectedTime,
                    onValueChange = { selectedTime = it },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("time_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = Color(0xFF5C53FF),
                        unfocusedBorderColor = Color(0xFFE0E0E0)
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                // 5. Select Logo / Logo Name choice row
                Text(
                    text = "Logo / Category Icon Style",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFF8B8A99),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderDefaultsCard()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFFF2F3F8)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    val emoji = when (logoName) {
                                        "Grocery shop" -> "🛒"
                                        "Work" -> "💼"
                                        "Personal" -> "🏠"
                                        else -> "📚"
                                    }
                                    Text(text = emoji, fontSize = 20.sp)
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = logoName,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E1A3D)
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFEEECFF))
                                    .clickable {
                                        // Simple cycle select logo name for interaction
                                        val curIndex = logoOptions.indexOf(logoName)
                                        logoName = logoOptions[(curIndex + 1) % logoOptions.size]
                                    }
                                    .padding(horizontal = 14.dp, vertical = 6.dp)
                                    .testTag("change_logo_button")
                            ) {
                                Text(
                                    text = "Change Logo",
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF5C53FF),
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // 6. Prominent Solid Primary Button "Add Project"
                Button(
                    onClick = {
                        if (projectName.trim().isEmpty()) {
                            Toast.makeText(context, "Please enter a Project Name!", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        // Insert standard task representing this beautiful project
                        viewModel.addProjectTask(
                            title = projectName,
                            description = description,
                            groupName = selectedGroup,
                            time = selectedTime,
                            dateString = "2026-05-25", // Save to May 25 to show on tasks instantly!
                            startDate = startDate,
                            endDate = endDate,
                            logoName = logoName
                        )
                        Toast.makeText(context, "Project Added successfully! 🎉", Toast.LENGTH_SHORT).show()
                        onBackClicked() // Go back home
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF5C53FF),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("add_project_submit_button")
                ) {
                    Text(
                        text = "Add Project",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
fun AddProjectHeader(
    onBackClicked: () -> Unit,
    onNotificationsClicked: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF9FAFF))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBackClicked,
            modifier = Modifier.testTag("back_button")
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Navigate back",
                tint = Color(0xFF1E1A3D)
            )
        }

        Text(
            text = "Add Project",
            style = MaterialTheme.typography.titleLarge,
            color = Color(0xFF1E1A3D),
            fontWeight = FontWeight.Bold
        )

        IconButton(
            onClick = onNotificationsClicked,
            modifier = Modifier
                .size(40.dp)
                .background(Color.White, CircleShape)
                .clip(CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "Notifications",
                tint = Color(0xFF1E1A3D),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun BorderDefaultsCard() = CardDefaults.outlinedCardBorder().copy(
    width = 1.dp,
    brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFEEEEEE))
)
