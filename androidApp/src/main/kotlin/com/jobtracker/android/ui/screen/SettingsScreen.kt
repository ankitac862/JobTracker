@file:OptIn(ExperimentalMaterial3Api::class)
package com.jobtracker.android.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jobtracker.shared.presentation.viewmodel.SettingsViewModel
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: SettingsViewModel = koinInject<SettingsViewModel>()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    val colorScheme = MaterialTheme.colorScheme
    
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
                        text = "Settings",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        ),
                        color = colorScheme.onBackground,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(48.dp)) // Balance for back button
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Sync Status",
                style = MaterialTheme.typography.titleLarge
            )
            
            uiState.lastSyncedAtEpochMs?.let {
                Text("Last synced: ${java.util.Date(it)}")
            }
            
            if (uiState.isSyncing) {
                CircularProgressIndicator()
            }
            
            uiState.syncError?.let {
                Text("Sync error: $it", color = MaterialTheme.colorScheme.error)
            }
            
            Button(
                onClick = { viewModel.syncNow() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Sync Now")
            }
        }
    }
}

