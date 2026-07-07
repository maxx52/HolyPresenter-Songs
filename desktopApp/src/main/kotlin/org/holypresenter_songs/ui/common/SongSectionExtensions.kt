package org.holypresenter_songs.ui.common

import androidx.compose.ui.graphics.Color
import org.holypresenter_songs.domain.SongSection
import org.holypresenter_songs.domain.SongSectionType

fun SongSection.title(): String =
    when (type) {
        SongSectionType.VERSE -> "Куплет $number"
        SongSectionType.CHORUS -> "Припев"
        SongSectionType.PRE_CHORUS -> "Предприпев"
        SongSectionType.BRIDGE -> "Бридж"
        SongSectionType.INTRO -> "Интро"
        SongSectionType.ENDING -> "Финал"
        SongSectionType.TAG -> "Тег"
    }

fun SongSectionType.shortName(): String =
    when (this) {
        SongSectionType.VERSE -> "К"
        SongSectionType.CHORUS -> "П"
        SongSectionType.PRE_CHORUS -> "ПП"
        SongSectionType.BRIDGE -> "Б"
        SongSectionType.INTRO -> "И"
        SongSectionType.ENDING -> "Ф"
        SongSectionType.TAG -> "Т"
    }

fun SongSectionType.color(): Color =
    when (this) {
        SongSectionType.VERSE -> Color(0xFF4F46E5)
        SongSectionType.CHORUS -> Color(0xFF16A34A)
        SongSectionType.PRE_CHORUS -> Color(0xFFF59E0B)
        SongSectionType.BRIDGE -> Color(0xFFEF4444)
        SongSectionType.INTRO -> Color(0xFF06B6D4)
        SongSectionType.ENDING -> Color(0xFF9333EA)
        SongSectionType.TAG -> Color(0xFF6B7280)
    }