package devs.erasmus.epills.broadcast_receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import org.litepal.crud.DataSupport;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import devs.erasmus.epills.model.IntakeMoment;
import devs.erasmus.epills.model.Medicine;
import devs.erasmus.epills.utils.AlarmUtil;

import static android.database.sqlite.SQLiteDatabase.OPEN_READWRITE;

public class BootReceiver extends BroadcastReceiver {
    String DATABASE_TABLE_NAME = "/data/data/devs.erasmus.epills/databases/ePills.db";

    //Unfortunately we can't inizialize LitePal without an application context, so we need to inizialize
    // the DB for retrieving the alarms using standard SQLite

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(DATABASE_TABLE_NAME, null, OPEN_READWRITE);
            //i need sdf because SQLite doesn't actually support Date as a type, so i have to rebuild it
            SimpleDateFormat dateFormat = new SimpleDateFormat();

           //String[] columns = new String[]{"startDate", "endDate", "medicine", "quantity", "alarmRequestCode", "isAlarmSet"};
           Cursor intakeCursor = db.query("intakemoment", null, null, null, null, null, null);

            int i=0;
            for (intakeCursor.moveToFirst(); !intakeCursor.isAfterLast(); intakeCursor.moveToNext()) {
                i++;
                long startDateMillis = intakeCursor.getLong(intakeCursor.getColumnIndex("startdate"));
                long endDateMillis = intakeCursor.getLong(intakeCursor.getColumnIndex("enddate"));
                int quantity = intakeCursor.getInt(intakeCursor.getColumnIndex("quantity"));
                int alarmRequestCode = intakeCursor.getInt(intakeCursor.getColumnIndex("alarmrequestcode"));
                long medicineId = intakeCursor.getLong(intakeCursor.getColumnIndex("medicineid"));
                String medicineName = "no name";
                Cursor medicineCursor = db.query("medicine", null, "id = "+String.valueOf(medicineId), null, null, null, null);


                if(medicineCursor.moveToFirst()){
                    medicineName = medicineCursor.getString(medicineCursor.getColumnIndex("name"));
                }

                Date startDate = long2Date(startDateMillis);
                //refresh startDate to current day/month/year
                //startDate = fixDate(startDate);

                Date endDate = long2Date(endDateMillis);

                startDate.setTime(startDateMillis);
                endDate.setTime(endDateMillis);

                AlarmUtil.setAlarm(context, medicineName, quantity, startDate, endDate, alarmRequestCode);
                Toast.makeText(context, "alarm set"+ String.valueOf(alarmRequestCode), Toast.LENGTH_SHORT).show();

                medicineCursor.close();
            }
            intakeCursor.close();
        }

    }

    /*
    private Date fixDate(Date date){
        Calendar dateCalendar = Calendar.getInstance();
        dateCalendar.setTime(date);

        while(dateCalendar.getTimeInMillis() < System.currentTimeMillis()){
            dateCalendar.set(Calendar.DAY_OF_MONTH, dateCalendar.get(Calendar.DAY_OF_MONTH) + 1);
        }

        return dateCalendar.getTime();
    }
    */
    private Date long2Date(long dateInMillis){
        Date date = new Date();
        date.setTime(dateInMillis);

        return date;
    }
}
