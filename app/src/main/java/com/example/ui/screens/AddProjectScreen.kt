package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import com.example.ui.theme.glowingNeonBorder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProjectScreen(
    viewModel: TaskViewModel,
    onBackClicked: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()

    // Form inputs state
    var selectedGroup by remember { mutableStateOf("Office Project") }
    var selectedPriority by remember { mutableStateOf("MEDIUM") }
    var projectName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("01 May, 2026") }
    var endDate by remember { mutableStateOf("30 Jun, 2026") }
    var selectedTime by remember { mutableStateOf("09:00 AM") }
    var logoName by remember { mutableStateOf("Grocery shop") }

    val groupOptions = listOf("Office Project", "Personal Project", "Daily Study")
    val logoOptions = listOf("Grocery shop", "Work", "Personal", "Study")

    // Sheet toggle states
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    // Dynamic styling driven by theme states
    val textColor = if (isDarkTheme) Color.White else Color(0xFF1E1A3D)
    val subtextColor = if (isDarkTheme) Color(0xFFB5B3C6) else Color(0xFF8B8A99)
    val cardBgColor = MaterialTheme.colorScheme.surface
    val inputFocusColor = if (isDarkTheme) Color(0xFFD43DFF) else Color(0xFF5C53FF)
    val primaryColor = if (isDarkTheme) Color(0xFFD43DFF) else Color(0xFF5C53FF)

    // Formatted helper utilities
    fun formatMillisToDateString(millis: Long?): String {
        if (millis == null) return "25 May, 2026"
        val sdf = java.text.SimpleDateFormat("dd MMM, yyyy", java.util.Locale.getDefault())
        return sdf.format(java.util.Date(millis))
    }

    fun formatTime(hour: Int, minute: Int): String {
        val adjustedHour = if (hour == 0 || hour == 12) 12 else hour % 12
        val amPm = if (hour < 12) "AM" else "PM"
        return String.format("%02d:%02d %s", adjustedHour, minute, amPm)
    }

    // Interactive Material 3 Monthly Date Selection sheets/dialogs
    if (showStartDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = System.currentTimeMillis()
        )
        DatePickerDialog(
            onDismissRequest = { showStartDatePicker = false },
            confirmButton = {
                androidx.compose.material3.TextButton(
                    onClick = {
                        startDate = formatMillisToDateString(datePickerState.selectedDateMillis)
                        showStartDatePicker = false
                    }
                ) {
                    Text("Select", fontWeight = FontWeight.Bold, color = primaryColor)
                }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(onClick = { showStartDatePicker = false }) {
                    Text("Cancel", color = subtextColor)
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showEndDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = System.currentTimeMillis()
        )
        DatePickerDialog(
            onDismissRequest = { showEndDatePicker = false },
            confirmButton = {
                androidx.compose.material3.TextButton(
                    onClick = {
                        endDate = formatMillisToDateString(datePickerState.selectedDateMillis)
                        showEndDatePicker = false
                    }
                ) {
                    Text("Select", fontWeight = FontWeight.Bold, color = primaryColor)
                }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(onClick = { showEndDatePicker = false }) {
                    Text("Cancel", color = subtextColor)
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // Clock Dialing Interface selection sheet
    if (showTimePicker) {
        val timePickerState = rememberTimePickerState(initialHour = 9, initialMinute = 0)
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                androidx.compose.material3.TextButton(
                    onClick = {
                        selectedTime = formatTime(timePickerState.hour, timePickerState.minute)
                        showTimePicker = false
                    }
                ) {
                    Text("Select", fontWeight = FontWeight.Bold, color = primaryColor)
                }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(onClick = { showTimePicker = false }) {
                    Text("Cancel", color = subtextColor)
                }
            },
            title = {
                Text(
                    text = "Dial Target Time",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(8.dp))
                    androidx.compose.material3.TimePicker(state = timePickerState)
                }
            },
            containerColor = cardBgColor,
            shape = RoundedCornerShape(28.dp)
        )
    }

    Scaffold(
        topBar = {
            AddProjectHeader(
                onBackClicked = onBackClicked,
                onNotificationsClicked = {},
                isDarkTheme = isDarkTheme,
                textColor = textColor
            )
        },
        containerColor = MaterialTheme.colorScheme.background
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
                    color = subtextColor,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    groupOptions.forEach { group ->
                        val isSelected = group == selectedGroup
                        val (groupColor, softBg) = when (group) {
                            "Office Project" -> Pair(Color(0xFFFB6F8B), Color(0xFFFFF1F4))
                            "Personal Project" -> Pair(Color(0xFF5C53FF), Color(0xFFF0EFFF))
                            else -> Pair(Color(0xFFFFAE34), Color(0xFFFFF7EC))
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(54.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isSelected) {
                                        if (isDarkTheme) groupColor.copy(alpha = 0.25f) else softBg
                                    } else {
                                        cardBgColor
                                    }
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) groupColor else if (isDarkTheme) Color(0xFF2E2652) else Color(0xFFEEEEEE),
                                    shape = RoundedCornerShape(12.dp)
                                )
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
                                    color = if (isSelected) groupColor else textColor
                                )
                                if (isSelected) {
                                    Box(
                                        modifier = Modifier
                                            .padding(top = 2.dp)
                                            .size(4.dp)
                                            .clip(CircleShape)
                                            .background(groupColor)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // NEW: Select Task Priority flags styled with beautiful soft colored badges
                Text(
                    text = "Select Task Priority",
                    style = MaterialTheme.typography.labelMedium,
                    color = subtextColor,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    listOf("HIGH", "MEDIUM", "LOW").forEach { priority ->
                        val isSelected = priority == selectedPriority
                        val (badgeCol, softBg) = when (priority) {
                            "HIGH" -> Pair(Color(0xFFFF007F), Color(0xFFFFEBEE))
                            "LOW" -> Pair(Color(0xFF00ADB5), Color(0xFFE0EFFF))
                            else -> Pair(Color(0xFFFFAE34), Color(0xFFFFF7EC))
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(54.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isSelected) {
                                        if (isDarkTheme) badgeCol.copy(alpha = 0.25f) else softBg
                                    } else {
                                        cardBgColor
                                    }
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) badgeCol else if (isDarkTheme) Color(0xFF2E2652) else Color(0xFFEEEEEE),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { selectedPriority = priority }
                                .padding(8.dp)
                                .testTag("priority_option_$priority"),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = priority,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) badgeCol else textColor
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 2. Project Name input
                Text(
                    text = "Project Name",
                    style = MaterialTheme.typography.labelMedium,
                    color = subtextColor,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = projectName,
                    onValueChange = { projectName = it },
                    placeholder = { Text("Enter project name...", color = subtextColor) },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("project_name_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = cardBgColor,
                        unfocusedContainerColor = cardBgColor,
                        focusedTextColor = textColor,
                        unfocusedTextColor = textColor,
                        focusedBorderColor = inputFocusColor,
                        unfocusedBorderColor = if (isDarkTheme) Color(0xFF221A3C) else Color(0xFFE0E0E0)
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                // 3. Description text input
                Text(
                    text = "Description",
                    style = MaterialTheme.typography.labelMedium,
                    color = subtextColor,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = { Text("Describe main scope milestones...", color = subtextColor) },
                    maxLines = 3,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .testTag("description_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = cardBgColor,
                        unfocusedContainerColor = cardBgColor,
                        focusedTextColor = textColor,
                        unfocusedTextColor = textColor,
                        focusedBorderColor = inputFocusColor,
                        unfocusedBorderColor = if (isDarkTheme) Color(0xFF221A3C) else Color(0xFFE0E0E0)
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                // 4. Interactive Bottom Sheet Date Selectors
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Start Date",
                            style = MaterialTheme.typography.labelMedium,
                            color = subtextColor,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(cardBgColor)
                                .border(
                                    width = 1.dp,
                                    color = if (isDarkTheme) Color(0xFF221A3C) else Color(0xFFE0E0E0),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .clickable { showStartDatePicker = true }
                                .padding(horizontal = 16.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CalendarToday,
                                    contentDescription = "Calendar",
                                    tint = primaryColor,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = startDate,
                                    color = textColor,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "End Date",
                            style = MaterialTheme.typography.labelMedium,
                            color = subtextColor,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(cardBgColor)
                                .border(
                                    width = 1.dp,
                                    color = if (isDarkTheme) Color(0xFF221A3C) else Color(0xFFE0E0E0),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .clickable { showEndDatePicker = true }
                                .padding(horizontal = 16.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CalendarToday,
                                    contentDescription = "Calendar",
                                    tint = if (isDarkTheme) Color(0xFFFF007F) else Color(0xFFFB6F8B),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = endDate,
                                    color = textColor,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Interactive Clock dial time picker
                Text(
                    text = "Scheduled Time",
                    style = MaterialTheme.typography.labelMedium,
                    color = subtextColor,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(cardBgColor)
                        .border(
                            width = 1.dp,
                            color = if (isDarkTheme) Color(0xFF221A3C) else Color(0xFFE0E0E0),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clickable { showTimePicker = true }
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = "Time",
                            tint = primaryColor,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = selectedTime,
                            color = textColor,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 5. Visual Team Logo Segment Option (Styled mockup style card)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .glowingNeonBorder(isDarkTheme, shape = RoundedCornerShape(20.dp)),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBgColor),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(primaryColor.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                val em = when (logoName) {
                                    "Grocery shop" -> "💼"
                                    "Work" -> "📦"
                                    "Personal" -> "🏠"
                                    else -> "📚"
                                }
                                Text(text = em, fontSize = 20.sp)
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = "Branding Icon",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = textColor
                                )
                                Text(
                                    text = logoName,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = subtextColor
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isDarkTheme) Color(0xFFD43DFF).copy(alpha = 0.2f) else Color(0xFFEEECFF))
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
                                color = primaryColor,
                                fontSize = 12.sp
                            )
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
                            logoName = logoName,
                            priority = selectedPriority
                        )
                        Toast.makeText(context, "Project Added successfully! 🎉", Toast.LENGTH_SHORT).show()
                        onBackClicked() // Go back home
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = primaryColor,
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
    onNotificationsClicked: () -> Unit,
    isDarkTheme: Boolean,
    textColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
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
                tint = textColor
            )
        }

        Text(
            text = "Add Project",
            style = MaterialTheme.typography.titleLarge,
            color = textColor,
            fontWeight = FontWeight.Bold
        )

        IconButton(
            onClick = onNotificationsClicked,
            modifier = Modifier
                .size(40.dp)
                .background(MaterialTheme.colorScheme.surface, CircleShape)
                .clip(CircleShape)
                .border(
                    width = 1.dp,
                    color = if (isDarkTheme) Color(0xFFD43DFF).copy(alpha = 0.2f) else Color(0xFFEEEEEE),
                    shape = CircleShape
                )
        ) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "Notifications",
                tint = textColor,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
