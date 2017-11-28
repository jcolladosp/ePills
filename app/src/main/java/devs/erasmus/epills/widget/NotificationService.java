package devs.erasmus.epills.widget;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import devs.erasmus.epills.R;
import devs.erasmus.epills.broadcast_receiver.ActionReceiver;
import devs.erasmus.epills.controller.ClockActivity;

public class NotificationService extends Service {
    @Nullable

    NotificationManager notificationManager;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        //alarm intent extras
        String medicineName = intent.getStringExtra("medicineName");
        int quantity = intent.getIntExtra("quantity", 69);
        int alarmId = intent.getIntExtra("alarmId", 69);

        //tap notification intent
        Intent openActivityIntent = new Intent(this.getApplicationContext(), ClockActivity.class);
        int id= (int) System.currentTimeMillis();  //using an unique id everytime alarm has it's own notification, it maybe overwhelming
        PendingIntent openActivityPendingIntent = PendingIntent.getActivity(this, id, openActivityIntent, 0);
        //action intents
        Intent skipIntent = new Intent(this.getApplicationContext(), ActionReceiver.class);
        skipIntent.putExtra("action","Skip");
        skipIntent.putExtra("id", id);

        Intent snoozeIntent = new Intent(this.getApplicationContext(), ActionReceiver.class);
        snoozeIntent.putExtra("action","Snooze");
        snoozeIntent.putExtra("id", id);

        Intent takeIntent = new Intent(this.getApplicationContext(), ActionReceiver.class);
        takeIntent.putExtra("action","Take");
        takeIntent.putExtra("id", id);
        //action pendings
        PendingIntent skipPending = PendingIntent.getBroadcast(this,1,skipIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent snoozePending = PendingIntent.getBroadcast(this,2,snoozeIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent takePending = PendingIntent.getBroadcast(this,3,takeIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        //New more flexible notification system ,Android Oreo only
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //channel id
            String channelId = "pill_reminder_channel";
            // The user-visible name of the channel.
            CharSequence channelName = getString(R.string.channel_name);
            // The user-visible description of the channel.
            String channelDescription = getString(R.string.channel_description);
            int channelImportance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(channelId, channelName, channelImportance);
            // Configure the notification channel.
            mChannel.setDescription(channelDescription);
            mChannel.enableLights(true);
            // Sets the notification light color for notifications posted to this
            // channel, if the device supports this feature.
            mChannel.setLightColor(Color.GREEN);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 500});
            notificationManager.createNotificationChannel(mChannel);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
                    .setContentTitle("You have to take "+ medicineName + ", " + quantity + " time(s)")
                    .setContentText("take the pill!")
                    .setSmallIcon(R.drawable.icon_alarm)
                    .setContentIntent(openActivityPendingIntent)
                    .addAction(R.drawable.ic_error, "Skip", skipPending)
                    .addAction(R.drawable.ic_add, "Snooze", snoozePending)
                    .addAction(R.drawable.ic_check_mark, "Take", takePending)
                    .setAutoCancel(true);

            notificationManager.notify(id, notificationBuilder.build());
        }
        else{
            makeSound();
            makeVibrate();

            Notification notify = new Notification.Builder(this)
                    .setContentTitle("You have to take "+ medicineName + ", " + quantity + " time(s)")
                    .setContentText("take the pill!")
                    .setColor(Color.GREEN)
                    .setSmallIcon(R.drawable.icon_alarm)
                    .setContentIntent(openActivityPendingIntent)
                    .addAction(R.drawable.ic_error, "Skip", skipPending)
                    .addAction(R.drawable.ic_add, "Snooze", snoozePending)
                    .addAction(R.drawable.ic_check_mark, "Take", takePending)
                    .setAutoCancel(true) // cancel when pressed
                    .build();
            notificationManager.notify(id, notify);
        }


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

        return START_NOT_STICKY;
    }

    public void makeSound(){
        Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone ringtone = RingtoneManager.getRingtone(getApplicationContext(), notificationSound);
        ringtone.play();
    }

    public void makeVibrate(){
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        v.vibrate(500);
    }
}
