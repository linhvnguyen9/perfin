package com.linh.perfin.presentation.profile.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import org.jetbrains.compose.resources.painterResource
import perfin.composeapp.generated.resources.Res
import perfin.composeapp.generated.resources.arrow_forward_24px

@Composable
fun ProfileMenuItem(
    title: String,
    icon: Painter,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        ListItem(
            headlineContent = {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge
                )
            },
            leadingContent = {
                Icon(
                    painter = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            trailingContent = {
                Icon(
                    painterResource(Res.drawable.arrow_forward_24px),
                    contentDescription = "Navigate",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        )
    }
}