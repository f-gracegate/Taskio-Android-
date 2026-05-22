package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ProjectTask
import com.example.ui.TaskViewModel

// Custom local data class for Calendar slider matching screenshot
data class CalendarDay(
    val dateString: String,
    val dayNum: String,
    val weekDay: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(
    viewModel: TaskViewModel,
    onBackClicked: () -> Unit
) {
    val selectedDate by viewModel.selectedDate.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()
    val filteredTasks by viewModel.filteredTasks.collectAsState()

    var selectedTaskForDetails by remember { mutableStateOf<ProjectTask?>(null) }

    if (selectedTaskForDetails != null) {
        ProjectDetailsDialog(
            task = selectedTaskForDetails!!,
            viewModel = viewModel,
            onDismissRequest = { selectedTaskForDetails = null }
        )
    }

    val calendarDays = listOf(
        CalendarDay("2026-05-23", "23", "Fri"),
        CalendarDay("2026-05-24", "24", "Sat"),
        CalendarDay("2026-05-25", "25", "Sun"),
        CalendarDay("2026-05-26", "26", "Mon"),
        CalendarDay("2026-05-27", "27", "Tue")
    )

    val filters = listOf("All", "To do", "In Progress", "Completed")

    Scaffold(
        topBar = {
            TasksHeaderBar(
                onBackClicked = onBackClicked,
                onNotificationClicked = {}
            )
        },
        containerColor = Color(0xFFF9FAFF)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // 1. Calendar Day Strip Horizontal Row
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp, horizontal = 24.dp)
                    .testTag("calendar_scroller"),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items(calendarDays) { day ->
                    val isSelected = day.dateString == selectedDate
                    CalendarDayItem(
                        day = day,
                        isSelected = isSelected,
                        onClick = { viewModel.changeSelectedDate(day.dateString) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 2. Filter chips block
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 12.dp)
            ) {
                items(filters) { filter ->
                    val isSelected = filter == selectedFilter
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.changeSelectedFilter(filter) },
                        label = {
                            Text(
                                text = filter,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF5C53FF),
                            selectedLabelColor = Color.White,
                            containerColor = Color.White,
                            labelColor = Color(0xFF8B8A99)
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            selectedBorderColor = Color.Transparent,
                            disabledSelectedBorderColor = Color.Transparent,
                            borderColor = Color(0xFFFFE0E2)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("filter_chip_$filter")
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 3. Main Tasks Lazy Column list
            if (filteredTasks.isEmpty()) {
                EmptyTasksDisplay()
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .testTag("tasks_list"),
                    contentPadding = PaddingValues(start = 24.dp, end = 24.dp, bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(filteredTasks, key = { it.id }) { task ->
                        TaskListItemComponent(
                            task = task,
                            onToggleComplete = { viewModel.toggleTaskCompletion(task) },
                            onDelete = { viewModel.deleteTask(task) },
                            onCardClick = { selectedTaskForDetails = task }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TasksHeaderBar(
    onBackClicked: () -> Unit,
    onNotificationClicked: () -> Unit
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
            text = "Today's Tasks",
            style = MaterialTheme.typography.titleLarge,
            color = Color(0xFF1E1A3D),
            fontWeight = FontWeight.Bold
        )

        IconButton(
            onClick = onNotificationClicked,
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
fun CalendarDayItem(
    day: CalendarDay,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(54.dp)
            .height(84.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(if (isSelected) Color(0xFF5C53FF) else Color.White)
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp)
            .testTag("calendar_day_${day.dayNum}"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = day.weekDay,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) Color.White.copy(alpha = 0.8f) else Color(0xFF8B8A99)
        )

        Text(
            text = day.dayNum,
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp),
            fontWeight = FontWeight.Bold,
            color = if (isSelected) Color.White else Color(0xFF1E1A3D)
        )
    }
}

@Composable
fun TaskListItemComponent(
    task: ProjectTask,
    onToggleComplete: () -> Unit,
    onDelete: () -> Unit,
    onCardClick: () -> Unit
) {
    val (groupColor, flagText) = when (task.groupName) {
        "Office Project" -> Pair(Color(0xFFFB6F8B), "Office Project")
        "Personal Project" -> Pair(Color(0xFF5C53FF), "Personal Project")
        else -> Pair(Color(0xFFFFAE34), "Daily Study")
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onCardClick)
            .testTag("task_item_${task.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Customizable checkbox status
            IconButton(
                onClick = onToggleComplete,
                modifier = Modifier.testTag("toggle_complete_${task.id}")
            ) {
                Icon(
                    imageVector = if (task.isCompleted) Icons.Filled.CheckCircle else Icons.Outlined.Circle,
                    contentDescription = "Toggle completion",
                    tint = if (task.isCompleted) Color(0xFF5C53FF) else Color(0xFFECEFF1),
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = flagText,
                        style = MaterialTheme.typography.labelSmall,
                        color = groupColor,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(groupColor.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = task.status.replace("_", " "),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = groupColor
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = task.title,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                    ),
                    fontWeight = FontWeight.Bold,
                    color = if (task.isCompleted) Color(0xFF8B8A99) else Color(0xFF1E1A3D)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = "Clock inline logo",
                        tint = Color(0xFF8B8A99),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Scheduled at ${task.time}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF8B8A99)
                    )
                }
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier.testTag("delete_task_${task.id}")
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Delete task icon",
                    tint = Color(0xFFEF9A9A)
                )
            }
        }
    }
}

@Composable
fun EmptyTasksDisplay() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .height(200.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "✨",
                fontSize = 40.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "All Tasks Completed!",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E1A3D)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "You cleared this day out! Have a magnificent break.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF8B8A99)
            )
        }
    }
}
