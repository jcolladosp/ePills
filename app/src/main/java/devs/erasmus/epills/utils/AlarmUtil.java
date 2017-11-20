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
    private int hourOfDay;
    private int minute;

    private int day;
    private int month;
    private int year;

    private boolean isOnce; //true if the alarms is needed only once
    private int weekDay; //index 1: Sunday, index 2: Monday ... because Calendar counts from 1
    private boolean enabled;

    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;

    public AlarmUtil(Context context){
       this.context = context;
    }

    //alarm created without occurences
    public AlarmUtil(Context context, String medicineName, int quantity, Date startDate, int alarmId){
        this.context = context;
        this.medicineName = medicineName;
        this.quantity = quantity;
        this.startDate = startDate;
        this.alarmId = alarmId;
        /*
        this.hourOfDay = hourOfDay;
        this.minute = minute;

        this.day = day;
        this.month = month;
        this.year = year;
        */
        this.isOnce = true;
        this.enabled = true;

        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        updateCalendar();
    }

    //alarm created with occurences
    public AlarmUtil(Context context, String medicineName, int quantity, Date startDate, Date endDate, int alarmId, int weekDay){
        this.context = context;
        this.medicineName = medicineName;
        this.quantity = quantity;
        this.startDate = startDate;
        this.endDate = endDate;
        this.alarmId = alarmId;

        /*
        this.hourOfDay = hourOfDay;
        this.minute = minute;

        this.day = day;
        this.month = month;
        this.year = year;
        */
        this.isOnce = false;
        this.weekDay = weekDay;
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
                Calendar occurenceCalendar = Calendar.getInstance();
                occurenceCalendar.setTime(startDate);
                //set the weekDay(NOTE: redundant passage if weekday is the same as start day
                occurenceCalendar.set(Calendar.DAY_OF_WEEK, getWeekDay());
                //if we are starting an alarm for the next week(e.g today is friday and i want an alarm for monday)
                 if(occurenceCalendar.get(Calendar.DAY_OF_MONTH) < calendar.get(Calendar.DAY_OF_MONTH)) {
                     //NOTE: trying to update the alarms variable here cause overflow
                     //this means that the variable arent reliable anymore after this set
                    occurenceCalendar.set(Calendar.DAY_OF_MONTH, occurenceCalendar.get(Calendar.DAY_OF_MONTH) + 7);
                }

                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                        occurenceCalendar.getTimeInMillis(),
                        AlarmManager.INTERVAL_DAY * 7, //once a week
                        pendingIntent);

            Log.e("set occurent alarm:",String.valueOf(occurenceCalendar.getTime()) +" ("+ String.valueOf(alarmId) + ")");
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

    public int getWeekDay(){
        return weekDay;
    }

    public void setWeekDay(int weekDay){
        this.weekDay = weekDay;
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
