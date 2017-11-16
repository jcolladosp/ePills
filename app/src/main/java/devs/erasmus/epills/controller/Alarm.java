package devs.erasmus.epills.controller;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import java.util.Arrays;
import java.util.Calendar;

import devs.erasmus.epills.broadcast_receiver.AlarmBroadcastReceiver;

/**
 * Created by Remo Andreoli on 15/11/2017.
 */

public class Alarm {
    private Context context;
    private int alarmId; //requestcode
    private String alarmTitle; //name of the pill

    private int hourOfDay;
    private int minute;

    private int day;
    private int month;
    private int year;

    private boolean isOnce; //true if the alarms is needed only once
    private boolean[] weekdaysSelection; //index 0: Sunday, index 1: Monday ...
    private boolean enabled;


    //default values, probably you shouldn't be here
    public Alarm(Context context){
        Calendar defaultCal = Calendar.getInstance();
        defaultCal.setTimeInMillis(System.currentTimeMillis());

        this.context = context;
        alarmId = 0; //it creates an unique id
        alarmTitle = "defaultTitle";

        hourOfDay = defaultCal.get(Calendar.HOUR_OF_DAY);
        minute = defaultCal.get(Calendar.MINUTE);

        day = defaultCal.get(Calendar.DAY_OF_MONTH);
        month = defaultCal.get(Calendar.MONTH);
        year = defaultCal.get(Calendar.YEAR);

        isOnce = true;
        enabled = true;

        updateCalendar();
    }

    //alarm created without occurences
    public Alarm(Context context, String medicineName, int alarmId, int hourOfDay, int minute, int year, int month, int day){
        this.context = context;
        this.alarmId = alarmId;
        alarmTitle = medicineName;

        this.hourOfDay = hourOfDay;
        this.minute = minute;

        this.day = day;
        this.month = month;
        this.year = year;

        this.isOnce = true;
        this.enabled = true;

        updateCalendar();
    }

    //alarm created with occurences
    public Alarm(Context context, String medicineName, int alarmId, int hourOfDay, int minute, int year, int month, int day, boolean[] weekdaysSelection){
        this.context = context;
        this.alarmId = alarmId;
        alarmTitle = medicineName;

        this.hourOfDay = hourOfDay;
        this.minute = minute;

        this.day = day;
        this.month = month;
        this.year = year;

        this.isOnce = false;
        this.weekdaysSelection=weekdaysSelection;
        this.enabled = true;

        updateCalendar();
    }

    //TODO: implement
    public void updateCalendar(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(year,month,day,hourOfDay,minute);

        Intent intent = new Intent(context, AlarmBroadcastReceiver.class);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        //Intent intent = new Intent(this, AlarmBroadcastReceiver.class);

        if(isOnce){
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, alarmId, intent, 0);
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    pendingIntent);
        } else {
            for(int i=0;i<weekdaysSelection.length;i++) {
                if(weekdaysSelection[i]) {
                    calendar.set(Calendar.DAY_OF_WEEK, Arrays.asList(weekdaysSelection).indexOf(i));
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, alarmId + i, intent, 0);
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                            calendar.getTimeInMillis(),
                            AlarmManager.INTERVAL_DAY * 7, //once a week
                            pendingIntent);
                }
            }
        }
    }

    // GETTERS / SETTERS
    public int getId() {
        return alarmId;
    }

    public void setId(int id) {
        alarmId = id;
    }

    public String getTitle() {
        return alarmTitle;
    }

    public void setTitle(String title) {
        alarmTitle = title;
    }

    public int getHourOfDay() {
        return hourOfDay;
    }

    public void setHourOfDay(int hour) {
        this.hourOfDay = hour;
        updateCalendar();
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
        updateCalendar();
    }

    public void setHourMinute(int hour, int minute) {
        this.hourOfDay = hour;
        this.minute = minute;
        updateCalendar();
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
        updateCalendar();
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
        updateCalendar();
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
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
