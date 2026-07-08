package org.holypresenter_songs.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun SongSectionMenu(
    onRename: () -> Unit = {},
    onDuplicate: () -> Unit = {},
    onDelete: () -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.wrapContentSize(Alignment.TopEnd)
    ) {
        IconButton(
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
}