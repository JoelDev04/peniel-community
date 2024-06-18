package id.jostudios.penielcommunity.Helpers

import android.provider.ContactsContract.Data
import com.google.firebase.ktx.Firebase
import id.jostudios.penielcommunity.Models.FirebaseModels.CredentialModel
import id.jostudios.penielcommunity.Models.FirebaseModels.TokenModel
import id.jostudios.penielcommunity.Models.FirebaseModels.UserModel
import id.jostudios.penielcommunity.Objects.GlobalState
import id.jostudios.penielcommunity.Objects.System
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import java.util.Date
import kotlin.math.log

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

        val login = auth.signInWithEmailAndPassword(email, password).await();

        if (login.user == null) {
            throw Exception("Login failed. User is null!");
        }

        auth.updateCurrentUser(login.user!!);

        System.debug("UID : ${login.user?.uid}");
        System.debug("Current user : ${auth.currentUser.toString()}");
        System.debug("Login user : ${login.user.toString()}");

        val credential = DatabaseHelper.getCredentialById(login.user?.uid!!);

        System.debug("Credentials : ${credential}");

        if (credential == null) {
            throw Exception("This credential is not found!");
        }

        if (password != credential.password) {
            throw Exception("Password doesn't match!");
        }

        val user = DatabaseHelper.getUserById(credential.id);

        System.debug("User : ${user}");

        GlobalState.currentUser = user;
        GlobalState.currentCredential = credential;
        GlobalState.isLogin = true;
        GlobalState.firebaseUser = login.user!!;

        FirebaseHelper.setAuth(auth);
    }

    public suspend fun createAccount(userName: String, password: String) {
        val auth = FirebaseHelper.getAuth();

        val email = "${userName}@gmail.com";

        val result = auth.createUserWithEmailAndPassword(email, password).await();

        if (result.user == null) {
            throw Exception("User creation failed!");
        }

        val credential = DatabaseHelper.getCredentialById(result.user?.uid!!);

        if (credential != null) {
            throw Exception("Account is already exists!");
        }

        if (DatabaseHelper.getCredentialById(auth.uid!!) != null) {
            throw Exception("Invalid! Please try again later!");
        }

        var newCreds = CredentialModel(
            id = result.user?.uid!!,
            name = userName,
            password = password
        );

        var newUser = UserModel(
            id = result.user?.uid!!,
            name = userName,
            displayName = userName
        );

        DatabaseHelper.postUser(newUser);
        DatabaseHelper.postCredential(newCreds);
    }
}