package com.jobtracker.android.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jobtracker.android.ui.theme.PrimaryBlue
import com.jobtracker.android.ui.theme.TextSecondary
import com.jobtracker.shared.domain.model.StatusHistory
import com.jobtracker.shared.domain.model.Task
import com.jobtracker.shared.presentation.viewmodel.ApplicationDetailViewModel
import org.koin.compose.koinInject
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplicationDetailScreen(
    applicationId: String,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (String) -> Unit,
    viewModel: ApplicationDetailViewModel = koinInject<ApplicationDetailViewModel>()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddTaskDialog by remember { mutableStateOf(false) }
    var taskTitle by remember { mutableStateOf("") }
    val colorScheme = MaterialTheme.colorScheme
    val isDark = colorScheme.background == Color(0xFF101A22)
    
    LaunchedEffect(applicationId) {
        viewModel.loadApplication(applicationId)
    }
    
    Scaffold(
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = colorScheme.background.copy(alpha = 0.95f),
                tonalElevation = 3.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = colorScheme.onBackground
                        )
                    }
                    Text(
                        text = "Application Details",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        ),
                        color = colorScheme.onBackground,
                        modifier = Modifier.weight(1f)
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(onClick = { onNavigateToEdit(applicationId) }) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Edit",
                                tint = colorScheme.onBackground
                            )
                        }
                        IconButton(
                            onClick = {
                                // TODO: Show confirmation dialog
                                onNavigateBack()
                            }
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = Color(0xFFEF4444)
                            )
                        }
                    }
                }
            }
        }
    ) { padding ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PrimaryBlue)
                }
            }
            uiState.application == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Application not found", color = colorScheme.onBackground)
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    item {
                        HeroSection(application = uiState.application!!)
                    }
                    
                    item {
                        OverviewSection(application = uiState.application!!)
                    }
                    
                    item {
                        TimelineSection(statusHistory = uiState.statusHistory)
                    }
                    
                    item {
                        TasksSection(
                            tasks = uiState.tasks,
                            onToggleTask = { taskId -> viewModel.toggleTask(taskId) },
                            onAddTask = { showAddTaskDialog = true }
                        )
                    }
                    
                    // Interviews section would go here when implemented
                    // Contacts section would go here when implemented
                }
            }
        }
    }
    
    if (showAddTaskDialog) {
        AlertDialog(
            onDismissRequest = { showAddTaskDialog = false },
            title = { Text("Add Task") },
            text = {
                OutlinedTextField(
                    value = taskTitle,
                    onValueChange = { taskTitle = it },
                    label = { Text("Task Title") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (taskTitle.isNotBlank()) {
                            viewModel.addTask(applicationId, taskTitle)
                            taskTitle = ""
                            showAddTaskDialog = false
                        }
                    }
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddTaskDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun HeroSection(application: com.jobtracker.shared.domain.model.Application) {
    val colorScheme = MaterialTheme.colorScheme
    val isDark = colorScheme.background == Color(0xFF101A22)
    val statusColor = getStatusColor(application.status)
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDark) Color(0xFF192833) else Color.White
        )
    ) {
        Column {
            // Background image placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(128.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                statusColor.copy(alpha = 0.3f),
                                Color.Transparent
                            )
                        )
                    )
            )
            
            // Company logo and info
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-48).dp)
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Company Logo Placeholder
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isDark) Color(0xFF233948) else Color.White
                        )
                        .border(
                            1.dp,
                            if (isDark) Color(0xFF325167) else Color(0xFFE2E8F0),
                            RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = application.company.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                        style = MaterialTheme.typography.displaySmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 32.sp
                        ),
                        color = colorScheme.onSurface
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = application.role,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    ),
                    color = colorScheme.onSurface
                )
                
                Text(
                    text = application.company,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp
                    ),
                    color = colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = statusColor.copy(alpha = 0.2f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = statusColor
                            )
                            Text(
                                text = application.status.name.replaceFirstChar { it.uppercaseChar() },
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 12.sp
                                ),
                                color = statusColor
                            )
                        }
                    }
                    
                    application.location?.let {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.LocationOn,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = it,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 12.sp
                                    ),
                                    color = colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // Open Job Link Button
                Button(
                    onClick = {
                        // TODO: Open job URL
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryBlue
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        Icons.Default.AddCircle,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Open Job Link",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = "Applied ${formatRelativeDate(application.appliedDateEpochMs)} via ${application.source ?: "Unknown"}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 12.sp
                    ),
                    color = colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
fun OverviewSection(application: com.jobtracker.shared.domain.model.Application) {
    val colorScheme = MaterialTheme.colorScheme
    val isDark = colorScheme.background == Color(0xFF101A22)
    
    Column {
        Text(
            text = "Overview",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            ),
            color = colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 12.dp, start = 4.dp)
        )
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isDark) Color(0xFF192833) else Color.White
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "SALARY",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Medium,
                                fontSize = 11.sp,
                                letterSpacing = 0.5.sp
                            ),
                            color = colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Not specified",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp
                            ),
                            color = colorScheme.onSurface
                        )
                    }
                    
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "WORK TYPE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Medium,
                                fontSize = 11.sp,
                                letterSpacing = 0.5.sp
                            ),
                            color = colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Not specified",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp
                            ),
                            color = colorScheme.onSurface
                        )
                    }
                }
                
                if (application.notes.isNotBlank()) {
                    Column {
                        Text(
                            text = "NOTES",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Medium,
                                fontSize = 11.sp,
                                letterSpacing = 0.5.sp
                            ),
                            color = colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            text = application.notes,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 14.sp,
                                lineHeight = 20.sp
                            ),
                            color = colorScheme.onSurface
                        )
                    }
                }
                
                Divider(
                    color = if (isDark) Color(0xFF325167) else Color(0xFFF1F5F9),
                    thickness = 1.dp
                )
                
                // Tags (if we had tags in model)
                application.source?.let {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isDark) Color(0xFF233948) else Color(0xFFF1F5F9)
                        ) {
                            Text(
                                text = it,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 12.sp
                                ),
                                color = colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TimelineSection(statusHistory: List<StatusHistory>) {
    val colorScheme = MaterialTheme.colorScheme
    val isDark = colorScheme.background == Color(0xFF101A22)
    
    Column {
        Text(
            text = "Timeline",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            ),
            color = colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 12.dp, start = 4.dp)
        )
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isDark) Color(0xFF192833) else Color.White
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                statusHistory.forEachIndexed { index, history ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Timeline dot and line
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(PrimaryBlue)
                                    .border(
                                        4.dp,
                                        if (isDark) Color(0xFF192833) else Color.White,
                                        CircleShape
                                    )
                            )
                            if (index < statusHistory.size - 1) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Box(
                                    modifier = Modifier
                                        .width(2.dp)
                                        .height(40.dp)
                                        .background(
                                            if (isDark) Color(0xFF325167) else Color(0xFFE2E8F0)
                                        )
                                )
                            }
                        }
                        
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${history.fromStatus?.name ?: "N/A"} → ${history.toStatus.name}",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                ),
                                color = colorScheme.onSurface
                            )
                            Text(
                                text = "${formatDate(history.changedAtEpochMs)} • ${if (index == 0) "Completed" else "Pending"}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 12.sp
                                ),
                                color = colorScheme.onSurfaceVariant
                            )
                            history.note?.let {
                                Text(
                                    text = it,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 12.sp
                                    ),
                                    color = colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
                    
                    if (index < statusHistory.size - 1) {
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun TasksSection(
    tasks: List<Task>,
    onToggleTask: (String) -> Unit,
    onAddTask: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val isDark = colorScheme.background == Color(0xFF101A22)
    
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Tasks",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                ),
                color = colorScheme.onBackground,
                modifier = Modifier.padding(start = 4.dp)
            )
            TextButton(
                onClick = onAddTask,
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "ADD TASK",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        letterSpacing = 0.5.sp
                    ),
                    color = PrimaryBlue
                )
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            tasks.forEach { task ->
                TaskItem(
                    task = task,
                    onToggleDone = { onToggleTask(task.id) }
                )
            }
            
            // Add Task Row
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onAddTask),
                shape = RoundedCornerShape(12.dp),
                color = Color.Transparent,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isDark) Color(0xFF475569) else Color(0xFFCBD5E1)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = null,
                        tint = colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Add a new task...",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp
                        ),
                        color = colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun TaskItem(
    task: Task,
    onToggleDone: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val isDark = colorScheme.background == Color(0xFF101A22)
    
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggleDone),
        shape = RoundedCornerShape(12.dp),
        color = if (isDark) Color(0xFF192833) else Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = task.isDone,
                onCheckedChange = { onToggleDone() },
                colors = CheckboxDefaults.colors(
                    checkedColor = PrimaryBlue
                )
            )
            Text(
                text = task.title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                ),
                color = if (task.isDone) {
                    colorScheme.onSurfaceVariant
                } else {
                    colorScheme.onSurface
                },
                textDecoration = if (task.isDone) TextDecoration.LineThrough else null,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

private fun getStatusColor(status: com.jobtracker.shared.domain.enum.ApplicationStatus): Color {
    return when (status) {
        com.jobtracker.shared.domain.enum.ApplicationStatus.INTERVIEW -> PrimaryBlue
        com.jobtracker.shared.domain.enum.ApplicationStatus.APPLIED -> Color(0xFFFFB800)
        com.jobtracker.shared.domain.enum.ApplicationStatus.OFFER -> Color(0xFF10B981)
        com.jobtracker.shared.domain.enum.ApplicationStatus.REJECTED -> Color(0xFFEF4444)
        else -> PrimaryBlue
    }
}

private fun formatDate(epochMs: Long): String {
    val date = Date(epochMs)
    val format = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    return format.format(date)
}

private fun formatRelativeDate(epochMs: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - epochMs
    val days = diff / (1000 * 60 * 60 * 24)
    return when {
        days == 0L -> "today"
        days == 1L -> "yesterday"
        days < 7L -> "$days days ago"
        days < 30L -> "${days / 7} weeks ago"
        else -> formatDate(epochMs)
    }
}
