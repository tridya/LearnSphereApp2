package com.example.learnsphereapp2.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun CommonTitleBar(
    title: String,
    navController: NavController? = null,
    showBackButton: Boolean = true,
    showNotificationIcon: Boolean = true,
    showProfileIcon: Boolean = true,
    onBackClick: (() -> Unit)? = null,
    onNotificationClick: (() -> Unit)? = null,
    onProfileClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp), // Match reference: only vertical padding
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Back Button (optional)
        if (showBackButton && navController != null) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Kembali",
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .clickable { onBackClick?.invoke() ?: navController.popBackStack() },
                tint = Color.Black
            )
        } else {
            Spacer(modifier = Modifier.size(24.dp)) // Match reference: simple Spacer
        }

        // Title
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge.copy(
                color = Color.Black
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f)
        )

        // Notification and Profile Icons
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (showNotificationIcon) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notifikasi",
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .clickable { onNotificationClick?.invoke() },
                    tint = Color.Black
                )
            }
            if (showProfileIcon) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profil",
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .clickable { onProfileClick?.invoke() },
                    tint = Color.Black
                )
            }
            if (!showNotificationIcon && !showProfileIcon) {
                Spacer(modifier = Modifier.size(24.dp))
            }
        }
    }
}