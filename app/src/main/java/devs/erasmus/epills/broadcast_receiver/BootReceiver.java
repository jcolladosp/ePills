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

           //String[] columns = new String[]{"startDate", "endDate", "medicine", "quantity", "alarmRequestCode", "isAlarmSet"};
           Cursor intakeCursor = db.query("intakemoment", null, null, null, null, null, null);

            for (intakeCursor.moveToFirst(); !intakeCursor.isAfterLast(); intakeCursor.moveToNext()) {
                long startDateMillis = intakeCursor.getLong(intakeCursor.getColumnIndex("startdate"));
                long endDateMillis = intakeCursor.getLong(intakeCursor.getColumnIndex("enddate"));
                int quantity = intakeCursor.getInt(intakeCursor.getColumnIndex("quantity"));
                int alarmRequestCode = intakeCursor.getInt(intakeCursor.getColumnIndex("alarmrequestcode"));
                long medicineId = intakeCursor.getLong(intakeCursor.getColumnIndex("medicineid"));
                int isOnce = intakeCursor.getInt(intakeCursor.getColumnIndex("isonce"));
                Cursor medicineCursor = db.query("medicine", null, "id = " + String.valueOf(medicineId), null, null, null, null);
                String medicineName = "placeholder name";

                if (medicineCursor.moveToFirst()) {
                    medicineName = medicineCursor.getString(medicineCursor.getColumnIndex("name"));
                }

                Date currentDate = Calendar.getInstance().getTime();

                //if the alarm is already passed, set it off as a one-time alarm!
                if(startDateMillis < System.currentTimeMillis()){
                    AlarmUtil.setAlarm(context, medicineName, quantity, currentDate, currentDate, alarmRequestCode);
                }

                //logic part to refresh startDate to the correct day/month/year if multi-time alarm
                if(isOnce==0){
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(startDateMillis);

                    Date startDate = calendar.getTime();
                    Date endDate = long2Date(endDateMillis);

                    while (calendar.getTimeInMillis() < System.currentTimeMillis()) {
                        calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 7);
                    }
                    Log.e("resetting alarm", String.valueOf(startDate) + "( " + String.valueOf(alarmRequestCode) + " )");
                    AlarmUtil.setAlarm(context, medicineName, quantity, startDate, endDate, alarmRequestCode);
                }

                medicineCursor.close();
            }
            intakeCursor.close();
        }

    }

    private Date long2Date(long dateInMillis){
        Date date = new Date();
        date.setTime(dateInMillis);

        return date;
    }
}
