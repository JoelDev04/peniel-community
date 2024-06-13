package id.jostudios.penielcommunity.Helpers

import android.content.Context
import android.net.Uri
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.gson.Gson
import id.jostudios.penielcommunity.Models.FirebaseModels.TokenModel
import id.jostudios.penielcommunity.Objects.System
import kotlinx.coroutines.tasks.await

object FirebaseHelper {
    private const val dbName: String = "mainDB";

    private lateinit var db: FirebaseDatabase;
    private lateinit var auth: FirebaseAuth;
    private lateinit var storage: FirebaseStorage;

    private val gson: Gson = Gson();

    public fun initializeDB(context: Context, tokenModel: TokenModel) {
        System.debug("Initializing database!");

        var firebaseOpts = FirebaseOptions.Builder().apply {
            setApiKey(tokenModel.apiKey!!);
            setApplicationId(tokenModel.appID!!);
            setDatabaseUrl(tokenModel.dbUrl!!);

            if (tokenModel.storage != null) {
                setStorageBucket(tokenModel.storage!!);
            }
        }.build();

        System.debug("Firebase options created!");

        try {
            FirebaseApp.initializeApp(context, firebaseOpts, dbName);
            db = FirebaseDatabase.getInstance(FirebaseApp.getInstance(dbName));
            auth = FirebaseAuth.getInstance(FirebaseApp.getInstance(dbName));

            if (tokenModel.storage != null) {
                storage = FirebaseStorage.getInstance(FirebaseApp.getInstance(dbName));
            }

            auth.signInAnonymously();

            System.debug("Database initialized!");
        } catch (e: Exception) {
            System.debug("Catching error : ${e.message}");
        }
    }

    public fun destroyDB() {
        FirebaseApp.getInstance(dbName).delete();
    }

    public fun getDBReference(): DatabaseReference {
        return db.reference;
    }

    public fun getStorageReference(): StorageReference {
        return storage.reference;
    }

    public fun getAuth(): FirebaseAuth {
        return auth;
    }

    public suspend fun readNode(child: String): String {
        val node = getDBReference().child(child).get();
        val value = node.await().value;

        val raw = gson.toJson(value);

        return raw;
    }

    public suspend fun writeNode(child: String, data: Any) {
        val node = getDBReference().child(child);
        node.setValue(data).await();
    }
}