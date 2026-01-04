package com.linh.perfin

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

import perfin.composeapp.generated.resources.Res
import perfin.composeapp.generated.resources.compose_multiplatform

@Serializable
data object TransactionList: NavKey

@Serializable
data class TransactionDetail(val id: String): NavKey

private val config = SavedStateConfiguration { 
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            subclass(TransactionList::class, TransactionList.serializer())
            subclass(TransactionDetail::class, TransactionDetail.serializer())
        }
    }
}

@Composable
@Preview
fun App() {
    MaterialTheme {
        val backStack = rememberNavBackStack(config, TransactionList)

        NavDisplay(
            modifier = Modifier.safeContentPadding(),
            backStack = backStack,
            onBack = { backStack.removeLastOrNull() },
            entryProvider = { key ->
                when (key) {
                    is TransactionList -> NavEntry(key) {
                        Text("Transaction list")
                    }
                    else -> NavEntry(key) {
                        Text("Unknown entry")
                    }
                }
            }
        )
    }
}