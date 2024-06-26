package id.jostudios.penielcommunity.Helpers

import android.graphics.Bitmap
import android.net.Uri
import androidx.core.net.toUri
import id.jostudios.penielcommunity.Objects.System
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.net.URI

object FileHelper {
    public fun getFile(dir: String, fileName: String): File? {
        val file = File(dir, fileName);

        if (!file.exists()) {
            return null
        }

        return file;
    }

    public fun readFile(file: File): String? {
        return try {
            file.readText();
        } catch (e: Exception) {
            System.debug("Error : ${e}");
            null;
        }
    }

    public fun writeFile(file: File, data: String): Boolean {
        return try {
            file.writeText(data);
            true;
        } catch (e: Exception) {
            System.debug("Error : ${e}");
            false;
        }
    }

    public fun getLastModifiedFile(parentDir: File): Uri? {
        val files = parentDir.listFiles();

        if (files == null) { return null; }

        return files.maxByOrNull { it.lastModified(); }?.toUri();
    }
}