package devs.erasmus.epills.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import devs.erasmus.epills.broadcast_receiver.AlarmBroadcastReceiver;

/**
 * Created by Remo Andreoli on 15/11/2017.
 */

public class AlarmUtil {
    private Context context;
    private int alarmId; //requestcode
    private String medicineName; //name of the pill
    private int quantity; //how many pills to take
    Date startDate;
    Date endDate;

    private boolean isOnce; //true if the alarms is needed only once
    private boolean enabled;

    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;

    //alarm created without occurences
    public AlarmUtil(Context context, String medicineName, int quantity, Date startDate, Date endDate, int alarmId){
        this.context = context;
        this.medicineName = medicineName;
        this.quantity = quantity;
        this.startDate = startDate;
        this.endDate = endDate;
        this.alarmId = alarmId;

        if(startDate == endDate) {
            this.isOnce = true;
        }
        else{
            this.isOnce = false;
        }
        this.enabled = true;

        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        updateCalendar();
    }

    public void updateCalendar(){
        Intent intent = new Intent(context, AlarmBroadcastReceiver.class);
        intent.putExtra("medicineName", medicineName);
        intent.putExtra("alarmId", alarmId);
        intent.putExtra("quantity", quantity);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        pendingIntent = PendingIntent.getBroadcast(context, alarmId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        calendar.setTime(startDate);

        if(getIsOnce()){
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    pendingIntent);

            Log.e("set alarm:",String.valueOf(calendar.getTime()) +" ("+ String.valueOf(alarmId) + ")");
        } else {
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(),
                        AlarmManager.INTERVAL_DAY * 7, //once a week
                        pendingIntent);

            Log.e("set occurent alarm:",String.valueOf(calendar.getTime()) +" ("+ String.valueOf(alarmId) + ")");

                //set the end date alarm
                calendar.setTime(endDate);
                intent.putExtra("end",1);
                int id = (int) System.currentTimeMillis();
                pendingIntent = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT); //intent stores the alarmId of the alarm to cancel

                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(),
                        pendingIntent);

            Log.e("set endalarm:", String.valueOf(calendar.getTime()));
        }

    }

    public void cancelAlarm(){
        if(pendingIntent!=null) {
            alarmManager.cancel(pendingIntent);
            Log.e("cancel alarm:",String.valueOf(medicineName) +" - "+ String.valueOf(alarmId));
        }
    }

    // GETTERS / SETTERS
    public Context getContext(){
        return context;
    }

    public void setContext(Context context){
        this.context = context;
        updateCalendar();
    }

    public String getmedicineName(){
        return medicineName;
    }

    public void setmedicineName(String medicineName){
        medicineName = medicineName;
        updateCalendar();
    }

    public int getAlarmId() {
        return alarmId;
    }

    public void setAlarmId(int id) {
        alarmId = id;
        updateCalendar();
    }

    public boolean getIsOnce() {
        return isOnce;
    }

    public void setIsOnce(boolean isOnce) {
        this.isOnce = isOnce;
        updateCalendar();
    }

    public boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        updateCalendar();
    }
}
