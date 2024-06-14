package id.jostudios.penielcommunity.Helpers

import android.provider.ContactsContract.Data
import com.google.firebase.ktx.Firebase
import id.jostudios.penielcommunity.Models.FirebaseModels.CredentialModel
import id.jostudios.penielcommunity.Models.FirebaseModels.TokenModel
import id.jostudios.penielcommunity.Models.FirebaseModels.UserModel
import id.jostudios.penielcommunity.Objects.GlobalState
import id.jostudios.penielcommunity.Objects.System
import java.lang.Exception
import java.util.Date

object AuthHelper {
    public fun parseToken(rawData: String): TokenModel? {
        val dataList = rawData.split("\n");

        var model: TokenModel = TokenModel();

        for (data in dataList) {
            val dataSplit = data.split("=");
            val key = dataSplit[0];
            val value = dataSplit[1];

            if (key == "appid") { model.appID = value; continue; }
            if (key == "dburl") { model.dbUrl = value; continue; }
            if (key == "apikey") { model.apiKey = value; continue; }
            if (key == "storage") { model.storage = value; continue; }

            return null;
        }

        return model;
    }

    public suspend fun login(userName: String, password: String) {
        val auth = FirebaseHelper.getAuth();

        val email = "${userName}@gmail.com";

        if (auth.currentUser == null) {
            auth.signInWithEmailAndPassword(email, password);
            return;
        }

        val credential = DatabaseHelper.getCredentialByName(userName);

        System.debug("Credentials : ${credential}");

        if (credential == null) {
            throw Exception("This credential is not found!");
        }

        if (password != credential.password) {
            throw Exception("Password doesn't match!");
        }

        val user = DatabaseHelper.getUserByID(credential.id);

        System.debug("User : ${user}");

        GlobalState.currentUser = user;
        GlobalState.currentCredential = credential;
        GlobalState.isLogin = true;
    }

    public suspend fun createAccount(userName: String, password: String) {
        val auth = FirebaseHelper.getAuth();

        val email = "${userName}@gmail.com";

        auth.createUserWithEmailAndPassword(email, password);
        auth.signInWithEmailAndPassword(email, password);

        val credential = DatabaseHelper.getCredentialByName(userName);

        if (credential != null) {
            throw Exception("Account is already exists!");
        }

        if (DatabaseHelper.getCredentialByID(auth.uid!!) != null) {
            throw Exception("Invalid! Please try again later!");
        }

        var newCreds = CredentialModel(
            id = auth.uid!!,
            name = userName,
            password = password
        );

        var newUser = UserModel(
            id = auth.uid!!,
            name = userName,
            displayName = userName
        );

        DatabaseHelper.postUser(newUser);
        DatabaseHelper.postCredential(newCreds);
    }
}