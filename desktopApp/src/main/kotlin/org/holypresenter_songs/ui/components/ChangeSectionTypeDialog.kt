package org.holypresenter_songs.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import org.holypresenter_songs.domain.SongSection
import org.holypresenter_songs.domain.SongSectionType

@Composable
fun ChangeSectionTypeDialog(
    section: SongSection,
    onDismiss: () -> Unit,
    onChange: (SongSectionType) -> Unit
) {
    var selectedType by remember(section.id) {
        mutableStateOf(section.type)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Сменить тип секции")
        },
        text = {
            Column {
                SongSectionType.entries.forEach { type ->
                    TextButton(
                        onClick = {
                            selectedType = type
                        }
                    ) {
                        RadioButton(
                            selected = selectedType == type,
                            onClick = {
                                selectedType = type
                            }
                        )

                        Text(
                            text = sectionTypeTitle(type)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                enabled = selectedType != section.type,
                onClick = {
                    onChange(selectedType)
                }
            ) {
                Text("Изменить")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Отмена")
            }
        }
    )
}