package devs.erasmus.epills.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
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

    static public void setAlarm(Context context, String medicineName, int quantity, Date startDate, Date endDate, int alarmId, boolean isEnable) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent;
        Intent intent = new Intent(context, AlarmBroadcastReceiver.class);

        intent.putExtra("startDate", startDate.getTime());
        intent.putExtra("endDate", endDate.getTime());
        intent.putExtra("medicineName", medicineName);
        intent.putExtra("alarmId", alarmId);
        intent.putExtra("quantity", quantity);

        if(startDate.compareTo(endDate) == 0){ //same date
            intent.putExtra("isOnce", 1);
        }
        else{
            intent.putExtra("isOnce", 0);
        }

        if(isEnable){
            intent.putExtra("isEnable", true);
        }
        else{
            intent.putExtra("isEnable", false);
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.setTime(startDate);

        pendingIntent = PendingIntent.getBroadcast(context, alarmId, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        //Compatibility check
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    pendingIntent);
        }
        else{
            alarmManager.setExact(alarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    pendingIntent);
        }
        Log.e("set alarm:", String.valueOf(calendar.getTime()) + " (" + String.valueOf(alarmId) + ")");

    }

    static public void cancelAlarm(Context context, int alarmId) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, alarmId, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent);
            Log.e("cancel alarm:", String.valueOf(alarmId));

            LitePalManageUtil.deleteIntakeByAlarmId(context, alarmId);
        }

    }


    static public boolean isAlarmSet(Context context, int alarmId){
        Intent intent = new Intent(context, AlarmBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, alarmId, intent, PendingIntent.FLAG_NO_CREATE);

        return (pendingIntent != null); //True: alarm is set
    }

    //Purpose: fix the calendar to set the proper alarm, it's used when adding a multi-time alarm
    //to fix the problematic case of setting an alarm at the end of the month or for another week
    static public Calendar fixCalendar(Calendar startDate, int weekday){
        Calendar occurenceCalendar = Calendar.getInstance();
        occurenceCalendar.setTime(startDate.getTime());
        occurenceCalendar.set(Calendar.DAY_OF_WEEK, weekday);

        //check if the previous instruction changed the month: if today is Sunday 26 Nov and you want an alarm on Friday,
        //the set above will change the date to Friday 2 Dic

        if (occurenceCalendar.get(Calendar.MONTH) == startDate.get(Calendar.MONTH) && occurenceCalendar.get(Calendar.DAY_OF_MONTH) < startDate.get(Calendar.DAY_OF_MONTH)) {
            occurenceCalendar.set(Calendar.DAY_OF_MONTH, occurenceCalendar.get(Calendar.DAY_OF_MONTH) + 7);
        }
        return occurenceCalendar;
    }
}
