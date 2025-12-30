package com.jobtracker.android.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jobtracker.android.ui.theme.PrimaryBlue
import com.jobtracker.android.ui.theme.TextSecondary
import com.jobtracker.shared.domain.enum.ApplicationStatus
import com.jobtracker.shared.domain.model.Application
import com.jobtracker.shared.presentation.viewmodel.ApplicationsViewModel
import org.koin.compose.koinInject
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplicationsListScreen(
    onNavigateToDetail: (String) -> Unit,
    onNavigateToAdd: () -> Unit,
    onNavigateToSettings: () -> Unit = {},
    viewModel: ApplicationsViewModel = koinInject<ApplicationsViewModel>()
) {
    val uiState by viewModel.uiState.collectAsState()
    var searchText by remember { mutableStateOf("") }
    val colorScheme = MaterialTheme.colorScheme
    
    LaunchedEffect(searchText) {
        viewModel.setSearchQuery(searchText)
    }
    
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAdd,
                containerColor = PrimaryBlue,
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add Application",
                    modifier = Modifier.size(32.dp),
                    tint = Color.White
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colorScheme.background)
        ) {
            // Sticky Header
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
                    Text(
                        text = "Job Tracker",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            letterSpacing = (-0.015).sp
                        ),
                        color = colorScheme.onBackground
                    )
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = colorScheme.onBackground
                        )
                    }
                }
            }
            
            // Search & Filter Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colorScheme.background)
                    .padding(vertical = 8.dp)
            ) {
                // Search Bar
                OutlinedTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    placeholder = { Text("Search company or role...") },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = null,
                            tint = if (MaterialTheme.colorScheme.background == Color(0xFF101A22)) TextSecondary else Color(0xFF64748B)
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryBlue,
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = if (MaterialTheme.colorScheme.background == Color(0xFF101A22)) Color(0xFF233948) else Color.White,
                        unfocusedContainerColor = if (MaterialTheme.colorScheme.background == Color(0xFF101A22)) Color(0xFF233948) else Color.White
                    ),
                    textStyle = MaterialTheme.typography.bodyLarge
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Filter Chips (Horizontal Scroll)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatusFilterChip(
                        label = "All",
                        selected = uiState.selectedStatus == null,
                        onClick = { viewModel.setStatusFilter(null) }
                    )
                    ApplicationStatus.values().forEach { status ->
                        StatusFilterChip(
                            label = status.name.replaceFirstChar { it.uppercaseChar() },
                            selected = uiState.selectedStatus == status,
                            onClick = { viewModel.setStatusFilter(status) }
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Sort Dropdown
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Sort by:",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Medium,
                            fontSize = 10.sp,
                            letterSpacing = 0.5.sp
                        ),
                        color = colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    TextButton(
                        onClick = { /* TODO: Show sort menu */ },
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp)
                    ) {
                        Text(
                            text = "Recently Updated",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = PrimaryBlue
                        )
                        Icon(
                            Icons.Default.ExpandMore,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = PrimaryBlue
                        )
                    }
                }
            }
            
            Divider(
                modifier = Modifier.fillMaxWidth(),
                color = if (MaterialTheme.colorScheme.background == Color(0xFF101A22)) Color(0xFF2A3C4A) else Color(0xFFE2E8F0),
                thickness = 1.dp
            )
            
            // Application List
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
                uiState.error != null -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Error: ${uiState.error}",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(uiState.filteredApplications) { application ->
                            ApplicationCard(
                                application = application,
                                onClick = { onNavigateToDetail(application.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatusFilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    
    Surface(
        onClick = onClick,
        modifier = Modifier.height(36.dp),
        shape = CircleShape,
        color = if (selected) {
            PrimaryBlue
        } else {
            if (colorScheme.background == Color(0xFF101A22)) Color(0xFF233948) else Color.White
        },
        border = if (!selected) {
            androidx.compose.foundation.BorderStroke(
                1.dp,
                if (colorScheme.background == Color(0xFF101A22)) Color.Transparent else Color(0xFFE2E8F0)
            )
        } else null
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp
            ),
            color = if (selected) Color.White else colorScheme.onSurface
        )
    }
}

@Composable
fun ApplicationCard(
    application: Application,
    onClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val statusColor = getStatusColor(application.status)
    val isDark = colorScheme.background == Color(0xFF101A22)
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDark) Color(0xFF1C2A35) else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            // Left border indicator
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(4.dp)
                    .background(statusColor)
                    .align(Alignment.CenterStart)
            )
            
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Company Logo Placeholder
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (isDark) Color.White.copy(alpha = 0.1f) else Color(0xFFF1F5F9)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = application.company.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp
                                ),
                                color = colorScheme.onSurface
                            )
                        }
                        
                        Column {
                            Text(
                                text = application.company,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                ),
                                color = colorScheme.onSurface
                            )
                            Text(
                                text = application.role,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 14.sp
                                ),
                                color = colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    
                    // Status Badge
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = statusColor.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = application.status.name.replaceFirstChar { it.uppercaseChar() },
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                letterSpacing = 0.5.sp
                            ),
                            color = statusColor
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Column(modifier = Modifier.padding(start = 52.dp)) {
                    // Location and Source
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        application.location?.let {
                            Row(
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
                                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                                    color = colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        
                        application.source?.let {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    if (it.contains("linkedin", ignoreCase = true)) Icons.Default.Person
                                    else Icons.Default.Language,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = it,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                                    color = colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                    
                    Divider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        color = if (isDark) Color.White.copy(alpha = 0.05f) else Color(0xFFF1F5F9),
                        thickness = 1.dp
                    )
                    
                    // Applied Date and Tags
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Applied: ${formatDate(application.appliedDateEpochMs)}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Medium,
                                fontSize = 12.sp
                            ),
                            color = colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                        
                        // Tags (if we had tags in the model, we'd show them here)
                        // For now, showing source as a tag
                        application.source?.let {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = if (isDark) Color.White.copy(alpha = 0.05f) else Color(0xFFF1F5F9),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isDark) Color.White.copy(alpha = 0.1f) else Color(0xFFE2E8F0)
                                )
                            ) {
                                Text(
                                    text = it,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 10.sp
                                    ),
                                    color = colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun getStatusColor(status: ApplicationStatus): Color {
    return when (status) {
        ApplicationStatus.INTERVIEW -> PrimaryBlue
        ApplicationStatus.APPLIED -> Color(0xFFFFB800) // Yellow
        ApplicationStatus.OFFER -> Color(0xFF10B981) // Green
        ApplicationStatus.REJECTED -> Color(0xFFEF4444) // Red
        else -> PrimaryBlue
    }
}

private fun formatDate(epochMs: Long): String {
    val date = Date(epochMs)
    val format = SimpleDateFormat("MMM dd", Locale.getDefault())
    return format.format(date)
}
