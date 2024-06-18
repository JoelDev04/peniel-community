package id.jostudios.penielcommunity.ViewModels

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import id.jostudios.penielcommunity.Enums.FeedType
import id.jostudios.penielcommunity.Helpers.DatabaseHelper
import id.jostudios.penielcommunity.Helpers.StorageHelper
import id.jostudios.penielcommunity.Models.FirebaseModels.FeedLikesModel
import id.jostudios.penielcommunity.Models.FirebaseModels.FeedPostModel
import id.jostudios.penielcommunity.Objects.GlobalState
import id.jostudios.penielcommunity.Objects.System
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.util.Date

class UploadViewModel: ViewModel() {
    private var m_selectedImage: MutableStateFlow<Uri?> = MutableStateFlow(null);
    private var m_caption: MutableStateFlow<String> = MutableStateFlow("");
    private var m_content: MutableStateFlow<String> = MutableStateFlow("");

    public fun selectedImage() = m_selectedImage.asStateFlow();
    public fun caption() = m_caption.asStateFlow();
    public fun content() = m_content.asStateFlow();

    public fun setSelectedImage(uri: Uri?) {
        m_selectedImage.value = uri;
    }

    public fun setCaption(value: String) {
        m_caption.value = value;
    }

    public fun setContent(value: String) {
        m_content.value = value;
    }

    public suspend fun uploadThread(context: Context) {
        System.showLoadingDialog(context);

        try {
            val tempFeed = FeedPostModel();
            tempFeed.thread = content().value;
            tempFeed.uploadTime = Date().time;
            tempFeed.caption = caption().value;
            tempFeed.feedID = tempFeed.uploadTime + (caption().value.hashCode() + content().value.hashCode());
            tempFeed.feedOwnerID = GlobalState.currentUser?.id!!;

            val tempFeedLikes = FeedLikesModel();
            tempFeedLikes.id = tempFeed.feedID;

            if (selectedImage().value != null) {
                val imgUri = StorageHelper.uploadFeedImage(selectedImage().value!!);
                tempFeed.feedType = FeedType.Photo;
                tempFeed.photoUrl = mutableListOf(imgUri.toString());
            } else {
                tempFeed.feedType = FeedType.Thread;
            }

//            DatabaseHelper.postFeedContainer(tempFeed);
//            DatabaseHelper.postFeedLike(tempFeed.feedID, tempFeedLikes);

            withContext(Dispatchers.IO) {
                Thread.sleep(2500)
            };

            System.destroyLoadingDialog();
        } catch (e: Exception) {
            System.dialogMessageBox(context, "Error",  e.message.toString());
            withContext(Dispatchers.IO) {
                Thread.sleep(2500)
            };
            System.destroyLoadingDialog();
        }

    }
}