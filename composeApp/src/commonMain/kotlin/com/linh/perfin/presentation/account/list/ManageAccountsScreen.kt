package com.linh.perfin.presentation.account.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.linh.perfin.domain.model.account.Account
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import perfin.composeapp.generated.resources.Res
import perfin.composeapp.generated.resources.account_balance_24px
import perfin.composeapp.generated.resources.arrow_back_24px

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageAccountsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAddAccount: () -> Unit,
    onNavigateToEditAccount: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ManageAccountsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Manage Accounts",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            painter = painterResource(Res.drawable.arrow_back_24px),
                            contentDescription = "Navigate back"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddAccount,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    painter = painterResource(Res.drawable.account_balance_24px),
                    contentDescription = "Add account",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        modifier = modifier
    ) { paddingValues ->
        when (val state = uiState) {
            is AccountListUiState.Loading -> {
                LoadingState(modifier = Modifier.padding(paddingValues))
            }

            is AccountListUiState.Success -> {
                if (state.accounts.isEmpty()) {
                    EmptyState(
                        modifier = Modifier.padding(paddingValues)
                    )
                } else {
                    AccountList(
                        accounts = state.accounts,
                        onAccountClick = onNavigateToEditAccount,
                        modifier = Modifier.padding(paddingValues)
                    )
                }
            }

            is AccountListUiState.Error -> {
                ErrorState(
                    message = state.message,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

@Composable
private fun AccountList(
    accounts: List<Account>,
    onAccountClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(accounts, key = { it.id }) { account ->
            AccountItem(
                account = account,
                onClick = { onAccountClick(account.id) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AccountItem(
    account: Account,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                painter = painterResource(Res.drawable.account_balance_24px),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = account.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (account.bank != null) {
                    Text(
                        text = formatBankName(account.bank),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                painter = painterResource(Res.drawable.account_balance_24px),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(16.dp)
            )
            Text(
                text = "No accounts yet",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Tap the + button to add your first account",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun LoadingState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorState(
    message: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                text = "Error",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.error
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}

private fun formatBankName(bank: com.linh.perfin.domain.model.BankInVietnam): String {
    return bank.name.replace("_", " ").lowercase()
        .split(" ")
        .joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }
}