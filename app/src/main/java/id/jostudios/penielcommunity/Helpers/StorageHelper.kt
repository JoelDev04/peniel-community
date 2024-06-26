package id.jostudios.penielcommunity.Helpers

import android.net.Uri
import id.jostudios.penielcommunity.Objects.GlobalState
import id.jostudios.penielcommunity.Objects.System
import kotlinx.coroutines.tasks.await
import java.util.Date

object StorageHelper {
    public suspend fun getUserProfilePicture(name: String): Uri? {
        try {
            val image = FirebaseHelper.getStorageReference().child("profiles/" + name);
            val downloadUrl = image.downloadUrl.await();

            return downloadUrl;
        } catch (e: Exception) {
            System.debug("Get Profile Error : ${e.message.toString()}");
            return null;
        }
    }



    public suspend fun getBanner(name: String): Uri? {
        try {
            val image = FirebaseHelper.getStorageReference().child("banners/" + name);
            val downloadUrl = image.downloadUrl.await();

            return downloadUrl;
        } catch (e: Exception) {
            System.debug("Get Profile Error : ${e.message.toString()}");
            return null;
        }
    }

    public suspend fun uploadFeedImage(imgUri: Uri): Uri? {
        val date = Date();
        val uploadPath = FirebaseHelper.getStorageReference().child("feeds/content_${GlobalState.currentUser?.name}_${date.time}");
        val uploadTask = uploadPath.putFile(imgUri).await();
        val uriPath = uploadTask.storage.downloadUrl.await();

        return uriPath;
    }
}