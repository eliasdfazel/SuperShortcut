/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 6/11/20 11:15 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.supershortcuts.PRO.Utils.Functions

import android.content.Context
import java.io.File
import java.nio.charset.Charset

class FunctionsClassIO (private val context: Context) {

    fun readFileLines(fileName: String) : Array<String>? {
        val file: File? = context.getFileStreamPath(fileName)

        return if (file != null) {
            if (file.exists() && file.isFile) {
                file.readLines(Charset.defaultCharset()).toTypedArray()
            } else {
                null
            }
        } else {
            null
        }
    }

    fun fileLinesCounter(fileName: String) : Int {
        val file: File? = context.getFileStreamPath(fileName)

        return if (file != null) {
            if (file.exists() && file.isFile) {
                file.readLines(Charset.defaultCharset()).size
            } else {
                0
            }
        } else {
            0
        }
    }

    fun readFile(fileName: String) : String? {
        val file: File? = context.getFileStreamPath(fileName)

        return if (file != null) {
            if (file.exists() && file.isFile) {
                file.readText(Charset.defaultCharset())
            } else {
                null
            }
        } else {
            null
        }
    }
}