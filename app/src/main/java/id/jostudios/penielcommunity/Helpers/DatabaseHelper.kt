package id.jostudios.penielcommunity.Helpers

import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
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
    private val credentialListModelType = object : TypeToken<Map<Long, CredentialModel>>(){}.type;

    private val userModelType = object : TypeToken<UserModel>(){}.type;
    private val userListModelType = object : TypeToken<Map<Long, UserModel>>(){}.type;

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

//    public suspend fun getCredentials(): MutableList<CredentialModel> {
//        val rawData = FirebaseHelper.readNode("credentials");
//        System.debug("Credential Raw : ${rawData}");
//
//        val data = gson.fromJson<Map<Long, CredentialModel>>(rawData, credentialListModelType);
//        val dataList = data.values.toMutableList();
//
//        val tempRaw = gson.toJson(dataList);
//        System.debug("Credential List Raw : ${tempRaw}");
//
//        return dataList;
//    }
//
//    public suspend fun getCredentialByID(id: String): CredentialModel? {
//        val rawData = FirebaseHelper.readNode("credentials/${id}");
//        val data = gson.fromJson<CredentialModel>(rawData, credentialModelType);
//
//        return data;
//    }
//
//    public suspend fun getCredentialByName(name: String): CredentialModel? {
//        val datas = getCredentials();
//
//        for (data in datas) {
//            if (data.id == 100.toString()) { continue; }
//
//            if (data.name == name) {
//                return data;
//            }
//        }
//
//        return null;
//    }
//
//    public suspend fun getUsers(): MutableList<UserModel> {
//        val rawData = FirebaseHelper.readNode("users");
//        System.debug("User Raw : ${rawData}");
//
//        val data = gson.fromJson<Map<Long, UserModel>>(rawData, userListModelType);
//        val dataList = data.values.toMutableList();
//
//        val tempRaw = gson.toJson(dataList);
//        System.debug("User List Raw : ${tempRaw}");
//
//        return dataList;
//    }
//
//    public suspend fun getUserByID(id: String): UserModel? {
//        val rawData = FirebaseHelper.readNode("users/${id}");
//        val data = gson.fromJson<UserModel>(rawData, userModelType);
//
//        return data;
//    }
//
//    public suspend fun getUserByName(name: String): UserModel? {
//        val datas = getUsers();
//
//        for (data in datas) {
//            if (data.id == 100.toString()) { continue; }
//
//            if (data.name == name) {
//                return data;
//            }
//        }
//
//        return null;
//    }
//
//    public suspend fun postUser(userModel: UserModel) {
//        var checkID = getUserByID(userModel.id);
//        var checkName = getUserByName(userModel.name);
//
//        if (checkID != null) {
//            throw Exception("Akun dengan ID yang sama sudah di buat!");
//        }
//
//        if (checkName != null) {
//            throw Exception("Akun dengan nama yang sama sudah di buat!");
//        }
//
//        FirebaseHelper.writeNode("users/${userModel.id}", userModel);
//    }
//
//    public suspend fun postCredential(credentialModel: CredentialModel) {
//        var checkID = getCredentialByID(credentialModel.id);
//        var checkName = getCredentialByName(credentialModel.name);
//
//        if (checkID != null) {
//            throw Exception("Kredensial dengan ID yang sama sudah di buat!");
//        }
//
//        if (checkName != null) {
//            throw Exception("Kredensial dengan nama yang sama sudah di buat!");
//        }
//
//        FirebaseHelper.writeNode("credentials/${credentialModel.id}", credentialModel);
//    }
//
//    public suspend fun getFeeds(): MutableList<FeedPostModel> {
//        val rawData = FirebaseHelper.readNode("feed_container");
//
//        val data = gson.fromJson<Map<Long, FeedPostModel>>(rawData, feedListModelType);
//        val dataList = data.values.toMutableList();
//
//        return dataList
//    }
//
//    public suspend fun getFeedByID(id: Long): FeedPostModel? {
//        val rawData = FirebaseHelper.readNode("feed_container/${id}");
//        val data = gson.fromJson<FeedPostModel>(rawData, feedModelType);
//
//        return data;
//    }
//
//    public  suspend fun postFeedContainer(feedPostModel: FeedPostModel) {
//        val checkID = getFeedByID(feedPostModel.feedID);
//
//        if (checkID != null) {
//            throw Exception("Feed dengan ID yang sama sudah di buat!");
//        }
//
//        FirebaseHelper.writeNode("feed_container/${feedPostModel.feedID}", feedPostModel);
//    }
//
//    public suspend fun getFeedLikes(): MutableList<FeedLikesModel> {
//        val rawData = FirebaseHelper.readNode("feed_likes");
//        val data = gson.fromJson<Map<Long, FeedLikesModel>>(rawData, feedLikeListType);
//        val dataList = data.values.toMutableList();
//
//        return dataList;
//    }
//
//    public suspend fun getFeedLikeByID(feedID: Long): FeedLikesModel? {
//        val rawData = FirebaseHelper.readNode("feed_likes/${feedID}");
//        val data = gson.fromJson<FeedLikesModel>(rawData, feedLikeType);
//
//        return data;
//    }
//
//    public suspend fun getFeedCommentByID(feedID: Long): FeedCommentModel? {
//        val rawData = FirebaseHelper.readNode("feed_comments/${feedID}");
//        val data = gson.fromJson<FeedCommentModel>(rawData, feedCommentType);
//
//        return data;
//    }
//
//    public suspend fun postFeedLike(feedID: Long, model: FeedLikesModel) {
//        FirebaseHelper.writeNode("feed_likes/${feedID}", model);
//    }
//
//    public suspend fun addFeedLike(feedID: Long) {
//        val checkID = getFeedLikeByID(feedID);
//
//        if (checkID != null) {
//            throw Exception("Feed like dengan ID yang sama telah di temukan!");
//        }
//
//        var feedLikeModel = FeedLikesModel();
//        feedLikeModel.id = feedID;
//        feedLikeModel.userLikes.add("100");
//
//        FirebaseHelper.writeNode("feed_likes/${feedID}", feedLikeModel);
//    }
}