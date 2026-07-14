package org.holypresenter_songs.ui.theme

import java.io.File
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

object FileChooser {
    fun chooseImage(): File? {
        val chooser = JFileChooser().apply {
            dialogTitle = "Выберите изображение"

            fileFilter = FileNameExtensionFilter(
                "Изображения",
                "png",
                "jpg",
                "jpeg",
                "bmp",
                "webp"
            )
        }

        return if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            chooser.selectedFile
        } else {
            null
        }
    }
}