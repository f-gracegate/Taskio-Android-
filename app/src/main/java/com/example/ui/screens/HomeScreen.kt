package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.FolderOpen
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.TaskViewModel
import com.example.ui.TaskGroupStats
import com.example.data.ProjectTask
import com.example.ui.theme.glowingNeonBorder

@Composable
fun HomeScreen(
    viewModel: TaskViewModel,
    onNavigateToTasks: () -> Unit,
    onNavigateToAddProject: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val todayPercentage by viewModel.todayCompletionPercentage.collectAsState()
    val inProgressTasks by viewModel.inProgressTasks.collectAsState()
    val groupStatsList by viewModel.taskGroupStats.collectAsState()
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()

    var selectedTaskForDetails by remember { mutableStateOf<ProjectTask?>(null) }

    if (selectedTaskForDetails != null) {
        ProjectDetailsDialog(
            task = selectedTaskForDetails!!,
            viewModel = viewModel,
            onDismissRequest = { selectedTaskForDetails = null }
        )
    }

    // Dynamic styling values matching design specifications
    val textColor = if (isDarkTheme) Color.White else Color(0xFF1E1A3D)
    val subtextColor = if (isDarkTheme) Color(0xFFB5B3C6) else Color(0xFF8B8A99)
    val primaryColor = if (isDarkTheme) Color(0xFFD43DFF) else Color(0xFF5C53FF)

    Scaffold(
        bottomBar = {
            HomeBottomNavigation(
                currentRoute = "home",
                onNavClicked = { route ->
                    when (route) {
                        "home" -> { /* No op */ }
                        "tasks" -> onNavigateToTasks()
                        "add" -> onNavigateToAddProject()
                        "profile" -> onNavigateToProfile()
                    }
                },
                isDarkTheme = isDarkTheme,
                primaryColor = primaryColor,
                subtextColor = subtextColor
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(top = 16.dp, bottom = 48.dp)
        ) {
            // 1. Header Row
            item {
                HomeHeaderSegment(isDarkTheme = isDarkTheme, textColor = textColor, subtextColor = subtextColor)
            }

            // 2. Today Task Progress Card
            item {
                TodayProgressCardComponent(
                    completionPercentage = todayPercentage,
                    onViewClicked = onNavigateToTasks,
                    isDarkTheme = isDarkTheme,
                    primaryColor = primaryColor
                )
            }

            // 3. In Progress section title
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "In Progress",
                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 20.sp),
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                }
            }

            // 4. Horizontal In Progress Scroll
            item {
                if (inProgressTasks.isEmpty()) {
                    EmptyInProgressPlaceholder(isDarkTheme = isDarkTheme, subtextColor = subtextColor)
                } else {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        items(inProgressTasks) { task ->
                            InProgressCardMetric(
                                task = task,
                                onClick = { selectedTaskForDetails = task },
                                isDarkTheme = isDarkTheme,
                                textColor = textColor,
                                subtextColor = subtextColor
                            )
                        }
                    }
                }
            }

            // 5. Task Groups Section
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Task Groups",
                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 20.sp),
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                }
            }

            // 6. Vertical list of Task Groups
            items(groupStatsList) { groupStats ->
                TaskGroupProgressItem(
                    groupStats = groupStats,
                    onClick = onNavigateToTasks,
                    isDarkTheme = isDarkTheme,
                    textColor = textColor,
                    subtextColor = subtextColor
                )
            }
        }
    }
}

@Composable
fun HomeHeaderSegment(isDarkTheme: Boolean, textColor: Color, subtextColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(if (isDarkTheme) Color(0xFFD43DFF).copy(alpha = 0.15f) else Color(0xFFEEECFF)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "GG",
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkTheme) Color(0xFFFF007F) else Color(0xFF5C53FF),
                    fontSize = 18.sp
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Hello!",
                    style = MaterialTheme.typography.labelSmall,
                    color = subtextColor
                )
                Text(
                    text = "GraceGate",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
            }
        }

        IconButton(
            onClick = { /* No-op notifications triggers */ },
            modifier = Modifier
                .size(44.dp)
                .background(MaterialTheme.colorScheme.surface, CircleShape)
                .border(
                    width = 1.dp,
                    color = if (isDarkTheme) Color(0xFFD43DFF).copy(alpha = 0.2f) else Color(0xFFEEEEEE),
                    shape = CircleShape
                )
                .testTag("notification_button")
        ) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "Notifications",
                tint = textColor
            )
        }
    }
}

