package com.linh.perfin.presentation.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import com.linh.perfin.presentation.profile.components.ProfileMenuItem
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import perfin.composeapp.generated.resources.Res
import perfin.composeapp.generated.resources.account_balance_24px

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateToManageAccounts: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Profile",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            )
        },
        modifier = modifier
    ) { paddingValues ->
        when (val state = uiState) {
            is ProfileUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is ProfileUiState.Success -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // User info section
                    Column(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "Welcome, ${state.userName}",
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Settings section title
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    // Accounts menu item
                    ProfileMenuItem(
                        title = "Accounts",
                        icon = painterResource(Res.drawable.account_balance_24px),
                        onClick = onNavigateToManageAccounts
                    )
                }
            }

            is ProfileUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = state.message,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
