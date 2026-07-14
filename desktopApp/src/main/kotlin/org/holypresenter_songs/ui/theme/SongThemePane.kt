package org.holypresenter_songs.ui.theme

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.holypresenter_songs.domain.SongBackground
import org.holypresenter_songs.domain.editor.command.theme.UpdateBackgroundCommand
import org.holypresenter_songs.domain.editor.command.theme.UpdateFontFamilyCommand
import org.holypresenter_songs.domain.editor.command.theme.UpdateFontSizeCommand
import org.holypresenter_songs.domain.editor.command.theme.UpdateOverlayCommand
import org.holypresenter_songs.presentation.SongEditorContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongThemePane(
    context: SongEditorContext,
    modifier: Modifier = Modifier
) {
    val song = context.state.song
    val opacity = song?.theme?.overlay?.opacity ?: 0.45f
    val fontSize = song?.theme?.textStyle?.fontSize ?: 64
    var fontMenuExpanded by remember { mutableStateOf(false) }

    var overlaySliderValue by remember {
        mutableFloatStateOf(opacity)
    }

    var overlayStartValue by remember {
        mutableFloatStateOf(opacity)
    }

    var fontSizeSliderValue by remember {
        mutableFloatStateOf(fontSize.toFloat())
    }

    var fontSizeStartValue by remember {
        mutableIntStateOf(fontSize)
    }

    LaunchedEffect(fontSize) {
        fontSizeSliderValue = fontSize.toFloat()
        fontSizeStartValue = fontSize
    }

    val fontFamilies = remember {
        listOf(
            "Arial",
            "Calibri",
            "Segoe UI",
            "Tahoma",
            "Verdana",
            "Times New Roman"
        )
    }

    val fontFamily =
        song?.theme?.textStyle?.fontFamily ?: "Arial"

    Card(
        modifier = modifier.fillMaxHeight()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Тема оформления",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.height(16.dp))

            Text("Фон")

            Spacer(Modifier.height(8.dp))

            OutlinedButton(
                onClick = {
                    val currentSong =
                        context.state.song ?: return@OutlinedButton

                    val file =
                        FileChooser.chooseImage() ?: return@OutlinedButton

                    context.editor.execute(
                        UpdateBackgroundCommand(
                            oldBackground = currentSong.theme.background,
                            newBackground = SongBackground.Image(
                                file.absolutePath
                            )
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Выбрать изображение")
            }

            Spacer(Modifier.height(24.dp))

            Text("Шрифт")

            ExposedDropdownMenuBox(
                expanded = fontMenuExpanded,
                onExpandedChange = {
                    fontMenuExpanded = !fontMenuExpanded
                }
            ) {
                OutlinedTextField(
                    value = fontFamily,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = fontMenuExpanded
                        )
                    }
                )

                DropdownMenu(
                    expanded = fontMenuExpanded,
                    onDismissRequest = {
                        fontMenuExpanded = false
                    }
                ) {
                    fontFamilies.forEach { family ->
                        DropdownMenuItem(
                            text = {
                                Text(family)
                            },
                            onClick = {
                                val currentSong =
                                    context.state.song
                                        ?: return@DropdownMenuItem

                                context.editor.execute(
                                    UpdateFontFamilyCommand(
                                        oldFontFamily =
                                            currentSong.theme.textStyle.fontFamily,
                                        newFontFamily = family
                                    )
                                )
                                fontMenuExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Overlay: ${(overlaySliderValue * 100).toInt()}%"
            )

            Slider(
                value = overlaySliderValue,
                valueRange = 0f..1f,
                onValueChange = { newValue ->
                    if (overlaySliderValue == opacity) {
                        overlayStartValue = opacity
                    }

                    overlaySliderValue = newValue
                },
                onValueChangeFinished = {
                    val currentSong = context.state.song ?: return@Slider
                    val newOpacity = overlaySliderValue.coerceIn(0f, 1f)

                    if (newOpacity != overlayStartValue) {
                        context.editor.execute(
                            UpdateOverlayCommand(
                                oldOpacity = overlayStartValue,
                                newOpacity = newOpacity
                            )
                        )
                    }
                    overlayStartValue = newOpacity
                }
            )

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Размер текста: ${fontSizeSliderValue.toInt()} pt"
            )

            Slider(
                value = fontSizeSliderValue,
                valueRange = 24f..120f,
                steps = 95,
                onValueChange = { newValue ->
                    val newSize = newValue.toInt()

                    if (fontSizeSliderValue.toInt() == fontSize) {
                        fontSizeStartValue = fontSize
                    }

                    fontSizeSliderValue = newSize.toFloat()
                    context.state.previewFontSize = newSize
                },
                onValueChangeFinished = {
                    val currentSong = context.state.song ?: return@Slider
                    val newSize = fontSizeSliderValue
                        .toInt()
                        .coerceIn(24, 120)

                    context.state.previewFontSize = null

                    if (newSize != fontSizeStartValue) {
                        context.editor.execute(
                            UpdateFontSizeCommand(
                                oldSize = fontSizeStartValue,
                                newSize = newSize
                            )
                        )
                    }
                    fontSizeStartValue = newSize
                }
            )
        }
    }
}