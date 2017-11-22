package devs.erasmus.epills.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;

import devs.erasmus.epills.broadcast_receiver.AlarmBroadcastReceiver;

/**
 * Created by Lenovo-PC on 22/11/2017.
 */

public class SetAlarmUtil {

    public SetAlarmUtil() {
        //
    }

    static public void setAlarm(Context context, String medicineName, int quantity, Date startDate, Date endDate, int alarmId) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent;
        Intent intent = new Intent(context, AlarmBroadcastReceiver.class);

        intent.putExtra("medicineName", medicineName);
        intent.putExtra("alarmId", alarmId);
        intent.putExtra("quantity", quantity);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        pendingIntent = PendingIntent.getBroadcast(context, alarmId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        calendar.setTime(startDate);

        //create an alarm without occurencies
        if (startDate.equals(endDate)) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    pendingIntent);

            Log.e("set alarm:", String.valueOf(calendar.getTime()) + " (" + String.valueOf(alarmId) + ")");
        }
        //create an alarm with occurencies
        else {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY * 7, //once a week
                    pendingIntent);

            Log.e("set occurent alarm:", String.valueOf(calendar.getTime()) + " (" + String.valueOf(alarmId) + ")");

            //set the end date alarm
            calendar.setTime(endDate);
            intent.putExtra("end", 1);
            int id = (int) System.currentTimeMillis();
            pendingIntent = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT); //intent stores the alarmId of the alarm to cancel

            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    pendingIntent);

            Log.e("set endalarm:", String.valueOf(calendar.getTime()));
        }
    }

    static public void cancelAlarm(Context context, int alarmId) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, alarmId, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent);
            Log.e("cancel alarm:", String.valueOf(alarmId));
        }
    }
}
