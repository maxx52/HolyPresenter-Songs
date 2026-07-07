package org.holypresenter_songs.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.*
import androidx.compose.runtime.*
import org.holypresenter_songs.domain.SongSectionType

@Composable
fun AddSectionDialog(
    onDismiss: () -> Unit,
    onCreate: (SongSectionType) -> Unit
) {
    var selectedType by remember {
        mutableStateOf(SongSectionType.VERSE)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Новая секция")
        },
        text = {
            Column {
                SongSectionType.entries.forEach { type ->
                    RowOption(
                        text = sectionTypeTitle(type),
                        selected = selectedType == type,
                        onClick = { selectedType = type }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onCreate(selectedType)
                }
            ) {
                Text("Создать")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}

@Composable
private fun RowOption(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    TextButton(onClick = onClick) {
        RadioButton(
            selected = selected,
            onClick = onClick
        )
        Text(text)
    }
}

private fun sectionTypeTitle(type: SongSectionType): String =
    when (type) {
        SongSectionType.VERSE -> "Куплет"
        SongSectionType.CHORUS -> "Припев"
        SongSectionType.PRE_CHORUS -> "Предприпев"
        SongSectionType.BRIDGE -> "Бридж"
        SongSectionType.TAG -> "Тег"
        SongSectionType.INTRO -> "Интро"
        SongSectionType.ENDING -> "Финал"
    }