package org.holypresenter_songs.ui.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import holypresenter.org.platform.api.projection.ProjectionContent
import holypresenter.org.platform.api.projection.ProjectionService
import org.holypresenter.platform.ui.presenter.HolyPresenterSectionHeader
import org.holypresenter_songs.domain.Song
import org.holypresenter_songs.domain.SongSectionType
import org.holypresenter_songs.domain.SongSlide
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import java.awt.KeyEventDispatcher
import java.awt.KeyboardFocusManager
import java.awt.event.KeyEvent
import org.holypresenter.platform.ui.presenter.HolyProjectionControls
import org.holypresenter.platform.ui.presenter.HolyProjectionShortcutsHint
import org.holypresenter.platform.ui.presenter.HolyProjectionToolbar

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
    val projectionService = remember(moduleContext) {
        moduleContext.services.get(
            ProjectionService::class
        )
    }

    val projectionState = projectionService
        ?.state
        ?.collectAsState()

    val isBlackScreen = projectionState
        ?.value
        ?.content == ProjectionContent.BlackScreen

    val isTextHidden =
        projectionState
            ?.value
            ?.textVisible == false

    val slides = remember(song) {
        song.sections.flatMap { section ->
            section.slides
        }
    }

    var selectedSlideIndex by remember(song.id) {
        mutableStateOf<Int?>(null)
    }

    fun showSlide(index: Int): Boolean {
        if (slides.isEmpty()) {
            return false
        }

        val safeIndex = index.coerceIn(
            minimumValue = 0,
            maximumValue = slides.lastIndex
        )

        selectedSlideIndex = safeIndex

        onSlideClick(
            slides[safeIndex],
            safeIndex
        )
        return true
    }

    fun showPreviousSlide(): Boolean {
        val currentIndex =
            selectedSlideIndex ?: 0

        return showSlide(
            index = (currentIndex - 1)
                .coerceAtLeast(0)
        )
    }

    fun showNextSlide(): Boolean {
        val currentIndex = selectedSlideIndex ?: -1

        return showSlide(
            index = (currentIndex + 1)
                .coerceAtMost(slides.lastIndex)
        )
    }

    DisposableEffect(
        song.id,
        slides.size
    ) {
        val keyboardManager = KeyboardFocusManager
            .getCurrentKeyboardFocusManager()

        val dispatcher = KeyEventDispatcher { event ->
            if (event.id != KeyEvent.KEY_PRESSED) {
                return@KeyEventDispatcher false
            }

            when (event.keyCode) {
                KeyEvent.VK_RIGHT,
                KeyEvent.VK_DOWN,
                KeyEvent.VK_PAGE_DOWN,
                KeyEvent.VK_SPACE -> {
                    showNextSlide()
                }

                KeyEvent.VK_LEFT,
                KeyEvent.VK_UP,
                KeyEvent.VK_PAGE_UP -> {
                    showPreviousSlide()
                }

                KeyEvent.VK_ESCAPE -> {
                    projectionService?.close()
                    true
                }

                KeyEvent.VK_B -> {
                    projectionService?.toggleBlackScreen()
                    true
                }

                KeyEvent.VK_C -> {
                    projectionService
                        ?.toggleTextVisibility()

                    true
                }
                else -> false
            }
        }

        keyboardManager.addKeyEventDispatcher(
            dispatcher
        )

        onDispose {
            keyboardManager.removeKeyEventDispatcher(
                dispatcher
            )
        }
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
            TextButton(
                onClick = onBackClick
            ) {
                Text("← Библиотека")
            }

            TextButton(
                onClick = onEditClick
            ) {
                Text("Редактировать")
            }

            Text(
                text = song.metadata.title,
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                enabled = slides.isNotEmpty(),
                onClick = {
                    showPreviousSlide()
                }
            ) {
                Text("← Предыдущий")
            }

            Button(
                enabled = slides.isNotEmpty(),
                onClick = {
                    showNextSlide()
                }
            ) {
                Text("Следующий →")
            }

            Spacer(
                modifier = Modifier.weight(1f)
            )

            HolyProjectionToolbar(
                isBlackScreen = isBlackScreen,
                isTextHidden = isTextHidden,
                enabled = projectionService != null,
                onToggleBlackScreen = {
                    projectionService
                        ?.toggleBlackScreen()
                },
                onToggleTextVisibility = {
                    projectionService
                        ?.toggleTextVisibility()
                },
                onCloseProjection = {
                    projectionService
                        ?.close()
                }
            )
        }

        HorizontalDivider(
            modifier = Modifier.padding(
                vertical = 12.dp
            )
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
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
                        modifier =
                            Modifier.padding(top = 8.dp)
                    )
                }

                section.slides.forEach { slide ->
                    val currentIndex = globalIndex
                    globalIndex++

                    item(key = "${song.id.value}-$currentIndex") {
                        SongPresenterSlideCard(
                            slide = slide,
                            number = currentIndex + 1,
                            selected = selectedSlideIndex == currentIndex,
                            onClick = {
                                showSlide(currentIndex)
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