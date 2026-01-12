package com.linh.perfin.presentation.transaction.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.linh.perfin.domain.model.transaction.TransactionWithDetails
import com.linh.perfin.presentation.transaction.list.components.EmptyTransactionState
import com.linh.perfin.presentation.transaction.list.components.TransactionGroupHeader
import com.linh.perfin.presentation.transaction.list.components.TransactionListItem
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import perfin.composeapp.generated.resources.Res
import perfin.composeapp.generated.resources.add_24px

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionListScreen(
    onNavigateToDetail: (String) -> Unit,
    onNavigateToCreate: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TransactionListViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Transactions",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToCreate) {
                Icon(
                    painterResource(Res.drawable.add_24px),
                    contentDescription = "Add transaction"
                )
            }
        },
        modifier = modifier
    ) { paddingValues ->
        when (val state = uiState) {
            is TransactionListUiState.Loading -> {
                LoadingState(modifier = Modifier.padding(paddingValues))
            }

            is TransactionListUiState.Success -> {
                if (state.transactions.isEmpty()) {
                    EmptyTransactionState(
                        onCreateTransaction = onNavigateToCreate,
                        modifier = Modifier.padding(paddingValues)
                    )
                } else {
                    TransactionList(
                        transactions = state.transactions,
                        onTransactionClick = onNavigateToDetail,
                        modifier = Modifier.padding(paddingValues)
                    )
                }
            }

            is TransactionListUiState.Error -> {
                ErrorState(
                    message = state.message,
                    modifier = Modifier.padding(paddingValues)
                )
            }
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
private fun TransactionList(
    transactions: List<TransactionWithDetails>,
    onTransactionClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val groupedTransactions = transactions.groupBy { transaction ->
        transaction.transaction.date
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .date
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        groupedTransactions.forEach { (date, transactionsForDate) ->
            stickyHeader(key = date) {
                TransactionGroupHeader(date = date)
            }

            items(
                items = transactionsForDate,
                key = { it.transaction.id }
            ) { transaction ->
                TransactionListItem(
                    transaction = transaction,
                    onClick = { onTransactionClick(transaction.transaction.id) },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
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
        Card(
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Error Loading Transactions",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.error
                )
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )
                TextButton(onClick = { /* TODO: Add retry logic */ }) {
                    Text("Retry")
                }
            }
        }
    }
}
