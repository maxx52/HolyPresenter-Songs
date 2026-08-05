package org.holypresenter_songs.domain

import holypresenter.org.platform.api.model.HolyId
import holypresenter.org.platform.api.model.HolyIds
import kotlinx.serialization.Serializable

@Serializable
data class SongSlide(
    val id: HolyId = HolyIds.newId(),

    /**
     * Строки текста песни.
     */
    val lines: List<String>,

    /**
     * Аккорды, соответствующие строкам текста.
     *
     * Индекс аккордной строки совпадает
     * с индексом строки в lines.
     *
     * null означает, что над строкой
     * текста аккордов нет.
     *
     * Пустой список означает, что песня
     * не содержит сохранённых аккордов.
     */
    val chords: List<String?> = emptyList()
) {
    init {
        require(
            chords.isEmpty() || chords.size == lines.size
        ) {
            "Chord lines count must match lyrics lines count"
        }
    }

    fun chordsForLine(
        lineIndex: Int
    ): String? =
        chords.getOrNull(lineIndex)
}