package io.legado.app.help

import io.legado.app.App
import io.legado.app.data.entities.Book
import io.legado.app.data.entities.BookChapter
import io.legado.app.utils.getPrefString
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.nio.charset.StandardCharsets

object BookHelp {

    private var downloadPath = App.INSTANCE.getPrefString("downloadPath") ?: App.INSTANCE.getExternalFilesDir(null)

    fun upDownloadPath() {
        downloadPath = App.INSTANCE.getPrefString("downloadPath") ?: App.INSTANCE.getExternalFilesDir(null)
    }

    fun saveContent(book: Book, bookChapter: BookChapter, content: String) {
        if (content.isEmpty()) {
            return
        }
        val filePath = getChapterPath(book, bookChapter)
        val file = FileHelp.getFile(filePath)
        //获取流并存储
        try {
            BufferedWriter(FileWriter(file)).use { writer ->
                writer.write(content)
                writer.flush()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    fun hasContent(book: Book, bookChapter: BookChapter): Boolean {
        val filePath = getChapterPath(book, bookChapter)
        runCatching {
            val file = File(filePath)
            if (file.exists()) {
                return true
            }
        }
        return false
    }

    fun getContent(book: Book, bookChapter: BookChapter): String? {
        val filePath = getChapterPath(book, bookChapter)
        runCatching {
            val file = File(filePath)
            if (file.exists()) {
                return String(file.readBytes(), StandardCharsets.UTF_8)
            }
        }
        return null
    }

    private fun getChapterPath(book: Book, bookChapter: BookChapter): String {
        val bookFolder = formatFolderName(book.name + book.bookUrl)
        val chapterFile = String.format("%05d-%s", bookChapter.index, formatFolderName(bookChapter.title))
        return "$downloadPath${File.separator}book_cache${File.separator}$bookFolder${File.separator}$chapterFile.nb"
    }

    private fun formatFolderName(folderName: String): String {
        return folderName.replace("/", "")
            .replace(":", "")
            .replace(".", "")
    }

    fun formatAuthor(author: String?): String {
        return author
            ?.replace("作\\s*者[\\s:：]*".toRegex(), "")
            ?.replace("\\s+".toRegex(), " ")
            ?.trim { it <= ' ' }
            ?: ""
    }

}