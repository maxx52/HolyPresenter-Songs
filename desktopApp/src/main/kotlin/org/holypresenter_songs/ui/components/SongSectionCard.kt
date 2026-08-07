package org.holypresenter_songs.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.holypresenter_songs.domain.SongSection
import org.holypresenter_songs.domain.SongSlide
import org.holypresenter_songs.ui.common.color
import androidx.compose.runtime.key

@Composable
fun SongSectionCard(
    section: SongSection,
    modifier: Modifier = Modifier,
    onAddSlide: (SongSection) -> Unit = {},
    selectedSlide: SongSlide? = null,
    onSlideSelected: (SongSlide) -> Unit = {},
    onSlideChanged: (SongSlide, String) -> Unit = { _, _ -> },
    onSlideChordsChanged: (SongSlide, String) -> Unit = { _, _ -> },
    onSlideSplit: (SongSlide, Int) -> Unit = { _, _ -> },
    onDuplicateSlide: (SongSection, SongSlide) -> Unit = { _, _ -> },
    onDeleteSlide: (SongSection, SongSlide) -> Unit = { _, _ -> },
    onDuplicateSection: (SongSection) -> Unit = {},
    onDeleteSection: (SongSection) -> Unit = {},
    onSlideMergeWithPrevious: (SongSlide) -> Unit = {},
    onAddToOrder: (SongSection) -> Unit = {},
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
                SongSectionHeader(
                    section = section,
                    onAddToOrder = {
                        onAddToOrder(section)
                    },
                    onDuplicate = {
                        onDuplicateSection(section)
                    },
                    onDelete = {
                        onDeleteSection(section)
                    }
                )

                Spacer(Modifier.height(18.dp))

                section.slides.forEachIndexed { slideIndex, slide ->
                    key(slide.id) {
                        SongSlideCard(
                            slide = slide,
                            selected = slide.id == selectedSlide?.id,
                            onSelect = {
                                onSlideSelected(slide)
                            },
                            onTextChange = {
                                onSlideChanged(slide, it)
                            },
                            onChordsChange = { chordsText ->
                                onSlideChordsChanged(
                                    slide,
                                    chordsText
                                )
                            },
                            onSplit = { splitOffset ->
                                onSlideSplit(
                                    slide,
                                    splitOffset
                                )
                            },
                            canMergeWithPrevious = slideIndex > 0,
                            onMergeWithPrevious = {
                                onSlideMergeWithPrevious(slide)
                            },
                            onDelete = {
                                onDeleteSlide(section, slide)
                            },
                            onDuplicate = {
                                onDuplicateSlide(section, slide)
                            }
                        )

                        Spacer(
                            modifier = Modifier.height(12.dp)
                        )
                    }
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