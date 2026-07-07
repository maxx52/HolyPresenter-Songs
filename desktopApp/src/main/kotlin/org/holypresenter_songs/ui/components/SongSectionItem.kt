package org.holypresenter_songs.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.holypresenter_songs.domain.SongSection
import org.holypresenter_songs.domain.SongSectionType
import org.holypresenter_songs.ui.editor.SongSlideItem

@Composable
fun SongSectionItem(
    section: SongSection,
) {
    fun SongSection.title(): String =
        when (type) {
            SongSectionType.VERSE -> "Куплет $number"
            SongSectionType.CHORUS -> "Припев"
            SongSectionType.BRIDGE -> "Бридж"
            SongSectionType.INTRO -> "Интро"
            SongSectionType.ENDING -> "Окончание"
            SongSectionType.PRE_CHORUS -> "Предприпев"
            SongSectionType.TAG -> "Тег"
        }

    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.padding(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text("${section.type} ${section.number}")

            section.slides.forEach { slide ->
                SongSlideItem(slide)
            }
        }
    }
}