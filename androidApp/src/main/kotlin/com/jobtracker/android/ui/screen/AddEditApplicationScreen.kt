package com.jobtracker.android.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jobtracker.android.ui.theme.PrimaryBlue
import com.jobtracker.shared.domain.enum.ApplicationStatus
import com.jobtracker.shared.presentation.viewmodel.ApplicationsViewModel
import org.koin.compose.koinInject
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditApplicationScreen(
    applicationId: String? = null,
    onNavigateBack: () -> Unit,
    viewModel: ApplicationsViewModel = koinInject<ApplicationsViewModel>()
) {
    var company by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var jobUrl by remember { mutableStateOf("") }
    var source by remember { mutableStateOf("") }
    var status by remember { mutableStateOf(ApplicationStatus.DRAFT) }
    var notes by remember { mutableStateOf("") }
    var appliedDate by remember { mutableStateOf(Date()) }
    val colorScheme = MaterialTheme.colorScheme
    val isDark = colorScheme.background == Color(0xFF101A22)
    
    LaunchedEffect(applicationId) {
        if (applicationId != null) {
            val app = viewModel.uiState.value.applications.find { it.id == applicationId }
            app?.let {
                company = it.company
                role = it.role
                location = it.location ?: ""
                jobUrl = it.jobUrl ?: ""
                source = it.source ?: ""
                status = it.status
                notes = it.notes
                appliedDate = Date(it.appliedDateEpochMs)
            }
        }
    }
    
    Scaffold(
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = if (isDark) Color(0xFF101A22) else Color.White,
                tonalElevation = 1.dp
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
                        text = if (applicationId == null) "Add Application" else "Edit Application",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        ),
                        color = colorScheme.onBackground,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(
                        onClick = {
                            if (company.isNotBlank() && role.isNotBlank()) {
                                if (applicationId == null) {
                                    viewModel.addNewApplication(
                                        company = company,
                                        role = role,
                                        location = location.takeIf { it.isNotBlank() },
                                        jobUrl = jobUrl.takeIf { it.isNotBlank() },
                                        source = source.takeIf { it.isNotBlank() },
                                        status = status,
                                        appliedDateEpochMs = appliedDate.time,
                                        notes = notes
                                    )
                                } else {
                                    val app = viewModel.uiState.value.applications.find { it.id == applicationId }
                                    app?.let {
                                        viewModel.updateExistingApplication(
                                            it.copy(
                                                company = company,
                                                role = role,
                                                location = location.takeIf { it.isNotBlank() },
                                                jobUrl = jobUrl.takeIf { it.isNotBlank() },
                                                source = source.takeIf { it.isNotBlank() },
                                                status = status,
                                                notes = notes
                                            )
                                        )
                                    }
                                }
                                onNavigateBack()
                            }
                        }
                    ) {
                        Text(
                            text = "Save",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = PrimaryBlue
                        )
                    }
                }
            }
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = if (isDark) Color(0xFF101A22) else Color.White,
                tonalElevation = 8.dp,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isDark) Color(0xFF2A3C4A) else Color(0xFFE2E8F0)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = {
                            if (company.isNotBlank() && role.isNotBlank()) {
                                if (applicationId == null) {
                                    viewModel.addNewApplication(
                                        company = company,
                                        role = role,
                                        location = location.takeIf { it.isNotBlank() },
                                        jobUrl = jobUrl.takeIf { it.isNotBlank() },
                                        source = source.takeIf { it.isNotBlank() },
                                        status = status,
                                        appliedDateEpochMs = appliedDate.time,
                                        notes = notes
                                    )
                                } else {
                                    val app = viewModel.uiState.value.applications.find { it.id == applicationId }
                                    app?.let {
                                        viewModel.updateExistingApplication(
                                            it.copy(
                                                company = company,
                                                role = role,
                                                location = location.takeIf { it.isNotBlank() },
                                                jobUrl = jobUrl.takeIf { it.isNotBlank() },
                                                source = source.takeIf { it.isNotBlank() },
                                                status = status,
                                                notes = notes
                                            )
                                        )
                                    }
                                }
                                onNavigateBack()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryBlue
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Save Application",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        )
                    }
                    
                    TextButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = colorScheme.onSurfaceVariant
                        )
                    ) {
                        Text(
                            text = "Cancel",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp
                            )
                        )
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Section: Core Details
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    FormField(
                        label = "Company",
                        value = company,
                        onValueChange = { company = it },
                        placeholder = "e.g. Google",
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    FormField(
                        label = "Role",
                        value = role,
                        onValueChange = { role = it },
                        placeholder = "e.g. Senior Product Designer",
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    var statusExpanded by remember { mutableStateOf(false) }
                    Column {
                        Text(
                            text = "Status",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp
                            ),
                            color = colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
                        )
                        ExposedDropdownMenuBox(
                            expanded = statusExpanded,
                            onExpandedChange = { statusExpanded = !statusExpanded }
                        ) {
                            OutlinedTextField(
                                value = status.name.replaceFirstChar { it.uppercaseChar() },
                                onValueChange = { },
                                readOnly = true,
                                placeholder = { Text("Select status") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusExpanded)
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = PrimaryBlue,
                                    unfocusedBorderColor = if (isDark) Color(0xFF325167) else Color(0xFFCBD5E1),
                                    focusedContainerColor = if (isDark) Color(0xFF1C2A35) else Color.White,
                                    unfocusedContainerColor = if (isDark) Color(0xFF1C2A35) else Color.White
                                ),
                                shape = RoundedCornerShape(12.dp),
                                textStyle = MaterialTheme.typography.bodyLarge
                            )
                            ExposedDropdownMenu(
                                expanded = statusExpanded,
                                onDismissRequest = { statusExpanded = false }
                            ) {
                                ApplicationStatus.values().forEach { statusOption ->
                                    DropdownMenuItem(
                                        text = { Text(statusOption.name.replaceFirstChar { it.uppercaseChar() }) },
                                        onClick = {
                                            status = statusOption
                                            statusExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
                
                Divider(
                    color = if (isDark) Color(0xFF2A3C4A) else Color(0xFFE2E8F0),
                    thickness = 1.dp
                )
                
                // Section: Logistics
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    FormFieldWithIcon(
                        label = "Location",
                        value = location,
                        onValueChange = { location = it },
                        placeholder = "e.g. San Francisco, CA",
                        icon = Icons.Default.LocationOn,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    FormFieldWithIcon(
                        label = "Source",
                        value = source,
                        onValueChange = { source = it },
                        placeholder = "e.g. LinkedIn, Referral",
                        icon = Icons.Default.Person,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                
                Divider(
                    color = if (isDark) Color(0xFF2A3C4A) else Color(0xFFE2E8F0),
                    thickness = 1.dp
                )
                
                // Section: Details
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    FormFieldWithIcon(
                        label = "Job Post URL",
                        value = jobUrl,
                        onValueChange = { jobUrl = it },
                        placeholder = "https://...",
                        icon = Icons.Default.MailOutline,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Column {
                        Text(
                            text = "Notes",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp
                            ),
                            color = colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
                        )
                        OutlinedTextField(
                            value = notes,
                            onValueChange = { notes = it },
                            placeholder = { Text("Add specific details, interview notes, or follow-up reminders...") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp),
                            minLines = 5,
                            maxLines = 8,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryBlue,
                                unfocusedBorderColor = if (isDark) Color(0xFF325167) else Color(0xFFCBD5E1),
                                focusedContainerColor = if (isDark) Color(0xFF1C2A35) else Color.White,
                                unfocusedContainerColor = if (isDark) Color(0xFF1C2A35) else Color.White
                            ),
                            shape = RoundedCornerShape(12.dp),
                            textStyle = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
                
                // Bottom spacer for footer
                Spacer(modifier = Modifier.height(96.dp))
            }
        }
    }
}

@Composable
fun FormField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    val isDark = colorScheme.background == Color(0xFF101A22)
    
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp
            ),
            color = colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder) },
            modifier = modifier.height(56.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryBlue,
                unfocusedBorderColor = if (isDark) Color(0xFF325167) else Color(0xFFCBD5E1),
                focusedContainerColor = if (isDark) Color(0xFF1C2A35) else Color.White,
                unfocusedContainerColor = if (isDark) Color(0xFF1C2A35) else Color.White
            ),
            shape = RoundedCornerShape(12.dp),
            textStyle = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun FormFieldWithIcon(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    val isDark = colorScheme.background == Color(0xFF101A22)
    
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp
            ),
            color = colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder) },
            modifier = modifier.height(56.dp),
            leadingIcon = {
                Icon(
                    icon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = colorScheme.onSurfaceVariant
                )
            },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryBlue,
                unfocusedBorderColor = if (isDark) Color(0xFF325167) else Color(0xFFCBD5E1),
                focusedContainerColor = if (isDark) Color(0xFF1C2A35) else Color.White,
                unfocusedContainerColor = if (isDark) Color(0xFF1C2A35) else Color.White
            ),
            shape = RoundedCornerShape(12.dp),
            textStyle = MaterialTheme.typography.bodyLarge
        )
    }
}
