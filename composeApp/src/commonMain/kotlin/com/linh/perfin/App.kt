package com.linh.perfin

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.linh.perfin.presentation.account.addedit.AccountAddEditScreen
import com.linh.perfin.presentation.account.list.ManageAccountsScreen
import com.linh.perfin.presentation.navigation.BottomNavigationBar
import com.linh.perfin.presentation.profile.ProfileScreen
import com.linh.perfin.presentation.transaction.addedit.TransactionAddEditScreen
import com.linh.perfin.presentation.transaction.list.TransactionListScreen
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import org.jetbrains.compose.ui.tooling.preview.Preview

@Serializable
data object TransactionList: NavKey

@Serializable
data class TransactionDetail(val id: String): NavKey

@Serializable
data class TransactionAddEdit(val transactionId: String? = null): NavKey

@Serializable
data object Profile: NavKey

@Serializable
data object ManageAccounts: NavKey

@Serializable
data class AccountAddEdit(val accountId: String? = null): NavKey

private val config = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            subclass(TransactionList::class, TransactionList.serializer())
            subclass(TransactionDetail::class, TransactionDetail.serializer())
            subclass(TransactionAddEdit::class, TransactionAddEdit.serializer())
            subclass(Profile::class, Profile.serializer())
            subclass(ManageAccounts::class, ManageAccounts.serializer())
            subclass(AccountAddEdit::class, AccountAddEdit.serializer())
        }
    }
}

@Composable
@Preview
fun App() {
    MaterialTheme {
        // Create separate back stacks for each tab
        val transactionsBackStack = rememberNavBackStack(config, TransactionList)
        val profileBackStack = rememberNavBackStack(config, Profile)

        // Track selected tab (0 = Transactions, 1 = Profile)
        var selectedTab by remember { mutableStateOf(0) }

        // Track bottom nav visibility
        var showBottomNav by remember { mutableStateOf(true) }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                if (showBottomNav) {
                    BottomNavigationBar(
                        selectedTab = selectedTab,
                        onTabSelected = { selectedTab = it }
                    )
                }
            }
        ) { paddingValues ->
            when (selectedTab) {
                0 -> {
                    // Transactions tab
                    NavDisplay(
                        backStack = transactionsBackStack,
                        onBack = { transactionsBackStack.removeLastOrNull() },
                        entryProvider = { key ->
                            when (key) {
                                is TransactionList -> NavEntry(key) {
                                    LaunchedEffect(Unit) { showBottomNav = true }
                                    TransactionListScreen(
                                        onNavigateToDetail = { id ->
                                            transactionsBackStack.add(TransactionDetail(id))
                                        },
                                        onNavigateToCreate = {
                                            transactionsBackStack.add(TransactionAddEdit(transactionId = null))
                                        }
                                    )
                                }
                                is TransactionDetail -> NavEntry(key) {
                                    LaunchedEffect(Unit) { showBottomNav = false }
                                    TransactionAddEditScreen(
                                        transactionId = key.id,
                                        onNavigateBack = { transactionsBackStack.removeLastOrNull() }
                                    )
                                }
                                is TransactionAddEdit -> NavEntry(key) {
                                    LaunchedEffect(Unit) { showBottomNav = false }
                                    TransactionAddEditScreen(
                                        transactionId = key.transactionId,
                                        onNavigateBack = { transactionsBackStack.removeLastOrNull() }
                                    )
                                }
                                else -> NavEntry(key) {
                                    Text("Unknown entry")
                                }
                            }
                        },
                        modifier = Modifier.padding(paddingValues)
                    )
                }
                1 -> {
                    // Profile tab
                    NavDisplay(
                        backStack = profileBackStack,
                        onBack = { profileBackStack.removeLastOrNull() },
                        entryProvider = { key ->
                            when (key) {
                                is Profile -> NavEntry(key) {
                                    LaunchedEffect(Unit) { showBottomNav = true }
                                    ProfileScreen(
                                        onNavigateToManageAccounts = {
                                            profileBackStack.add(ManageAccounts)
                                        }
                                    )
                                }
                                is ManageAccounts -> NavEntry(key) {
                                    LaunchedEffect(Unit) { showBottomNav = false }
                                    ManageAccountsScreen(
                                        onNavigateBack = { profileBackStack.removeLastOrNull() },
                                        onNavigateToAddAccount = {
                                            profileBackStack.add(AccountAddEdit(accountId = null))
                                        },
                                        onNavigateToEditAccount = { accountId -> profileBackStack.add(AccountAddEdit(accountId = accountId)) }
                                    )
                                }
                                is AccountAddEdit -> NavEntry(key) {
                                    LaunchedEffect(Unit) { showBottomNav = false }
                                    AccountAddEditScreen(
                                        accountId = key.accountId,
                                        onNavigateBack = { profileBackStack.removeLastOrNull() }
                                    )
                                }
                                else -> NavEntry(key) {
                                    Text("Unknown entry")
                                }
                            }
                        },
                        modifier = Modifier.padding(paddingValues)
                    )
                }
            }
        }
    }
}