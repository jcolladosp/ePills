package devs.erasmus.epills.controller;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

import devs.erasmus.epills.broadcast_receiver.AlarmBroadcastReceiver;

/**
 * Created by Remo Andreoli on 15/11/2017.
 */

public class Alarm {
   /* private AlarmManager alarmManager;
    private PendingIntent alarmIntent;
    private Calendar calendar;
    public Alarm(Context context){
        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.clear();
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    public setAlarm(int hourOfDay, int minute, int year, int month, int day){
        calendar.set(year,month,day,hourOfDay,minute);

        Intent intent = new Intent(this, AlarmBroadcastReceiver.class);
        int id = (int)System.currentTimeMillis(); //it creates an unique id
    }
    */
}
