package id.jostudios.penielcommunity.Helpers

import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import id.jostudios.penielcommunity.Models.FirebaseModels.CommentModel
import id.jostudios.penielcommunity.Models.FirebaseModels.CredentialModel
import id.jostudios.penielcommunity.Models.FirebaseModels.FeedCommentModel
import id.jostudios.penielcommunity.Models.FirebaseModels.FeedLikesModel
import id.jostudios.penielcommunity.Models.FirebaseModels.FeedPostModel
import id.jostudios.penielcommunity.Models.FirebaseModels.UserModel
import id.jostudios.penielcommunity.Objects.System
import org.json.JSONObject
import java.lang.Exception

object DatabaseHelper {
    private val gson: Gson = Gson();

    private val credentialModelType = object : TypeToken<CredentialModel>(){}.type;
    private val credentialListModelType = object : TypeToken<Map<String, CredentialModel>>(){}.type;

    private val userModelType = object : TypeToken<UserModel>(){}.type;
    private val userListModelType = object : TypeToken<Map<String, UserModel>>(){}.type;

    private val feedModelType = object : TypeToken<FeedPostModel>(){}.type;
    private val feedListModelType = object : TypeToken<Map<Long, FeedPostModel>>(){}.type;

    private val feedLikeType = object : TypeToken<FeedLikesModel>(){}.type;
    private val feedLikeListType = object : TypeToken<Map<Long, FeedLikesModel>>(){}.type;

    private val feedCommentType = object : TypeToken<FeedCommentModel>(){}.type;
    private val feedCommentListType = object : TypeToken<Map<Long, FeedCommentModel>>(){}.type;

    public suspend fun getCredentials(): MutableList<CredentialModel> {
        val rawData = FirebaseHelper.readNode("credentials");
        System.debug("Credential Raw : ${rawData}");

        val data = gson.fromJson<Map<Long, CredentialModel>>(rawData, credentialListModelType);
        val dataList = data.values.toMutableList();

        return dataList;
    }

    public suspend fun getUsers(): MutableList<UserModel> {
        val rawData = FirebaseHelper.readNode("users");
        System.debug("User Raw : ${rawData}");

        val data = gson.fromJson<Map<Long, UserModel>>(rawData, userListModelType);
        val dataList = data.values.toMutableList();

        return dataList;
    }

    public suspend fun getCredentialById(id: String): CredentialModel? {
        val rawData = FirebaseHelper.readNode("credentials/${id}");
        val data = gson.fromJson<CredentialModel>(rawData, credentialModelType);

        return data;
    }

    public suspend fun getUserById(id: String): UserModel? {
        val rawData = FirebaseHelper.readNode("users/${id}");
        val data = gson.fromJson<UserModel>(rawData, userModelType);

        return data;
    }

    public suspend fun getCredentialByName(name: String): CredentialModel? {
        val datas = getCredentials();

        for (data in datas) {
            if (data.id == 100.toString()) {
                continue;
            }

            if (data.name == name) {
                return data;
            }
        }

        return null;
    }

    public suspend fun getUserByName(name: String): UserModel? {
        val datas = getUsers();

        for (data in datas) {
            if (data.id == 100.toString()) {
                continue;
            }

            if (data.name == name) {
                return data;
            }
        }

        return null;
    }

    public suspend fun postCredential(credentialModel: CredentialModel) {
        val checkID = getCredentialById(credentialModel.id);
        val checkName = getCredentialByName(credentialModel.name);

        if (checkID != null) {
            throw Exception("Kredensial dengan ID yang sama sudah di buat!");
        }

        if (checkName != null) {
            throw Exception("Kredensial dengan nama yang sama sudah di buat!");
        }

        FirebaseHelper.writeNode("credentials/${credentialModel.id}", credentialModel);
    }

    public suspend fun postUser(userModel: UserModel) {
        val checkID = getUserById(userModel.id);
        val checkName = getUserByName(userModel.name);

        if (checkID != null) {
            throw Exception("Akun dengan ID yang sama sudah di buat!");
        }

        if (checkName != null) {
            throw Exception("Akun dengan nama yang sama sudah di buat!");
        }

        FirebaseHelper.writeNode("users/${userModel.id}", userModel);
    }

