package org.holypresenter_songs.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.holypresenter_songs.domain.SongSection
import org.holypresenter_songs.domain.SongSlide
import org.holypresenter_songs.ui.common.color

@Composable
fun SongSectionItem(
    section: SongSection,
    modifier: Modifier = Modifier,
    onAddSlide: (SongSection) -> Unit = {},
    selectedSlide: SongSlide? = null,
    onSlideSelected: (SongSlide) -> Unit = {},
    onSlideChanged: (SongSlide, String) -> Unit = { _, _ -> }
) {
    Row(
        modifier = modifier.fillMaxWidth()
    ) {
        // Цветная полоса секции
        Box(
            modifier = Modifier
                .width(5.dp)
                .fillMaxHeight()
                .background(
                    section.type.color(),
                    RoundedCornerShape(
                        topStart = 16.dp,
                        bottomStart = 16.dp
                    )
                )
        )

        Surface(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(
                topEnd = 16.dp,
                bottomEnd = 16.dp
            ),
            tonalElevation = 1.dp
        ) {
            Column(
                modifier = Modifier.padding(18.dp)
            ) {
                SongSectionHeader(section)

                Spacer(Modifier.height(18.dp))

                section.slides.forEachIndexed { index, slide ->

                    SongSlideCard(
                        slide = slide,
                        selected = slide == selectedSlide,
                        onClick = {
                            onSlideSelected(slide)
                        },
                        onTextChanged = {
                            onSlideChanged(slide, it)
                        }
                    )

                    Spacer(Modifier.height(12.dp))
                }
                AddSlideButton(
                    onClick = {
                        onAddSlide(section)
                    }
                )
            }
        }
    }
}