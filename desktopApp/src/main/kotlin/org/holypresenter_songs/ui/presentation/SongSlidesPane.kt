package org.holypresenter_songs.ui.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import holypresenter.org.platform.api.module.ModuleContext
import org.holypresenter.platform.ui.presenter.HolyPresenterSectionHeader
import org.holypresenter_songs.domain.Song
import org.holypresenter_songs.domain.SongSectionType
import org.holypresenter_songs.domain.SongSlide

@Composable
fun SongSlidesPane(
    moduleContext: ModuleContext,
    song: Song,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    onSlideClick: (
        slide: SongSlide,
        globalIndex: Int
    ) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedSlideIndex by remember(song.id) {
        mutableStateOf<Int?>(null)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TextButton(onClick = onBackClick) {
                Text("← Библиотека")
            }

            TextButton(onClick = onEditClick) {
                Text("Редактировать")
            }

            Text(
                text = song.metadata.title,
                style = MaterialTheme.typography.titleLarge
            )
        }

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 12.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            var globalIndex = 0

            song.sections.forEach { section ->
                item {
                    HolyPresenterSectionHeader(
                        title = sectionTitle(
                            type = section.type,
                            number = section.number
                        ),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                section.slides.forEach { slide ->
                    val currentIndex = globalIndex
                    globalIndex++

                    item(
                        key = "${song.id.value}-$currentIndex"
                    ) {
                        SongPresenterSlideCard(
                            slide = slide,
                            number = currentIndex + 1,
                            selected = selectedSlideIndex == currentIndex,
                            onClick = {
                                selectedSlideIndex = currentIndex

                                onSlideClick(
                                    slide,
                                    currentIndex
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

private fun sectionTitle(
    type: SongSectionType,
    number: Int
): String =
    when (type) {
        SongSectionType.VERSE ->
            "Куплет $number"

        SongSectionType.CHORUS ->
            "Припев $number"

        SongSectionType.BRIDGE ->
            "Бридж $number"

        SongSectionType.INTRO ->
            "Вступление"

        SongSectionType.PRE_CHORUS ->
            "Предпрепев"

        SongSectionType.TAG ->
            "Раздел $number"

        SongSectionType.ENDING ->
            "Завершение"
    }