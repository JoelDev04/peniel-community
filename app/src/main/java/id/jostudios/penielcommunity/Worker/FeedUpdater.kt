package id.jostudios.penielcommunity.Worker

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.gson.Gson
import id.jostudios.penielcommunity.Helpers.AuthHelper
import id.jostudios.penielcommunity.Helpers.DataHelper
import id.jostudios.penielcommunity.Helpers.DatabaseHelper
import id.jostudios.penielcommunity.Helpers.FirebaseHelper
import id.jostudios.penielcommunity.Helpers.NotificationHelper
import id.jostudios.penielcommunity.Models.SaveModel
import id.jostudios.penielcommunity.Objects.GlobalState
import id.jostudios.penielcommunity.Objects.System
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FeedUpdater(private val context: Context, params: WorkerParameters): Worker(context, params) {
    private var lastFeed: Long = 0;
    private var gson: Gson = Gson();

    override fun doWork(): Result {
        System.debug("Feed updater is working!");

        GlobalScope.launch {
            try {
                val dbRef = FirebaseHelper.getDBReference();

                if (dbRef == null) {
                    initDB();
                    return@launch;
                }

                val updatedFeed = DatabaseHelper.getUpdatedFeed();

                System.debug("Updated feed : ${updatedFeed}");
                System.debug("Last feed : ${lastFeed}");

                if (updatedFeed == null) {
                    return@launch;
                }

                if (lastFeed == updatedFeed) {
                    System.debug("Same feed detected! Skipping...");
                    return@launch;
                }

                lastFeed = updatedFeed;
                System.debug("Updating the feed!");

                withContext(Dispatchers.Main) {
                    NotificationHelper.createNotificationChannel(context);

                    val notification = NotificationHelper.buildNotification(
                        context,
                        "Peniel Community Feed",
                        "Ada feed baru yang sudah di post. Ayo check sekarang!"
                    );

                    if (ContextCompat.checkSelfPermission(
                            context,
                            android.Manifest.permission.POST_NOTIFICATIONS
                        ) == PackageManager.PERMISSION_DENIED ||
                        ContextCompat.checkSelfPermission(
                            context,
                            android.Manifest.permission.POST_NOTIFICATIONS
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        System.debug("There's no notification permission!");
                        return@withContext;
                    }

                    NotificationManagerCompat.from(context).notify(100, notification);
                    System.setToast(context, "New feed has been updated!");
                }
            } catch (e: Exception) {
                System.debug("Error at feed updater : ${e.message}");
            }

        }

        return Result.success();
    }

    private suspend fun initDB() {
        val dataHelper = DataHelper(context);
        val loadRaw = dataHelper.loadData()!!;

        val parsedToken = AuthHelper.parseToken(loadRaw.token);

        FirebaseHelper.initializeDB(context, parsedToken!!);

        AuthHelper.login(GlobalState.currentUser?.name!!, GlobalState.currentCredential?.password!!);
    }
}