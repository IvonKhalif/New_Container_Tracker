package com.example.containertracker.utils.media

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import java.io.*

object FileUtil {
    private const val EOF = -1
    private const val DEFAULT_BUFFER_SIZE = 1024 * 4

    fun from(context: Context, uri: Uri): File {
        val inputStream = context.contentResolver.openInputStream(uri)
        val fileName = getFileName(context, uri)
        val splitName = splitFileName(fileName)
        var tempFile = File.createTempFile(splitName[0], splitName[1])
        var out: FileOutputStream? = null

        tempFile = rename(tempFile, fileName)
        tempFile.deleteOnExit()

        try {
            out = FileOutputStream(tempFile)

            if (inputStream != null) {
                copy(inputStream, out)
                inputStream.close()
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } finally {
            out?.close()
        }

        return tempFile
    }

    private fun splitFileName(fileName: String): Array<String> {
        var name = fileName
        var extension = ""
        val i = fileName.lastIndexOf(".")

        if (i != -1) {
            name = fileName.substring(0, i)
            extension = fileName.substring(i)
        }

        return arrayOf(name, extension)
    }

    @SuppressLint("Range")
    private fun getFileName(context: Context, uri: Uri): String {
        var result: String? = null

        if (uri.scheme == "content") {
            try {
                context.contentResolver.query(
                    uri, null, null, null, null
                ).use { cursor ->
                    if (cursor != null && cursor.moveToFirst()) {
                        result = cursor.getString(
                            cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf(File.separator) ?: 0

            if (cut != -1) {
                result = result?.substring(cut + 1)
            }
        }

        return result.orEmpty()
    }

    private fun rename(file: File, newName: String): File {
        val newFile = File(file.parent, newName)
        if (newFile != file) {
            if (newFile.exists() && newFile.delete()) {
                Log.d("FileUtil", "Delete old $newName file")
            }
            if (file.renameTo(newFile)) {
                Log.d("FileUtil", "Rename file to $newName")
            }
        }
        return newFile
    }

    @Throws(IOException::class)
    private fun copy(input: InputStream, output: OutputStream): Long {
        var count: Long = 0
        var n: Int
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)

        while (EOF != input.read(buffer).also { n = it }) {
            output.write(buffer, 0, n)
            count += n.toLong()
        }

        return count
    }

    fun isImageFileType(uri: Uri?, context: Context) : Boolean {
        val fileType = getMimeTypeFromUri(uri, context)
        val fileExtension = uri.toString().substring(uri.toString().lastIndexOf(".") + 1)
        return fileType.contains("image", true) || fileExtension.contains("jpeg", true) ||
                fileExtension.contains("jpg", true) || fileExtension.contains("png", true)
    }

    fun getMimeTypeFromUri(uri: Uri?, context: Context): String {
        return if (uri == null) {
            ""
        } else {
            val cR: ContentResolver = context.contentResolver
            cR.getType(uri) ?: ""
        }
    }
}