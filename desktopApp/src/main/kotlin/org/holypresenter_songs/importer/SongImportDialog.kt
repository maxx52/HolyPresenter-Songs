package org.holypresenter_songs.importer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.holypresenter_songs.domain.SongSectionType
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import org.holypresenter_songs.importer.link.SongLinkImportPanel
import org.holypresenter_songs.importer.link.SongLinkImporter

@Composable
internal fun SongImportDialog(
    parser: SongTextParser,
    linkImporter: SongLinkImporter,
    onDismissRequest: () -> Unit,
    onImport: (SongImportDraft) -> Unit
) {
    var title by remember {
        mutableStateOf("")
    }

    var author by remember {
        mutableStateOf("")
    }

    var sourceText by remember {
        mutableStateOf("")
    }

    var maxLinesPerSlide by remember {
        mutableIntStateOf(
            SongTextParser.DEFAULT_MAX_LINES_PER_SLIDE
        )
    }

    val parsedDraft = remember(
        sourceText,
        maxLinesPerSlide
    ) {
        parser.parse(
            text = sourceText,
            maxLinesPerSlide = maxLinesPerSlide
        )
    }

    val slideCount =
        parsedDraft.sections.sumOf { section ->
            section.slides.size
        }

    var showChordsInPreview by remember {
        mutableStateOf(true)
    }

    AlertDialog(
        modifier = Modifier.width(980.dp),
        onDismissRequest = onDismissRequest,
        title = {
            Text("Импорт песни")
        },
        text = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(540.dp),
                horizontalArrangement =
                    Arrangement.spacedBy(20.dp)
            ) {
                ImportSourceColumn(
                    linkImporter = linkImporter,
                    title = title,
                    onTitleChange = { title = it },
                    author = author,
                    onAuthorChange = { author = it },
                    sourceText = sourceText,
                    onSourceTextChange = { sourceText = it },
                    maxLinesPerSlide = maxLinesPerSlide,
                    onMaxLinesPerSlideChange = {
                        maxLinesPerSlide = it
                    },
                    onPasteFromClipboard = {
                        readClipboardText()?.let { clipboardText ->
                            sourceText = clipboardText
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )

                ImportPreviewColumn(
                    draft = parsedDraft,
                    slideCount = slideCount,
                    showChords = showChordsInPreview,
                    onShowChordsChange = {
                        showChordsInPreview = it
                    },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
            }
        },
        confirmButton = {
            TextButton(
                enabled =
                    title.isNotBlank() &&
                            !parsedDraft.isEmpty,
                onClick = {
                    onImport(
                        parsedDraft.copy(
                            title = title.trim(),
                            author = author.trim()
                        )
                    )
                }
            ) {
                Text("Создать песню")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest
            ) {
                Text("Отмена")
            }
        }
    )
}

@Composable
private fun ImportSourceColumn(
    linkImporter: SongLinkImporter,
    title: String,
    onTitleChange: (String) -> Unit,
    author: String,
    onAuthorChange: (String) -> Unit,
    sourceText: String,
    onSourceTextChange: (String) -> Unit,
    maxLinesPerSlide: Int,
    onMaxLinesPerSlideChange: (Int) -> Unit,
    onPasteFromClipboard: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement =
            Arrangement.spacedBy(12.dp)
    ) {
        SongLinkImportPanel(
            importer = linkImporter,
            onLoaded = { data ->
                onTitleChange(data.title)
                onAuthorChange(data.author.orEmpty())
                onSourceTextChange(data.sourceText)
            },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = title,
            onValueChange = onTitleChange,
            label = {
                Text("Название")
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = author,
            onValueChange = onAuthorChange,
            label = {
                Text("Автор или исполнитель")
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement =
                Arrangement.spacedBy(8.dp),
            verticalAlignment =
                Alignment.CenterVertically
        ) {
            Text("Строк на слайде:")

            listOf(2, 3, 4).forEach { count ->
                if (maxLinesPerSlide == count) {
                    Button(
                        onClick = {
                            onMaxLinesPerSlideChange(count)
                        }
                    ) {
                        Text(count.toString())
                    }
                } else {
                    OutlinedButton(
                        onClick = {
                            onMaxLinesPerSlideChange(count)
                        }
                    ) {
                        Text(count.toString())
                    }
                }
            }
        }

        OutlinedButton(
            onClick = onPasteFromClipboard
        ) {
            Text("Вставить из буфера")
        }

        OutlinedTextField(
            value = sourceText,
            onValueChange = onSourceTextChange,
            label = {
                Text("Текст песни")
            },
            placeholder = {
                Text(
                    text =
                        "1 куплет:\n" +
                                "Первая строка\n" +
                                "Вторая строка\n\n" +
                                "Припев:\n" +
                                "Текст припева"
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
    }
}

@Composable
private fun ImportPreviewColumn(
    draft: SongImportDraft,
    slideCount: Int,
    showChords: Boolean,
    onShowChordsChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val containsChords =
        draft.sections.any { section ->
            section.slides.any { slide ->
                slide.chords.any { chords ->
                    !chords.isNullOrBlank()
                }
            }
        }

    Column(
        modifier = modifier,
        verticalArrangement =
            Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Предварительный просмотр",
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            text =
                "Блоков: ${draft.sections.size}, " +
                        "слайдов: $slideCount",
            style = MaterialTheme.typography.bodySmall
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Checkbox(
                checked = containsChords && showChords,
                enabled = containsChords,
                onCheckedChange = { checked ->
                    onShowChordsChange(checked)
                },
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary,
                    uncheckedColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    checkmarkColor = MaterialTheme.colorScheme.onPrimary,
                    disabledCheckedColor =
                        MaterialTheme.colorScheme.primary.copy(
                            alpha = 0.55f
                        ),
                    disabledUncheckedColor =
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(
                            alpha = 0.55f
                        )
                )
            )

            Text(
                text = if (containsChords) {
                    "Показывать аккорды в предпросмотре"
                } else {
                    "Аккорды в тексте не обнаружены"
                },
                style = MaterialTheme.typography.bodyMedium
            )
        }

        if (draft.isEmpty) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text =
                        "Вставьте текст песни,\n" +
                                "чтобы увидеть результат"
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement =
                    Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = draft.sections,
                    key = { section ->
                        section.id.value
                    }
                ) { section ->
                    Column(
                        verticalArrangement =
                            Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = sectionTitle(
                                type = section.type,
                                number = section.number
                            ),
                            style =
                                MaterialTheme.typography.titleSmall
                        )

                        section.slides.forEachIndexed {
                                slideIndex,
                                slide ->

                            Card(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier =
                                        Modifier.padding(12.dp),
                                    verticalArrangement =
                                        Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text =
                                            "Слайд ${slideIndex + 1}",
                                        style =
                                            MaterialTheme
                                                .typography
                                                .labelMedium
                                    )

                                    slide.lines.forEachIndexed {
                                            lineIndex,
                                            lyricsLine ->

                                        val chordLine =
                                            slide.chords
                                                .getOrNull(lineIndex)

                                        Column(
                                            verticalArrangement =
                                                Arrangement.spacedBy(
                                                    2.dp
                                                )
                                        ) {
                                            if (
                                                showChords &&
                                                !chordLine.isNullOrBlank()
                                            ) {
                                                Text(
                                                    text = chordLine,
                                                    style =
                                                        MaterialTheme
                                                            .typography
                                                            .bodySmall,
                                                    color =
                                                        MaterialTheme
                                                            .colorScheme
                                                            .primary
                                                )
                                            }

                                            if (
                                                lyricsLine.isNotBlank()
                                            ) {
                                                Text(
                                                    text = lyricsLine
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun readClipboardText(): String? =
    runCatching {
        Toolkit
            .getDefaultToolkit()
            .systemClipboard
            .getData(
                DataFlavor.stringFlavor
            ) as? String
    }.getOrNull()

private fun sectionTitle(
    type: SongSectionType,
    number: Int
): String {
    val name =
        when (type) {
            SongSectionType.VERSE ->
                "Куплет"

            SongSectionType.CHORUS ->
                "Припев"

            SongSectionType.PRE_CHORUS ->
                "Предприпев"

            SongSectionType.BRIDGE ->
                "Бридж"

            SongSectionType.TAG ->
                "Тег"

            SongSectionType.INTRO ->
                "Вступление"

            SongSectionType.ENDING ->
                "Финал"
        }

    return if (
        type == SongSectionType.VERSE ||
        number > 1
    ) {
        "$name $number"
    } else {
        name
    }
}