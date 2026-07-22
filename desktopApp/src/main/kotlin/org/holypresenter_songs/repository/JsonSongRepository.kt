package org.holypresenter_songs.repository

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.holypresenter_songs.domain.Song
import org.holypresenter_songs.domain.SongId
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption

class JsonSongRepository(
    private val songsDirectory: File
) : SongRepository {
    private val json = Json {
        prettyPrint = true
        encodeDefaults = true
        ignoreUnknownKeys = true
        classDiscriminator = "type"
    }

    init {
        if (!songsDirectory.exists()) {
            songsDirectory.mkdirs()
        }
    }

    override fun getAll(): List<Song> =
        songsDirectory
            .listFiles { file ->
                file.isFile &&
                        file.extension.equals("json", ignoreCase = true)
            }
            .orEmpty()
            .mapNotNull(::readSong)
            .sortedBy { song ->
                song.metadata.title.lowercase()
            }

    override fun findById(id: SongId): Song? {
        val file = songFile(id)

        if (!file.exists()) {
            return null
        }

        return readSong(file)
    }

    override fun save(song: Song) {
        songsDirectory.mkdirs()

        val targetFile = songFile(song.id)
        val temporaryFile = File(
            songsDirectory,
            "${song.id.value}.json.tmp"
        )

        val serializedSong = json.encodeToString(song)

        temporaryFile.writeText(
            text = serializedSong,
            charset = Charsets.UTF_8
        )

        /*
         * Сначала пишем во временный файл, затем атомарно заменяем основной.
         * Так существующая песня не повредится при сбое во время записи.
         */
        runCatching {
            Files.move(
                temporaryFile.toPath(),
                targetFile.toPath(),
                StandardCopyOption.REPLACE_EXISTING,
                StandardCopyOption.ATOMIC_MOVE
            )
        }.recoverCatching {
            /*
             * Не все файловые системы поддерживают ATOMIC_MOVE.
             */
            Files.move(
                temporaryFile.toPath(),
                targetFile.toPath(),
                StandardCopyOption.REPLACE_EXISTING
            )
        }.getOrElse { error ->
            temporaryFile.delete()

            throw IllegalStateException(
                "Не удалось сохранить песню: ${targetFile.absolutePath}",
                error
            )
        }
    }

    override fun delete(id: SongId) {
        val file = songFile(id)

        if (file.exists() && !file.delete()) {
            error(
                "Не удалось удалить песню: ${file.absolutePath}"
            )
        }
    }

    private fun readSong(file: File): Song? =
        runCatching {
            json.decodeFromString<Song>(
                file.readText(Charsets.UTF_8)
            )
        }.onFailure { error ->
            println(
                "Не удалось прочитать песню ${file.name}: ${error.message}"
            )
        }.getOrNull()

    private fun songFile(id: SongId): File =
        File(
            songsDirectory,
            "${id.value}.json"
        )
}