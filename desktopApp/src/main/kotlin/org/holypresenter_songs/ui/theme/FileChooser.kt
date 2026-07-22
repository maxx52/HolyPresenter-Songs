package org.holypresenter_songs.ui.theme

import org.holypresenter_songs.domain.SongBackground
import java.io.File
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

object FileChooser {
    fun chooseBackground(): File? {
        val chooser = JFileChooser().apply {
            dialogTitle = "Выберите изображение или видео"
            fileFilter = FileNameExtensionFilter(
                "Изображения и видео",
                "png",
                "jpg",
                "jpeg",
                "bmp",
                "webp",
                "mp4",
                "mkv",
                "avi",
                "mov",
                "webm",
                "mpeg",
                "mpg"
            )
        }
        return if (
            chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION
        ) {
            chooser.selectedFile
        } else {
            null
        }
    }
}

private val imageExtensions = setOf(
    "png",
    "jpg",
    "jpeg",
    "bmp",
    "webp"
)

private val videoExtensions = setOf(
    "mp4",
    "mkv",
    "avi",
    "mov",
    "webm",
    "mpeg",
    "mpg"
)

fun File.toSongBackground(): SongBackground? {
    val extension = extension.lowercase()

    return when (extension) {
        in imageExtensions -> SongBackground.Image(absolutePath)
        in videoExtensions -> SongBackground.Video(absolutePath)
        else -> null
    }
}