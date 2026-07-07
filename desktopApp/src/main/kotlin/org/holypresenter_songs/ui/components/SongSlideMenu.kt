package org.holypresenter_songs.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.*

@Composable
fun SongSlideMenu(
    onDuplicate: () -> Unit = {},
    onDelete: () -> Unit = {}
) {
    var expanded by remember {
        mutableStateOf(false)
    }

    TextButton(
        onClick = {
            expanded = true
        }
    ) {
        Text("⋮")
    }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = {
            expanded = false
        }
    ) {
        DropdownMenuItem(
            text = {
                Text("Дублировать")
            },
            onClick = {
                expanded = false
                onDuplicate()
            }
        )

        DropdownMenuItem(
            text = {
                Text("Удалить")
            },
            onClick = {
                expanded = false
                onDelete()
            }
        )
    }
}