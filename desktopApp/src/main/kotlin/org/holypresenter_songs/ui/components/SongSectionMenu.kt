package org.holypresenter_songs.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun SongSectionMenu(
    onChangeType: () -> Unit = {},
    onDuplicate: () -> Unit = {},
    onDelete: () -> Unit = {}
) {
    var expanded by remember {
        mutableStateOf(false)
    }

    Box(
        modifier =
            Modifier.wrapContentSize(
                Alignment.TopEnd
            )
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
                    Text("Сменить тип")
                },
                onClick = {
                    expanded = false
                    onChangeType()
                }
            )

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