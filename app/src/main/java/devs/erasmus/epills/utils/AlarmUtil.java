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

public class AlarmUtil {

    public AlarmUtil() {
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
        calendar.setTime(startDate);

        //create an alarm without occurencies
        if (startDate.equals(endDate)) {
            intent.putExtra("isOnce", true);
            pendingIntent = PendingIntent.getBroadcast(context, alarmId, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    pendingIntent);

            Log.e("set alarm:", String.valueOf(calendar.getTime()) + " (" + String.valueOf(alarmId) + ")");
        }
        //create an alarm with occurencies
        else {
            intent.putExtra("isOnce", false);
            pendingIntent = PendingIntent.getBroadcast(context, alarmId, intent, PendingIntent.FLAG_UPDATE_CURRENT);

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

            LitePalManageUtil.deleteIntakesByAlarmId(alarmId);
        }

    }

    //Purpose: fix the calendar to set the proper alarm, it's used when adding a multi-time alarm
    //to fix the problematic cases of setting an alarm at the end of the month or for another week
    // and when retrieving the multi-time alarms on boot
    static public Calendar fixCalendar(Calendar startDate, int weekday){
        Calendar occurenceCalendar = Calendar.getInstance();
        occurenceCalendar.setTime(startDate.getTime());
        occurenceCalendar.set(Calendar.DAY_OF_WEEK, weekday);

        //check if the set changed the month: if today is Sunday 26 Nov and you want an alarm on Friday,
        //the set above will change the date to Friday 2 Dic, without this first if  the next if would change
        //the date to Friday 9 Dic
        if(occurenceCalendar.get(Calendar.MONTH) == startDate.get(Calendar.MONTH)) {
            //check if you are trying to set an alarm for the next week, if True change week
            if(occurenceCalendar.get(Calendar.DAY_OF_MONTH) < startDate.get(Calendar.DAY_OF_MONTH)) {
                occurenceCalendar.set(Calendar.DAY_OF_MONTH, occurenceCalendar.get(Calendar.DAY_OF_MONTH) + 7);
            }
        }
        return occurenceCalendar;
    }

    static public boolean isAlarmSet(Context context, int alarmId){
        Intent intent = new Intent(context, AlarmBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, alarmId, intent, PendingIntent.FLAG_NO_CREATE);

        return (pendingIntent != null); //True: alarm is set
    }

}
