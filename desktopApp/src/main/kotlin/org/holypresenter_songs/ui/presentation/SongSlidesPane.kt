package org.holypresenter_songs.ui.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import holypresenter.org.platform.api.module.ModuleContext
import holypresenter.org.platform.api.projection.ProjectionContent
import holypresenter.org.platform.api.projection.ProjectionService
import org.holypresenter.platform.ui.presenter.HolyProjectionToolbar
import org.holypresenter_songs.domain.Song
import org.holypresenter_songs.domain.SongSlide
import org.holypresenter_songs.domain.executionSections
import org.holypresenter_songs.ui.common.accentColor
import java.awt.KeyEventDispatcher
import java.awt.KeyboardFocusManager
import java.awt.event.KeyEvent

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

    val isBlackScreen =
        projectionState
            ?.value
            ?.content == ProjectionContent.BlackScreen

    val isTextHidden =
        projectionState
            ?.value
            ?.textVisible == false

    val executionSections =
        remember(song) {
            song.executionSections()
        }

    val slides =
        remember(song) {
            executionSections.flatMap { executionSection ->
                executionSection.section.slides
            }
        }

    var selectedSlideIndex by remember(song.id) {
        mutableStateOf<Int?>(null)
    }

    val slidesListState = rememberLazyListState()

    LaunchedEffect(selectedSlideIndex) {
        val slideIndex =
            selectedSlideIndex
                ?: return@LaunchedEffect

        val lazyItemIndex =
            lazyItemIndexForSlide(
                song = song,
                globalSlideIndex = slideIndex
            )

        slidesListState.animateScrollToItem(
            index = lazyItemIndex
        )
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
        val currentIndex =
            selectedSlideIndex ?: -1

        return showSlide(
            index = (currentIndex + 1)
                .coerceAtMost(slides.lastIndex)
        )
    }

    DisposableEffect(
        song.id,
        slides.size
    ) {
        val keyboardManager =
            KeyboardFocusManager
                .getCurrentKeyboardFocusManager()

        val dispatcher =
            KeyEventDispatcher { event ->
                if (
                    event.id !=
                    KeyEvent.KEY_PRESSED
                ) {
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
                        projectionService
                            ?.toggleBlackScreen()

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
            verticalAlignment =
                Alignment.CenterVertically,
            horizontalArrangement =
                Arrangement.spacedBy(8.dp)
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
                style =
                    MaterialTheme.typography.titleLarge
            )

            Spacer(
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            verticalAlignment =
                Alignment.CenterVertically,
            horizontalArrangement =
                Arrangement.spacedBy(8.dp)
        ) {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                val useSingleRow = maxWidth >= 980.dp

                if (useSingleRow) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement =
                            Arrangement.spacedBy(8.dp)
                    ) {
                        SlideNavigationControls(
                            enabled = slides.isNotEmpty(),
                            onPrevious = {
                                showPreviousSlide()
                            },
                            onNext = {
                                showNextSlide()
                            }
                        )

                        Spacer(
                            modifier = Modifier.weight(1f)
                        )

                        HolyProjectionToolbar(
                            isBlackScreen = isBlackScreen,
                            isTextHidden = isTextHidden,
                            enabled = projectionService != null,
                            compact = false,
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
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement =
                            Arrangement.spacedBy(8.dp)
                    ) {
                        SlideNavigationControls(
                            enabled = slides.isNotEmpty(),
                            onPrevious = {
                                showPreviousSlide()
                            },
                            onNext = {
                                showNextSlide()
                            }
                        )

                        HolyProjectionToolbar(
                            isBlackScreen = isBlackScreen,
                            isTextHidden = isTextHidden,
                            enabled = projectionService != null,
                            compact = false,
                            modifier = Modifier.align(
                                Alignment.End
                            ),
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
                }
            }
        }

        HorizontalDivider(
            modifier = Modifier.padding(
                vertical = 12.dp
            )
        )

        LazyColumn(
            state = slidesListState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            var globalIndex = 0

            executionSections.forEach { executionSection ->
                val section = executionSection.section

                item(
                    key = "section-${executionSection.entry.id}"
                ) {
                    SongPresenterSectionHeader(
                        section = section,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                section.slides.forEach { slide ->
                    val currentIndex = globalIndex
                    globalIndex++

                    item(
                        key = "${song.id.value}-${executionSection.entry.id}-$currentIndex"
                    ) {
                        SongPresenterSlideCard(
                            slide = slide,
                            number = currentIndex + 1,
                            selected = selectedSlideIndex == currentIndex,
                            accentColor = section.type.accentColor(),
                            onClick = { showSlide(currentIndex)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SlideNavigationControls(
    enabled: Boolean,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment =
            Alignment.CenterVertically,
        horizontalArrangement =
            Arrangement.spacedBy(8.dp)
    ) {
        OutlinedButton(
            enabled = enabled,
            onClick = onPrevious
        ) {
            Text(
                text = "← Предыдущий",
                maxLines = 1,
                softWrap = false
            )
        }

        Button(
            enabled = enabled,
            onClick = onNext
        ) {
            Text(
                text = "Следующий →",
                maxLines = 1,
                softWrap = false
            )
        }
    }
}

private fun lazyItemIndexForSlide(
    song: Song,
    globalSlideIndex: Int
): Int {
    var remainingSlideIndex = globalSlideIndex
    var lazyItemIndex = 0

    song.executionSections()
        .forEach { executionSection ->
            val section = executionSection.section
            /*
             * Заголовок секции является
             * отдельным элементом LazyColumn.
             */
            lazyItemIndex++

            if (remainingSlideIndex < section.slides.size) {
                return lazyItemIndex + remainingSlideIndex
            }
            remainingSlideIndex -= section.slides.size
            lazyItemIndex += section.slides.size
        }
    return 0
}