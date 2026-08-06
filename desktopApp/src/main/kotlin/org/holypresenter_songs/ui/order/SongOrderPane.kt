package org.holypresenter_songs.ui.order

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.holypresenter.platform.ui.interaction.dragdrop.HolyReorderColumn
import org.holypresenter_songs.domain.editor.command.section.DeleteSectionCommand
import org.holypresenter_songs.domain.editor.command.section.MoveSectionCommand
import org.holypresenter_songs.presentation.SongEditorContext

@Composable
fun SongOrderPane(
    context: SongEditorContext,
    modifier: Modifier = Modifier
) {
    val song = context.state.song
    val sections = song?.sections.orEmpty()
    val selectedSectionId = context.state.selectedSection?.id

    Surface(
        modifier = modifier.fillMaxHeight(),
        tonalElevation = 1.dp,
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Порядок исполнения",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            HolyReorderColumn(
                items = sections,
                modifier = Modifier.weight(1f),
                onMove = { fromIndex, toIndex ->
                    context.editor.execute(
                        MoveSectionCommand(
                            fromIndex = fromIndex,
                            toIndex = toIndex
                        )
                    )
                }
            ) { section, index, _ ->
                SongOrderItem(
                    number = index + 1,
                    section = section,
                    selected = section.id == selectedSectionId,
                    onClick = {
                        /*
                         * Выбор секции автоматически
                         * выбирает её первый слайд.
                         *
                         * SongPreviewPane читает
                         * selectedSlide из состояния,
                         * поэтому предпросмотр обновится
                         * автоматически.
                         */
                        context.state.selectSection(section)
                    },
                    onDelete = {
                        val wasSelected = context.state.selectedSection?.id == section.id

                        /*
                         * Если удаляется выбранная секция,
                         * заранее запоминаем следующую.
                         *
                         * Если следующей нет —
                         * выбираем предыдущую.
                         */
                        val replacementSectionId =
                            if (wasSelected) {
                                when {
                                    index < sections.lastIndex -> sections[index + 1].id
                                    index > 0 -> sections[index - 1].id
                                    else -> null
                                }
                            } else {
                                null
                            }

                        context.editor.execute(
                            DeleteSectionCommand(section)
                        )

                        /*
                         * После выполнения команды используем
                         * уже обновлённые объекты секций.
                         */
                        if (wasSelected) {
                            val replacementSection =
                                replacementSectionId
                                    ?.let { sectionId ->
                                        context.state
                                            .song
                                            ?.sections
                                            ?.firstOrNull {
                                                it.id == sectionId
                                            }
                                    }

                            if (replacementSection != null) {
                                context.state.selectSection(
                                    replacementSection
                                )
                            }
                        }
                    }
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )
        }
    }
}