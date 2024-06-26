package id.jostudios.penielcommunity.Helpers

import android.app.Activity
import android.content.Intent
import androidx.work.WorkManager
import id.jostudios.penielcommunity.Activities.AuthActivity
import id.jostudios.penielcommunity.Models.SaveModel
import id.jostudios.penielcommunity.Objects.GlobalState

class AccountHelper(private val activity: Activity) {

    private val localSaveHelper: DataHelper = DataHelper(activity.applicationContext);

    public fun logoutUser() {
        GlobalState.isAuth = false;
        GlobalState.isLogin = false;

        GlobalState.currentUser = null;
        GlobalState.currentCredential = null;
        GlobalState.token = "";

        var auth = FirebaseHelper.getAuth();

        val model = SaveModel();

        localSaveHelper.saveData(model);
        auth.signOut();
        FirebaseHelper.offlineDB();

        WorkManager.getInstance(activity.applicationContext).cancelAllWork();

        val authIntent = Intent(activity.applicationContext, AuthActivity::class.java);
        activity.startActivity(authIntent);
        activity.finish();

        FirebaseHelper.destroyDB();
    }
}