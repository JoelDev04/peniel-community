package id.jostudios.penielcommunity.Helpers

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.net.toUri
import id.jostudios.penielcommunity.Objects.System
import java.io.File
import java.io.FileOutputStream

object BitmapHelper {
    public fun isLandscape(image: Bitmap): Boolean {
        return image.width > image.height;
    }

    public fun resizeImage(image: Bitmap, width: Int, height: Int): Bitmap {
        val aspectRatio = image.width.toFloat() / image.height.toFloat();

        var newWidth = width
        var newHeight = height

        if (aspectRatio > 1) {
            newHeight = (width / aspectRatio).toInt()
        } else {
            newWidth = (height * aspectRatio).toInt()
        }

        return Bitmap.createScaledBitmap(image, newWidth, newHeight, true);
    }

    public fun compressBitmap(context: Context, img: Bitmap): Uri? {
        val tempFile = File(context.cacheDir, "temp_image_compressed.jpeg");
        System.debug("Temp file : ${tempFile.absolutePath}");

        val tempOStream = FileOutputStream(tempFile);


        if (!img.compress(Bitmap.CompressFormat.JPEG, 98, tempOStream)) {
            return null;
        }

        tempOStream.flush();
        tempOStream.close();

        return tempFile.toUri();
    }
}