@Composable
fun TodayProgressCardComponent(
    completionPercentage: Int,
    onViewClicked: () -> Unit,
    isDarkTheme: Boolean,
    primaryColor: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp)
            .glowingNeonBorder(isDarkTheme, shape = RoundedCornerShape(24.dp))
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = if (isDarkTheme) Color(0xFFFF007F).copy(alpha = 0.2f) else Color(0x335C53FF),
                spotColor = if (isDarkTheme) Color(0xFFD43DFF).copy(alpha = 0.2f) else Color(0x335C53FF)
            )
            .testTag("today_progress_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.linearGradient(
                        colors = if (isDarkTheme) {
                            listOf(Color(0xFF8B02BB), Color(0xFFFF007F))
                        } else {
                            listOf(Color(0xFF5c53FF), Color(0xFF7A70FF))
                        }
                    )
                )
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Your today's task\nalmost done!",
                        style = MaterialTheme.typography.titleMedium.copy(lineHeight = 24.sp),
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onViewClicked,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = if (isDarkTheme) Color(0xFF8B02BB) else Color(0xFF5C53FF)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        modifier = Modifier.testTag("view_task_button")
                    ) {
                        Text(
                            text = "View Task",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Circular Progress Graphic
                Box(
                    modifier = Modifier.size(90.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val animatePercent by animateFloatAsState(
                        targetValue = completionPercentage.toFloat() / 100f,
                        animationSpec = tween(durationMillis = 800), label = "circle"
                    )

                    Canvas(modifier = Modifier.fillMaxSize()) {
                        // Background soft loop
                        drawCircle(
                            color = Color(0x33FFFFFF),
                            radius = size.minDimension / 2,
                            style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                        )
                        // Active completion rate arc
                        drawArc(
                            color = Color.White,
                            startAngle = -90f,
                            sweepAngle = animatePercent * 360f,
                            useCenter = false,
                            style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }

                    Text(
                        text = "$completionPercentage%",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun InProgressCardMetric(
    task: ProjectTask,
    onClick: () -> Unit,
    isDarkTheme: Boolean,
    textColor: Color,
    subtextColor: Color
) {
    val (borderColor, brandBg) = when (task.groupName) {
        "Office Project" -> Pair(Color(0xFFFB6F8B), if (isDarkTheme) Color(0xFF130F26) else Color(0xFFFFF1F4))
        "Personal Project" -> Pair(Color(0xFF5C53FF), if (isDarkTheme) Color(0xFF130F26) else Color(0xFFF0EFFF))
        else -> Pair(Color(0xFFFFAE34), if (isDarkTheme) Color(0xFF130F26) else Color(0xFFFFF7EC))
    }

    Card(
        modifier = Modifier
            .width(260.dp)
            .height(130.dp)
            .glowingNeonBorder(isDarkTheme, shape = RoundedCornerShape(20.dp))
            .border(1.dp, borderColor.copy(alpha = if (isDarkTheme) 0.5f else 0.3f), RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .testTag("in_progress_card_${task.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = brandBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = task.groupName,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isDarkTheme && task.groupName == "Personal Project") Color(0xFFD43DFF) else borderColor,
                    fontWeight = FontWeight.Bold
                )

                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(if (isDarkTheme && task.groupName == "Personal Project") Color(0xFFD43DFF) else borderColor)
                )
            }

            Text(
                text = task.title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = textColor,
                maxLines = 1
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = "Clock inline logo",
                    tint = subtextColor,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${task.time}  •  ${task.dateString}",
                    style = MaterialTheme.typography.labelSmall,
                    color = subtextColor
                )
            }
        }
    }
}

@Composable
fun TaskGroupProgressItem(
    groupStats: TaskGroupStats,
    onClick: () -> Unit,
    isDarkTheme: Boolean,
    textColor: Color,
    subtextColor: Color
) {
    val groupColor = Color(android.graphics.Color.parseColor(groupStats.colorHex))
    val groupBg = when (groupStats.groupName) {
        "Office Project" -> if (isDarkTheme) Color(0xFFFF007F).copy(alpha = 0.15f) else Color(0xFFFFF1F4)
        "Personal Project" -> if (isDarkTheme) Color(0xFFD43DFF).copy(alpha = 0.15f) else Color(0xFFF0EFFF)
        else -> if (isDarkTheme) Color(0xFFFFAE34).copy(alpha = 0.15f) else Color(0xFFFFF7EC)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp)
            .glowingNeonBorder(isDarkTheme, shape = RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .testTag("task_group_item_${groupStats.groupName}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                // Circle Logo
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(groupBg),
                    contentAlignment = Alignment.Center
                ) {
                    val initialText = when (groupStats.groupName) {
                        "Office Project" -> "💼"
                        "Personal Project" -> "🏠"
                        else -> "📚"
                    }
                    Text(text = initialText, fontSize = 22.sp)
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = groupStats.groupName,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${groupStats.totalTasks} Tasks",
                        style = MaterialTheme.typography.labelMedium,
                        color = subtextColor
                    )
                }
            }

            // Circular progress on the right side
            Box(
                modifier = Modifier.size(50.dp),
                contentAlignment = Alignment.Center
            ) {
                val animatedProgress by animateFloatAsState(
                    targetValue = groupStats.completionPercentage.toFloat() / 100f,
                    animationSpec = tween(durationMillis = 600), label = "groupCircle"
                )

                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(
                        color = if (isDarkTheme) Color(0xFF1E173C) else Color(0xFFECEFF1),
                        radius = size.minDimension / 2,
                        style = Stroke(width = 4.dp.toPx())
                    )
                    drawArc(
                        color = if (isDarkTheme && groupStats.groupName == "Personal Project") Color(0xFFD43DFF) else groupColor,
                        startAngle = -90f,
                        sweepAngle = animatedProgress * 360f,
                        useCenter = false,
                        style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                Text(
                    text = "${groupStats.completionPercentage}%",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
            }
        }
    }
}

@Composable
fun EmptyInProgressPlaceholder(isDarkTheme: Boolean, subtextColor: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .height(120.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(if (isDarkTheme) Color(0xFF130F26) else Color(0xFFF2F3F8)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "No Tasks In Progress Right Now!",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = subtextColor
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Tap + to start a project",
                style = MaterialTheme.typography.labelMedium,
                color = subtextColor
            )
        }
    }
}

@Composable
fun HomeBottomNavigation(
    currentRoute: String,
    onNavClicked: (String) -> Unit,
    isDarkTheme: Boolean,
    primaryColor: Color,
    subtextColor: Color
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(start = 24.dp, end = 24.dp, bottom = 16.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .glowingNeonBorder(isDarkTheme, shape = RoundedCornerShape(28.dp))
                .shadow(
                    elevation = 16.dp,
                    shape = RoundedCornerShape(28.dp),
                    ambientColor = if (isDarkTheme) Color(0xFFFF007F).copy(alpha = 0.1f) else Color(0x22110D2C),
                    spotColor = if (isDarkTheme) Color(0xFFD43DFF).copy(alpha = 0.1f) else Color(0x22110D2C)
                )
                .clip(RoundedCornerShape(28.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Home Menu Icon
            BottomNavItemComponent(
                isSelected = currentRoute == "home",
                activeIcon = Icons.Filled.Home,
                inactiveIcon = Icons.Outlined.Home,
                label = "Home",
                onClick = { onNavClicked("home") },
                primaryColor = primaryColor,
                subtextColor = subtextColor
            )

            // Tasks/Calendar Icon
            BottomNavItemComponent(
                isSelected = currentRoute == "tasks",
                activeIcon = Icons.Filled.CalendarMonth,
                inactiveIcon = Icons.Outlined.CalendarMonth,
                label = "Calendar",
                onClick = { onNavClicked("tasks") },
                primaryColor = primaryColor,
                subtextColor = subtextColor
            )

            // Dynamic Action FAB
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            colors = if (isDarkTheme) {
                                listOf(Color(0xFFFF007F), Color(0xFFD43DFF))
                            } else {
                                listOf(Color(0xFF5C53FF), Color(0xFF4238FF))
                            }
                        )
                    )
                    .clickable { onNavClicked("add") }
                    .testTag("fab_add_task"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Floating plus add",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }

            // Folders Icon
            BottomNavItemComponent(
                isSelected = false,
                activeIcon = Icons.Filled.FolderOpen,
                inactiveIcon = Icons.Outlined.FolderOpen,
                label = "Files",
                onClick = { onNavClicked("tasks") },
                primaryColor = primaryColor,
                subtextColor = subtextColor
            )

            // Profile Icon
            BottomNavItemComponent(
                isSelected = currentRoute == "profile",
                activeIcon = Icons.Filled.Person,
                inactiveIcon = Icons.Outlined.Person,
                label = "Profile",
                onClick = { onNavClicked("profile") },
                primaryColor = primaryColor,
                subtextColor = subtextColor
            )
        }
    }
}

@Composable
fun BottomNavItemComponent(
    isSelected: Boolean,
    activeIcon: androidx.compose.ui.graphics.vector.ImageVector,
    inactiveIcon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    primaryColor: Color,
    subtextColor: Color
) {
    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = if (isSelected) activeIcon else inactiveIcon,
            contentDescription = label,
            tint = if (isSelected) primaryColor else subtextColor,
            modifier = Modifier.size(24.dp)
        )
        if (isSelected) {
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .clip(CircleShape)
                    .background(primaryColor)
            )
        }
    }
}
