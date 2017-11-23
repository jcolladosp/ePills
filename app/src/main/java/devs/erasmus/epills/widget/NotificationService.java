package devs.erasmus.epills.widget;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import devs.erasmus.epills.R;
import devs.erasmus.epills.controller.ClockActivity;

public class NotificationService extends Service {
    @Nullable

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String medicineName = intent.getStringExtra("medicineName");
        int quantity = intent.getIntExtra("quantity", 69);
        int alarmId = intent.getIntExtra("alarmId", 69);

        //make a sound
        Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone ringtone = RingtoneManager.getRingtone(getApplicationContext(), notificationSound);
        ringtone.play();

        //make notification
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent openActivityIntent = new Intent(this.getApplicationContext(), ClockActivity.class);

        int id= (int) System.currentTimeMillis();
        PendingIntent pendingIntent = PendingIntent.getActivity(this, id, openActivityIntent, 0);

        /*
        Intent takeIntent = new Intent(this, );
        Intent dozeIntent = openActivityIntent;
        Intent skipIntent = ;

        Notification notify = new NotificationCompat.Builder(this, id)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_check_mark)
                .setContentTitle("Pill to take!")
                .setContentText("take the goddamn pill")
                */

        Notification notify = new Notification.Builder(this)
                .setContentTitle("You have to take ["+ medicineName + "], [" + quantity + "] time(s)")
                .setContentText("take the goddamn pill")
                .setSmallIcon(R.drawable.ic_check_mark)
                .setContentIntent(pendingIntent)
                .build();
        notificationManager.notify(id, notify);

        return START_NOT_STICKY;
    }
}
