package org.holypresenter_songs.domain.editor.command.slide

import org.holypresenter_songs.domain.Song
import org.holypresenter_songs.domain.SongOrderEntry
import org.holypresenter_songs.domain.SongSection
import org.holypresenter_songs.domain.SongSlide
import org.holypresenter_songs.domain.editor.command.SongEditCommand

class DeleteSlideCommand(
    section: SongSection,
    slide: SongSlide
) : SongEditCommand {
    override val description: String = "Удалить слайд"
    /*
     * Команда хранит идентификаторы, а не полагается
     * на полное равенство объектов data class.
     */
    private val sectionId = section.id
    private val slideId = slide.id
    private var originalSectionIndex: Int? = null
    private var originalSlideIndex: Int? = null
    private var deletedSlide: SongSlide? = null
    private var sectionBeforeDeletion: SongSection? = null

    /*
     * true, когда удалённый слайд был последним
     * и вместе с ним исчезла вся секция.
     */
    private var removedWholeSection: Boolean = false

    /*
     * При удалении всей секции временно удаляются
     * и её вхождения из порядка исполнения.
     */
    private var removedOrderEntries: List<IndexedOrderEntry>? = null
    private var initialized: Boolean = false

    override fun execute(
        song: Song
    ): Song {
        val currentSectionIndex =
            song.sections.indexOfFirst { currentSection ->
                currentSection.id == sectionId
            }

        if (currentSectionIndex == -1) {
            return song
        }

        val currentSection = song.sections[currentSectionIndex]

        val currentSlideIndex =
            currentSection.slides.indexOfFirst { currentSlide ->
                currentSlide.id == slideId
            }

        if (currentSlideIndex == -1) {
            return song
        }

        /*
         * Состояние запоминается только при первом
         * выполнении. При Redo используются те же
         * данные и идентификаторы.
         */
        if (!initialized) {
            originalSectionIndex = currentSectionIndex
            originalSlideIndex = currentSlideIndex
            deletedSlide = currentSection.slides[currentSlideIndex]
            sectionBeforeDeletion = currentSection
            removedWholeSection = currentSection.slides.size == 1

            if (removedWholeSection) {
                removedOrderEntries =
                    song.executionOrder
                        .mapIndexedNotNull { index, entry ->
                            if (entry.sectionId == sectionId) {
                                IndexedOrderEntry(
                                    index = index,
                                    entry = entry
                                )
                            } else {
                                null
                            }
                        }
            }
            initialized = true
        }

        val updatedSlides = currentSection.slides.toMutableList()

        updatedSlides.removeAt(
            currentSlideIndex
        )

        val updatedSections = song.sections.toMutableList()

        val updatedOrder =
            if (updatedSlides.isEmpty()) {
                /*
                 * Пустую секцию пока не оставляем
                 * в структуре песни.
                 */
                updatedSections.removeAt(
                    currentSectionIndex
                )

                song.executionOrder.filterNot { entry ->
                    entry.sectionId == sectionId
                }
            } else {
                updatedSections[currentSectionIndex] =
                    currentSection.copy(
                        slides = updatedSlides
                    )

                song.executionOrder
            }

        return song.copy(
            sections = updatedSections,
            executionOrder = updatedOrder
        )
    }

    override fun undo(
        song: Song
    ): Song {
        val slideToRestore = deletedSlide ?: return song
        val savedSectionIndex = originalSectionIndex ?: return song
        val savedSlideIndex = originalSlideIndex ?: return song

        val currentSectionIndex =
            song.sections.indexOfFirst { currentSection ->
                currentSection.id == sectionId
            }

        val updatedSections = song.sections.toMutableList()

        if (currentSectionIndex == -1) {
            /*
             * Секция исчезла при удалении последнего
             * слайда. Восстанавливаем её целиком.
             */
            val sectionToRestore = sectionBeforeDeletion ?: return song

            updatedSections.add(
                index = savedSectionIndex.coerceIn(
                    minimumValue = 0,
                    maximumValue = updatedSections.size
                ),
                element = sectionToRestore
            )
        } else {
            /*
             * Секция существует — возвращаем только
             * удалённый слайд.
             */
            val currentSection = updatedSections[currentSectionIndex]

            val slideAlreadyExists =
                currentSection.slides.any { currentSlide ->
                    currentSlide.id == slideId
                }

            if (!slideAlreadyExists) {
                val updatedSlides = currentSection.slides.toMutableList()

                updatedSlides.add(
                    index = savedSlideIndex.coerceIn(
                        minimumValue = 0,
                        maximumValue = updatedSlides.size
                    ),
                    element = slideToRestore
                )

                updatedSections[currentSectionIndex] =
                    currentSection.copy(
                        slides = updatedSlides
                    )
            }
        }

        val updatedOrder = song.executionOrder.toMutableList()

        /*
         * Вхождения нужно возвращать только тогда,
         * когда при удалении слайда исчезла вся секция.
         */
        if (removedWholeSection) {
            removedOrderEntries
                .orEmpty()
                .sortedBy { indexedEntry ->
                    indexedEntry.index
                }
                .forEach { indexedEntry ->
                    val alreadyExists =
                        updatedOrder.any { currentEntry ->
                            currentEntry.id == indexedEntry.entry.id
                        }

                    if (!alreadyExists) {
                        updatedOrder.add(
                            index = indexedEntry.index
                                .coerceIn(
                                    minimumValue = 0,
                                    maximumValue =
                                        updatedOrder.size
                                ),
                            element = indexedEntry.entry
                        )
                    }
                }
        }

        return song.copy(
            sections = updatedSections,
            executionOrder = updatedOrder
        )
    }

    private data class IndexedOrderEntry(
        val index: Int,
        val entry: SongOrderEntry
    )
}