package org.holypresenter_songs.ui.library

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import holypresenter.org.platform.api.planner.PlannerItem
import holypresenter.org.platform.api.planner.PlannerReference
import holypresenter.org.platform.api.planner.PlannerService
import org.holypresenter.platform.ui.workspace.HolyLibraryPane
import org.holypresenter_songs.domain.SongId
import org.holypresenter_songs.repository.SongRepository

@Composable
fun SongLibraryPane(
    repository: SongRepository,
    selectedSongId: SongId?,
    plannerService: PlannerService?,
    onCreateSong: () -> Unit,
    onOpenSong: (SongId) -> Unit
) {
    val songs = repository.getAll()

    var search by remember {
        mutableStateOf("")
    }

    val filteredSongs = remember(
        songs,
        search
    ) {
        songs.filter {
            it.metadata.title.contains(
                search,
                ignoreCase = true
            )
        }
    }

    HolyLibraryPane {
        Text(
            text = "Песни",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = onCreateSong
        ) {
            Text("Новая песня")
        }

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = search,
            onValueChange = {
                search = it
            },
            label = {
                Text("Поиск")
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(
                items = filteredSongs,
                key = { song -> song.id.value }
            ) { song ->
                ListItem(
                    headlineContent = {
                        Text(
                            text = song.metadata.title.ifBlank {
                                "Без названия"
                            }
                        )
                    },
                    supportingContent = {
                        val author = song.metadata.author

                        if (author?.isNotBlank() ?: true) {
                            author?.let { Text(it) }
                        }
                    },
                    trailingContent = {
                        TextButton(
                            onClick = {
                                plannerService?.add(
                                    PlannerItem.Generic(
                                        reference = PlannerReference(
                                            moduleId = "songs",
                                            itemId = song.id.value
                                        ),
                                        title = song.metadata.title.ifBlank {
                                            "Без названия"
                                        }
                                    )
                                )
                            }
                        ) {
                            Text("+")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onOpenSong(song.id)
                        },
                    colors = ListItemDefaults.colors(
                        containerColor = if (song.id == selectedSongId) {
                            MaterialTheme.colorScheme.secondaryContainer
                        } else {
                            Color.Transparent
                        }
                    )
                )
            }
        }
    }
}