    public suspend fun getFeeds(): MutableList<FeedPostModel> {
        val rawData = FirebaseHelper.readNode("feed_container");
        val data = gson.fromJson<Map<Long, FeedPostModel>>(rawData, feedListModelType);
        val dataList = data.values.toMutableList();

        return dataList;
    }

    public suspend fun getFeedById(id: Long): FeedPostModel? {
        val rawData = FirebaseHelper.readNode("feed_container/${id}");
        val data = gson.fromJson<FeedPostModel>(rawData, feedModelType);

        return data;
    }

    public suspend fun postFeed(feedModel: FeedPostModel) {
        val checkId = getFeedById(feedModel.feedID);

        if (checkId != null) {
            throw Exception("Feed dengan ID yang sama sudah di buat!");
        }

        FirebaseHelper.writeNode("feed_container/${feedModel.feedID}", feedModel);
    }

    public suspend fun getFeedLikes(): MutableList<FeedLikesModel> {
        val rawData = FirebaseHelper.readNode("feed_likes");
        val data = gson.fromJson<Map<Long, FeedLikesModel>>(rawData, feedLikeListType);
        val dataList = data.values.toMutableList();

        return dataList;
    }

    public suspend fun getFeedLikeById(feedID: Long): FeedLikesModel? {
        val rawData = FirebaseHelper.readNode("feed_likes/${feedID}");
        val data = gson.fromJson<FeedLikesModel>(rawData, feedLikeType);

        data.userLikes.remove("100");

        return data;
    }

    public suspend fun getUserFeedLike(feedID: Long, userID: String): String? {
        val rawData = FirebaseHelper.readNode("feed_likes/${feedID}/userLikes/${userID}");
        val data = gson.fromJson<String>(rawData, String::class.java);

        return data;
    }

    public suspend fun postFeedLike(feedID: Long, model: FeedLikesModel) {
        FirebaseHelper.writeNode("feed_likes/${feedID}", model);
    }

    public suspend fun getFeedComments(): MutableList<FeedCommentModel> {
        val rawData = FirebaseHelper.readNode("feed_comments");
        val data = gson.fromJson<Map<Long, FeedCommentModel>>(rawData, feedCommentListType);

        val dataList = data.values.toMutableList();

        return dataList;
    }

    public suspend fun getFeedCommentsById(feedID: Long): FeedCommentModel? {
        val rawData = FirebaseHelper.readNode("feed_comments/${feedID}");
        val data = gson.fromJson<FeedCommentModel>(rawData, feedCommentType);

        return data;
    }

    public suspend fun getFeedCommentById(feedID: Long, commentID: Long): CommentModel? {
        val rawData = FirebaseHelper.readNode("feed_comments/${feedID}/comments/${commentID}");
        val data = gson.fromJson<CommentModel>(rawData, CommentModel::class.java);

        return data;
    }

    public suspend fun postFeedComment(feedID: Long, model: FeedCommentModel) {
        FirebaseHelper.writeNode("feed_comments/${feedID}", model);
    }

    public suspend fun postUserFeedComment(feedID: Long, commentModel: CommentModel) {
        FirebaseHelper.writeNode("feed_comments/${feedID}/comments/${commentModel.commentID}", commentModel);
    }

    public suspend fun getCurrentBanner(): String? {
        val data = FirebaseHelper.readNode("_currentBanner")?.replace("\"", "");
        System.debug("Current banner : ${data}");
        return data;
    }

    public suspend fun postCurrentBanner(imgName: String) {
        FirebaseHelper.writeNode("_currentBanner", imgName);
    }

    public suspend fun getUpdatedFeed(): Long? {
        val data = FirebaseHelper.readNode("_public/_updatedFeed")?.toLong();
        System.debug("Updated feed : ${data}");
        return data;
    }

    public suspend fun postUpdatedFeed(feedID: Long) {
        FirebaseHelper.writeNode("_public/_updatedFeed", feedID);
    }

    public suspend fun getGlobalAppVersion(): String? {
        val data = FirebaseHelper.readNode("_appVersion")?.replace("\"", "");
        System.debug("App version : ${data}");
        return data;
    }
}