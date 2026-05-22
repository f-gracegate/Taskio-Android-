package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.ProjectTask
import com.example.ui.TaskViewModel
import com.example.ui.ai.GeminiService
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProjectDetailsDialog(
    task: ProjectTask,
    viewModel: TaskViewModel,
    onDismissRequest: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // AI Coach states
    var isAILoading by remember { mutableStateOf(false) }
    var aiChecklist by remember { mutableStateOf<List<String>?>(null) }
    // Track which generated sub-tasks are checked off in the Dialog checklist interface
    val checkedSubTasks = remember(aiChecklist) { mutableStateListOf<String>() }

    val (groupColor, groupBg, emoji) = when (task.groupName) {
        "Office Project" -> Triple(Color(0xFFFB6F8B), Color(0xFFFFF1F4), "💼")
        "Personal Project" -> Triple(Color(0xFF5C53FF), Color(0xFFF0EFFF), "🏠")
        else -> Triple(Color(0xFFFFAE34), Color(0xFFFFF7EC), "📚")
    }

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .wrapContentHeight()
                .testTag("project_details_dialog"),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
                    .padding(24.dp)
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(groupBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = emoji, fontSize = 18.sp)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = task.groupName,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = groupColor
                        )
                    }

                    IconButton(
                        onClick = onDismissRequest,
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color(0xFFF5F5FC), CircleShape)
                            .testTag("close_details_dialog")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Dialog",
                            tint = Color(0xFF1E1A3D),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Title
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleLarge.copy(lineHeight = 28.sp),
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E1A3D)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Metadata Rows (Scheduled Date & Time & Status)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Scheduled Date and Time
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF9FAFF))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = "Calendar",
                            tint = Color(0xFF8B8A99),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${task.time}  •  ${task.dateString}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF8B8A99)
                        )
                    }

                    // Status Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(groupColor.copy(alpha = 0.12f))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = if (task.isCompleted) "Completed" else task.status.replace("_", " "),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = groupColor
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Description Title
                Text(
                    text = "Project Description",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF8B8A99)
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Description Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFF9FAFF))
                        .padding(16.dp)
                ) {
                    Text(
                        text = task.description.ifBlank { "No detailed description provided for this project task." },
                        style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
                        color = Color(0xFF1E1A3D)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Divider(color = Color(0xFFF1F1F8))

                Spacer(modifier = Modifier.height(20.dp))

                // AI Coach Segment
                Text(
                    text = "✨ AI PROJECT COACH",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF5C53FF),
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                if (aiChecklist == null && !isAILoading) {
                    // Default State: Introduce the AI Coach card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F4FF)),
                        border = BorderStroke(1.dp, Color(0xFFEEECFF))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Analyze project requirements & generate an expert starter checklist to launch this project immediately using Gemini AI.",
                                style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp),
                                color = Color(0xFF5C53FF),
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    isAILoading = true
                                    coroutineScope.launch {
                                        try {
                                            val generated = GeminiService.generateTaskChecklist(
                                                title = task.title,
                                                description = task.description,
                                                groupName = task.groupName
                                            )
                                            aiChecklist = generated
                                        } catch (e: Exception) {
                                            Toast.makeText(context, "Error reaching AI: ${e.message}", Toast.LENGTH_LONG).show()
                                        } finally {
                                            isAILoading = false
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF5C53FF),
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("ask_ai_coach_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "Sparkles icon",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Ask AI Coach",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                } else if (isAILoading) {
                    // Loading State with Pulsing/Breathing Animation text
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFF5C53FF),
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Gemini AI Coach is analyzing project scope...",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF8B8A99),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                } else {
                    // Loaded State: Checklist
                    val checklist = aiChecklist ?: emptyList()
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Suggested Starter Activities Checklist:",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF1E1A3D),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 10.dp)
                        )

                        checklist.forEachIndexed { index, step ->
                            val isChecked = checkedSubTasks.contains(step)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable {
                                        if (isChecked) {
                                            checkedSubTasks.remove(step)
                                        } else {
                                            checkedSubTasks.add(step)
                                        }
                                    }
                                    .padding(vertical = 8.dp, horizontal = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (isChecked) Icons.Filled.CheckCircle else Icons.Outlined.Circle,
                                    contentDescription = "Toggle step status",
                                    tint = if (isChecked) Color(0xFF5C53FF) else Color(0xFFECEFF1),
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = step,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (isChecked) Color(0xFF8B8A99) else Color(0xFF1E1A3D)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Custom action layout for generated checklist items
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = {
                                    aiChecklist = null // Restart / regeneration callback
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFECEFF1),
                                    contentColor = Color(0xFF1E1A3D)
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(text = "Re-analyze", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            }

                            Button(
                                onClick = {
                                    val countToSave = if (checkedSubTasks.isEmpty()) checklist.size else checkedSubTasks.size
                                    val itemsToSave = if (checkedSubTasks.isEmpty()) checklist else checkedSubTasks.toList()

                                    // Insert steps directly to the databases!
                                    itemsToSave.forEachIndexed { idx, step ->
                                        // offset times to keep lists ordered nicely
                                        val calculatedTime = when (idx) {
                                            0 -> "10:30 AM"
                                            1 -> "01:30 PM"
                                            2 -> "04:30 PM"
                                            3 -> "06:00 PM"
                                            else -> "08:00 PM"
                                        }
                                        viewModel.addProjectTask(
                                            title = step,
                                            description = "AI recommended sub-task to launch: ${task.title}",
                                            groupName = task.groupName,
                                            time = calculatedTime,
                                            dateString = task.dateString,
                                            startDate = task.startDate,
                                            endDate = task.endDate,
                                            logoName = task.logoName
                                        )
                                    }

                                    Toast.makeText(
                                        context,
                                        "Successfully scheduled $countToSave checklist tasks for today's board! 🎉",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    onDismissRequest() // Close dialog
                                },
                                modifier = Modifier
                                    .weight(2f)
                                    .height(44.dp)
                                    .testTag("save_ai_tasks_button"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF5C53FF),
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = if (checkedSubTasks.isEmpty()) "Add All Steps to Tasks" else "Add (${checkedSubTasks.size}) Steps to Tasks",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
