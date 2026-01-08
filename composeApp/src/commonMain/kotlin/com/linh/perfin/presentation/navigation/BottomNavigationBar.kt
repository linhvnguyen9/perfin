package com.linh.perfin.presentation.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.painterResource
import perfin.composeapp.generated.resources.Res
import perfin.composeapp.generated.resources.receipt_24px
import perfin.composeapp.generated.resources.person_24px

@Composable
fun BottomNavigationBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surfaceContainer
    ) {
        NavigationBarItem(
            selected = selectedTab == 0,
            onClick = { onTabSelected(0) },
            icon = {
                Icon(
                    painter = painterResource(Res.drawable.receipt_24px),
                    contentDescription = "Transactions"
                )
            },
            label = {
                Text("Transactions")
            }
        )

        NavigationBarItem(
            selected = selectedTab == 1,
            onClick = { onTabSelected(1) },
            icon = {
                Icon(
                    painter = painterResource(Res.drawable.person_24px),
                    contentDescription = "Profile"
                )
            },
            label = {
                Text("Profile")
            }
        )
    }
}
