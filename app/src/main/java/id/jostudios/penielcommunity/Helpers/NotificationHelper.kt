package id.jostudios.penielcommunity.Helpers

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import id.jostudios.penielcommunity.R

object NotificationHelper {
    public fun createNotificationChannel(context: Context) {
        val id = "PenielNotify";
        val name = "PenielCommunityNotify";
        val desc = "A notification system for Peniel Community App!";
        val importance = NotificationManager.IMPORTANCE_HIGH;

        val channel = NotificationChannel(
            id, name, importance
        );
        channel.description = desc;

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager;
        notificationManager.createNotificationChannel(channel);
    }

    public fun buildNotification(context: Context, title: String, content: String): Notification {
        val builder = NotificationCompat.Builder(context, "PenielNotify");
        builder.setContentTitle(title);
        builder.setContentText(content);
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        builder.setSmallIcon(R.drawable.notification);

        return builder.build();
    }
}